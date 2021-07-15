package com.coolbeevip.benchmark.common;

import com.beust.jcommander.JCommander;
import com.coolbeevip.benchmark.common.args.AbstractElasticSearchArgs;

public class ElasticBenchmarkFactory<T extends AbstractElasticSearchArgs> {

  public T parseArgs(String[] args, Class<T> t) {
    try {
      T instance = t.newInstance();
      JCommander cmd = JCommander.newBuilder()
          .addObject(instance)
          .build();
      cmd.parse(args);
      return instance;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}