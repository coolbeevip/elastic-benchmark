package com.coolbeevip.benchmark.common.args;

import com.beust.jcommander.Parameter;
import java.util.List;

public abstract class AbstractElasticSearchArgs {

  @Parameter(names = "--help", help = true)
  protected boolean help;

  public boolean isHelp() {
    return help;
  }

  @Parameter(
      names = {"--host", "-H"}
  )
  protected List<String> hosts;

  @Parameter(
      names = {"--verify"}
  )
  protected boolean verify;

  public List<String> getHosts() {
    return hosts;
  }

  public boolean isVerify() {
    return verify;
  }
}