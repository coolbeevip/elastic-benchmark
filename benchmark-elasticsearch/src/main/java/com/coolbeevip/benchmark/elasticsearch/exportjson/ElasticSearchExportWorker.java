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
package com.coolbeevip.benchmark.elasticsearch.exportjson;

import com.coolbeevip.benchmark.elasticsearch.ElasticSearchClient;
import java.lang.invoke.MethodHandles;
import java.text.NumberFormat;
import java.util.concurrent.atomic.AtomicLong;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchExportWorker {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  final String name;
  final int size;
  private final ElasticSearchClient searchClient;
  private AtomicLong exportCounter = new AtomicLong(0);

  public ElasticSearchExportWorker(ElasticSearchClient searchClient,
      String name) {
    this.searchClient = searchClient;
    this.name = name;
    this.size = 10000;
  }

  public void run() {
    try {

      NumberFormat numberFormat = NumberFormat.getInstance();
      numberFormat.setMaximumFractionDigits(2);

      long beginTime = System.currentTimeMillis();
      final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
      SearchRequest searchRequest = new SearchRequest(this.name);
      searchRequest.scroll(scroll);
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
      searchSourceBuilder.size(size);
      searchSourceBuilder.query(QueryBuilders.matchAllQuery());
      searchRequest.source(searchSourceBuilder);

      SearchResponse searchResponse = this.searchClient.getClient()
          .search(searchRequest, RequestOptions.DEFAULT);
      String scrollId = searchResponse.getScrollId();
      SearchHit[] searchHits = searchResponse.getHits().getHits();
      long total = searchResponse.getHits().getTotalHits().value;
      exportCounter.addAndGet(searchResponse.getHits().getHits().length);
      log.info("total {}", total);
      while (searchHits != null && searchHits.length > 0) {
        long beginBatchTime = System.currentTimeMillis();
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(scroll);
        searchResponse = this.searchClient.getClient()
            .scroll(scrollRequest, RequestOptions.DEFAULT);
        scrollId = searchResponse.getScrollId();
        searchHits = searchResponse.getHits().getHits();
        exportCounter.addAndGet(searchHits.length);
        log.info("batch size {}, time {}, process {}%", searchHits.length,
            System.currentTimeMillis() - beginBatchTime,
            numberFormat.format((float) exportCounter.get() / (float) total * 100));
      }

      ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
      clearScrollRequest.addScrollId(scrollId);
      ClearScrollResponse clearScrollResponse = this.searchClient.getClient()
          .clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
      boolean succeeded = clearScrollResponse.isSucceeded();
      log.info("time {}", System.currentTimeMillis() - beginTime);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }
}