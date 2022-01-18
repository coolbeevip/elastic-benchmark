package com.coolbeevip.benchmark.redis.jmh;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

public class Ping {

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @Warmup(iterations = 2, time = 10)
  @Threads(4)
  @Measurement(iterations = 4, time = 10, timeUnit = TimeUnit.SECONDS)
  @OutputTimeUnit(TimeUnit.SECONDS)
  public void ping(BenchmarkScope benchmarkScope) {
    benchmarkScope.connection.sync().ping();
  }

  @Benchmark
  @BenchmarkMode(Mode.Throughput)
  @Warmup(iterations = 10)
  @Threads(4)
  @Measurement(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
  @OutputTimeUnit(TimeUnit.SECONDS)
  public void pingAsync(BenchmarkScope benchmarkScope) {
    benchmarkScope.connection.async().ping();
  }
}