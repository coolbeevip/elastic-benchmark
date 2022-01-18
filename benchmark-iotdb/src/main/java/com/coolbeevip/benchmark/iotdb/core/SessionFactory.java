package com.coolbeevip.benchmark.iotdb.core;

import java.util.List;
import org.apache.iotdb.session.Session;

public class SessionFactory {

  private final List<String> nodeUrls;

  public SessionFactory(List<String> nodeUrls) {
    this.nodeUrls = nodeUrls;
  }

  public Session createSession(){
    Session session = new Session.Builder()
        .nodeUrls(this.nodeUrls)
        .build();
    return session;
  }
}