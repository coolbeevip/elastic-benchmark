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
package com.coolbeevip.benchmark.jmh;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ElasticSearchClient {

  final RestHighLevelClient client;

  public ElasticSearchClient() {
    this.client = new RestHighLevelClient(
        RestClient.builder(
            new HttpHost("10.1.207.180", 9200, "http"),
            new HttpHost("10.1.207.181", 9200, "http"),
            new HttpHost("10.1.207.182", 9200, "http")
        ));
  }
}