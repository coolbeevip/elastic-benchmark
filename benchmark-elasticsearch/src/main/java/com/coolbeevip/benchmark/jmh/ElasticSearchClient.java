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