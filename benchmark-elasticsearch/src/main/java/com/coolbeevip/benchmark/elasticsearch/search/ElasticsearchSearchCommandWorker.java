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

import java.lang.invoke.MethodHandles;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.HttpStatus;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticsearchSearchCommandWorker implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final AtomicLong timerCounter;
  private final AtomicLong errorCounter;
  private final AtomicLong requestSizeCounter;
  private final AtomicLong responseSizeCounter;
  private final CountDownLatch concurrencyLatch;
  private final AtomicLong requestCounter;
  private final String indexName;
  private final RestHighLevelClient client;
  private final int batch;
  private final String json;

  public ElasticsearchSearchCommandWorker(
      CountDownLatch concurrencyLatch,
      String indexName,
      RestHighLevelClient client,
      String json,
      int batch,
      AtomicLong timerCounter,
      AtomicLong requestCounter,
      AtomicLong errorCounter,
      AtomicLong requestSizeCounter,
      AtomicLong responseSizeCounter) {
    this.timerCounter = timerCounter;
    this.requestCounter = requestCounter;
    this.errorCounter = errorCounter;
    this.requestSizeCounter = requestSizeCounter;
    this.responseSizeCounter = responseSizeCounter;
    this.concurrencyLatch = concurrencyLatch;
    this.indexName = indexName;
    this.client = client;
    this.json = json;
    this.batch = batch;
  }

  @Override
  public void run() {
    try {
      for (int i = 0; i < this.batch; i++) {
        try {
          requestSizeCounter.addAndGet(json.length());
          long beginTime = System.currentTimeMillis();
          Request request = new Request("POST", indexName + "/_search");
          request.setJsonEntity(json);
          Response response = client.getLowLevelClient().performRequest(request);
          timerCounter.addAndGet(System.currentTimeMillis()-beginTime);
          this.requestCounter.incrementAndGet();
          responseSizeCounter.addAndGet(response.getEntity().getContentLength());
          if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            log.info("search index [{}} {}] ok", indexName, this.requestCounter.get());
          } else {
            errorCounter.incrementAndGet();
            log.error("search index [{}}] fails, status {}", indexName,
                response.getStatusLine().getStatusCode());
          }
          Thread.sleep(10);
        } catch (Exception e) {
          log.error("search index [{}}] fails {}", e);
          errorCounter.incrementAndGet();
          throw new RuntimeException(e);
        }
      }
      log.info("search [{}] counter=[{}]", this.indexName, this.batch);
    } finally {
      concurrencyLatch.countDown();
    }
  }
}