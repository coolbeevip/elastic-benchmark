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
package com.coolbeevip.benchmark.elasticsearch;

import java.util.List;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticSearchClientFactory {

  public ElasticSearchClient create(List<String> hosts) {
    HttpHost[] address = hosts.stream().map(h -> {
      String[] hp = h.split(":");
      return new HttpHost(hp[0], Integer.valueOf(hp[1]), "http");
    }).toArray(HttpHost[]::new);
    RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(address)
        .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
            .setKeepAliveStrategy((response, context) -> 2 * 60 * 1000)
            .setMaxConnPerRoute(10))
        .setRequestConfigCallback(requestConfigBuilder ->
            requestConfigBuilder
                .setConnectTimeout(60 * 1000)
                .setConnectionRequestTimeout(60 * 1000)
                .setSocketTimeout(60 * 1000)));
    return new ElasticSearchClient(client);
  }
}