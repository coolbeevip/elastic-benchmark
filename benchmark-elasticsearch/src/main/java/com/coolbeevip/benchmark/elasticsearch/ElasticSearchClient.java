package com.coolbeevip.benchmark.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;

public class ElasticSearchClient {

  final RestHighLevelClient client;

  public ElasticSearchClient(RestHighLevelClient client) {
    this.client = client;
  }

  public RestHighLevelClient getClient() {
    return client;
  }
}