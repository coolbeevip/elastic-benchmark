/**
 * Copyright © 2020 Lei Zhang (zhanglei@apache.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.coolbeevip.benchmark.elasticsearch.importcsv;

import com.coolbeevip.benchmark.elasticsearch.ElasticSearchClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Template;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchImportCsvWorkExecute implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  final String name;
  final String id;
  final int batch;
  final boolean completed;
  final long totalRecords;
  final AtomicLong cvsRecodeTotalProcessCounter;
  final AtomicLong cvsParseJsonTimeCounter;
  final AtomicLong errorCounter;

  final ElasticSearchClient searchClient;
  final CountDownLatch concurrencyLatch;
  final Template template;
  final List<Path> paths;
  final ObjectMapper mapper = new ObjectMapper();

  public ElasticSearchImportCsvWorkExecute(ElasticSearchClient searchClient,
      String name,
      String id,
      int batch,
      boolean completed,
      CountDownLatch concurrencyLatch,
      Template template, List<Path> paths,
      long totalRecords,
      AtomicLong cvsRecodeTotalProcessCounter,
      AtomicLong cvsParseJsonTimeCounter,
      AtomicLong errorCounter) {
    this.name = name;
    this.id = id;
    this.batch = batch;
    this.completed = completed;
    this.searchClient = searchClient;
    this.concurrencyLatch = concurrencyLatch;
    this.template = template;
    this.paths = paths;
    this.totalRecords = totalRecords;
    this.cvsRecodeTotalProcessCounter = cvsRecodeTotalProcessCounter;
    this.cvsParseJsonTimeCounter = cvsParseJsonTimeCounter;
    this.errorCounter = errorCounter;
  }

  @Override
  public void run() {
    try (RandomAccessFile stream = new RandomAccessFile("error.log", "rw")) {
      FileChannel channel = stream.getChannel();
      write(channel);
      concurrencyLatch.countDown();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  private void write(final FileChannel channel) {
    try {
      AtomicLong runDown = new AtomicLong();
      this.paths.forEach(csv -> {
        List<IndexRequest> indexRequests = new ArrayList<>();
        try (Stream inputStream = Files.lines(csv, StandardCharsets.UTF_8)) {
          inputStream.forEach(record -> {
            try {
              long beninParseJson = System.currentTimeMillis();
              Map<String, Object> params = parseCsvRecord(record.toString());
              StringWriter stringWriter = new StringWriter();
              template.process(params, stringWriter);
              this.cvsParseJsonTimeCounter.addAndGet(System.currentTimeMillis() - beninParseJson);
              String json = stringWriter.toString();
              if (isJSONValid(json)) {
                if (indexRequests.size() < this.batch) {
                  indexRequests
                      .add(indexRequest(this.name,
                          this.id == null ? null : params.get("C" + this.id).toString(),
                          stringWriter.toString()));
                }
                if (indexRequests.size() == this.batch) {
                  bulkAsync(channel, runDown, indexRequests);
                  indexRequests.clear();
                }
              } else {
                error(channel, json);
              }
            } catch (Exception e) {
              error(channel, record.toString());
              e.printStackTrace();
            }
          });
          if (!indexRequests.isEmpty()) {
            bulkAsync(channel, runDown, indexRequests);
          }
          if (this.completed) {
            csv.toFile().renameTo(new File(
                csv.getParent() + File.separator + csv.getFileName().toString()
                    .substring(0, csv.getFileName().toString().lastIndexOf(".")) + ".COMPLETED"));
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
      while (runDown.get() != 0) {
        Thread.sleep(100);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Map<String, Object> parseCsvRecord(String record) {
    Map<String, Object> params = new HashMap<>();
    String[] recordArrays = CsvSplit.split(record);
    IntStream.range(0, recordArrays.length)
        .forEach(idx -> {
          if (recordArrays[idx].trim().length() > 0) {
            params.put("C" + idx, recordArrays[idx]);
          }
        });
    return params;
  }

  private void bulkAsync(final FileChannel channel, final AtomicLong runDown,
      List<IndexRequest> indexRequests) {
    log.info("bulk batch {}", indexRequests.size());
    long beginTime = System.currentTimeMillis();
    runDown.addAndGet(indexRequests.size());

    BulkRequest bulkRequest = new BulkRequest();
    indexRequests.forEach(i -> bulkRequest.add(i));
    this.searchClient.getClient()
        .bulkAsync(bulkRequest, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
          @Override
          public void onResponse(BulkResponse bulkResponse) {
            long endTime = System.currentTimeMillis();
            cvsRecodeTotalProcessCounter.addAndGet(bulkRequest.numberOfActions());
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            String info = "";
            if (bulkResponse.status() == RestStatus.OK) {
              if (bulkResponse.hasFailures()) {
                Arrays.stream(bulkResponse.getItems()).forEach(p -> {
                  error(channel, p.getId());
                });
                info = String.format("[%s] insert %d to [%s] time [%d] process [%s] ERROR %s",
                    Thread.currentThread().getName(),
                    bulkRequest.numberOfActions(),
                    name,
                    endTime - beginTime,
                    numberFormat.format(
                        (float) cvsRecodeTotalProcessCounter.get() / (float) totalRecords * 100)
                        + "%",
                    bulkResponse.buildFailureMessage());
              } else {
                info = String.format("[%s] insert %d to [%s] time [%d] process [%s]",
                    Thread.currentThread().getName(),
                    bulkRequest.numberOfActions(),
                    name,
                    endTime - beginTime,
                    numberFormat.format(
                        (float) cvsRecodeTotalProcessCounter.get() / (float) totalRecords * 100)
                        + "%");

              }
            } else {
              info = String
                  .format("[%s] insert %d to [%s] time [%d] process [%s] ERROR status=[%s] %s",
                      Thread.currentThread().getName(),
                      bulkRequest.numberOfActions(),
                      name,
                      endTime - beginTime,
                      numberFormat.format(
                          (float) cvsRecodeTotalProcessCounter.get() / (float) totalRecords * 100)
                          + "%",
                      bulkResponse.status(),
                      bulkResponse.buildFailureMessage());
            }
            runDown.addAndGet(0 - bulkRequest.numberOfActions());
            log.info(info);
          }

          @Override
          public void onFailure(Exception e) {
            runDown.addAndGet(0 - bulkRequest.numberOfActions());
            e.printStackTrace();
          }
        });

    indexRequests.clear();
  }

  private IndexRequest indexRequest(String indexName, String id, String json) {
    IndexRequest request = new IndexRequest(indexName);
    if (id != null) {
      request.id(id);
    }
    request.timeout(TimeValue.timeValueMinutes(2));
    request.source(json, XContentType.JSON);
    return request;
  }

  private boolean isJSONValid(String json) {
    try {
      mapper.readTree(json);
      return true;
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  private void error(final FileChannel channel, String error) {
    try {
      this.errorCounter.incrementAndGet();
      String line = error + System.lineSeparator();
      byte[] strBytes = line.getBytes();
      ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
      buffer.put(strBytes);
      buffer.flip();
      channel.write(buffer);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}