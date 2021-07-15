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