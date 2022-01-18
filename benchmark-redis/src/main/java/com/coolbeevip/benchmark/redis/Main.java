package com.coolbeevip.benchmark.redis;

import com.coolbeevip.benchmark.redis.jmh.Ping;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Main {

  public static void main(String[] args) throws RunnerException {

    Options opt = new OptionsBuilder()
        .include(".*" + Ping.class.getSimpleName() + ".*")
        .forks(1)
        .build();

    new Runner(opt).run();
  }
}