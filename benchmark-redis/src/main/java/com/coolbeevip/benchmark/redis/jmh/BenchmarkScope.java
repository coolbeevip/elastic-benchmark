package com.coolbeevip.benchmark.redis.jmh;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import java.lang.invoke.MethodHandles;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@State(Scope.Benchmark)
public class BenchmarkScope {

  static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  final RedisClient redisClient;
  final StatefulRedisConnection<String, String> connection;

  public BenchmarkScope() {
    this.redisClient = RedisClient.create("redis://10.1.207.182:6379/0");
    this.connection = redisClient.connect();
  }
}