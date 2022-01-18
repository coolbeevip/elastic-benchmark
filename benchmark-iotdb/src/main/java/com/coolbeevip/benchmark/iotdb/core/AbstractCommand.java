package com.coolbeevip.benchmark.iotdb.core;

import com.beust.jcommander.Parameter;
import java.util.ArrayList;
import java.util.List;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.session.Session;

public abstract class AbstractCommand {
  private SessionFactory factory;
  @Parameter(names = {"--host", "-H"}, required = true)
  protected List<String> hosts;
  @Parameter(names = {"--requests"})
  protected Integer requests = 1;
  @Parameter(names = {"--threads"})
  protected Integer threads = 1;
  protected List<Session> sessions = new ArrayList<>();

  public synchronized Session createSession() throws IoTDBConnectionException {
    if(factory==null){
      factory = new SessionFactory(this.hosts);
    }
    Session session = factory.createSession();
    session.open();
    return session;
  }
}