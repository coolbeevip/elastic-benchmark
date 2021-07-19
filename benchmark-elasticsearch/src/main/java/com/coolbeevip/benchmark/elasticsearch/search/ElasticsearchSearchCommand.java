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
package com.coolbeevip.benchmark.elasticsearch.search;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.coolbeevip.benchmark.common.UnitTools;
import com.coolbeevip.benchmark.common.args.AbstractElasticSearchArgs;
import com.coolbeevip.benchmark.elasticsearch.ElasticSearchClient;
import com.coolbeevip.benchmark.elasticsearch.ElasticSearchClientFactory;
import com.coolbeevip.benchmark.elasticsearch.importcsv.ElasticSearchImportCsvWorkExecute;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Parameters(commandNames = {"search"})
public class ElasticsearchSearchCommand extends AbstractElasticSearchArgs {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private AtomicLong timerCounter = new AtomicLong(0);
  private AtomicLong requestCounter = new AtomicLong(0);
  private AtomicLong errorCounter = new AtomicLong(0);
  private AtomicLong requestSizeCounter = new AtomicLong(0);
  private AtomicLong responseSizeCounter = new AtomicLong(0);

  @Parameter(names = {"--name"}, description = "Index Name", required = true)
  private String name;

  @Parameter(names = {"--requests"})
  private Integer requests = 1;

  @Parameter(names = {"--threads"})
  private Integer threads = 1;

  @Parameter(names = {"--file"}, description = "Query JSON file", required = true)
  private String file;

  public void execute() throws InterruptedException, IOException {
    ElasticSearchClientFactory client = new ElasticSearchClientFactory();
    ElasticSearchClient searchClient = client.create(this.getHosts());

    Path path = Paths.get(file);
    if (path.toFile().exists()) {
      Stream<String> lines = Files.lines(path);
      String json = lines.collect(Collectors.joining("\n"));
      int batch = this.requests / this.threads;

      CountDownLatch concurrencyLatch = new CountDownLatch(threads);
      for (int i = 0; i < threads; i++) {
        ElasticsearchSearchCommandWorker worker = new ElasticsearchSearchCommandWorker(
            concurrencyLatch,
            name, searchClient.getClient(), json, batch, timerCounter, requestCounter, errorCounter, requestSizeCounter,
            responseSizeCounter);
        new Thread(worker).start();
      }
      concurrencyLatch.await();
      long second = timerCounter.get() / 1000;
      if (second <= 0) {
        second = 1;
      }
      log.info("==========================================");
      log.info(" ElasticSearch Search");
      log.info("----------------- params ------------------");
      log.info("index: " + this.name);
      log.info("requests: " + this.requests);
      log.info("threads: " + this.threads);
      log.info("----------------- output ------------------");
      log.info("succeed : " + (requests - this.errorCounter.get()));
      log.info("fails : " + this.errorCounter.get());
      log.info("total time: " + UnitTools.convertTime(timerCounter.get()));
      log.info("throughput request: " + (float)requests / (float) second + " ops/s");
      log.info("throughput request size: " + UnitTools
          .convertSize((float) this.requestSizeCounter.get() / (float) second)
          + " ops/s");
      log.info("throughput response size: " + UnitTools
          .convertSize((float) this.responseSizeCounter.get() / (float) second)
          + " ops/s");
      log.info("==========================================");
    } else {
      throw new RuntimeException("file " + file + " exists");
    }
  }
}