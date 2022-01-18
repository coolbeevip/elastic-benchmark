package com.coolbeevip.benchmark.iotdb.select;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.coolbeevip.benchmark.common.UnitTools;
import com.coolbeevip.benchmark.iotdb.core.AbstractCommand;
import java.lang.invoke.MethodHandles;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Parameters(commandNames = {"select"})
public class SelectCommand extends AbstractCommand {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final AtomicLong timerCounter = new AtomicLong(0);
  private final AtomicLong requestCounter = new AtomicLong(0);
  private final AtomicLong requestSizeCounter = new AtomicLong(0);
  private final AtomicLong responseCounter = new AtomicLong(0);
  private final AtomicLong errorCounter = new AtomicLong(0);

  @Parameter(names = {"--timeout"})
  protected Integer timeout = 10_000;

  @Parameter(names = {"--sql"}, required = true)
  private String sql;

  public void execute() {
    try {
      for (int i = 0; i < this.threads; i++) {
        Session ss = createSession();
        sessions.add(ss);
      }

      int total = this.requests / this.threads;
      CountDownLatch concurrencyLatch = new CountDownLatch(threads);
      long beginTime = System.currentTimeMillis();
      for (int i = 0; i < threads; i++) {
        SelectCommandWorker worker = new SelectCommandWorker(concurrencyLatch, sessions.get(i), sql,
            timeout, total,
            timerCounter, requestCounter, requestSizeCounter, responseCounter, errorCounter);
        new Thread(worker).start();
      }
      concurrencyLatch.await();
      long second = timerCounter.get() / 1000;
      if (second <= 0) {
        second = 1;
      }
      log.info("==========================================");
      log.info(" IoTDB Batch Insert");
      log.info("----------------- params ------------------");
      log.info("requests: " + this.requests);
      log.info("threads: " + this.threads);
      log.info("----------------- output ------------------");
      log.info("succeed : " + (requests - this.errorCounter.get()));
      log.info("fails : " + this.errorCounter.get());
      log.info("total time: " + UnitTools.convertTime(System.currentTimeMillis() - beginTime));
      log.info("throughput request: " + (float) requests / (float) second + " ops/s");
      log.info("throughput request size: " + UnitTools.convertSize(
          (float) this.requestSizeCounter.get() / (float) second) + " ops/s");
      log.info("throughput response row size: " + (float) this.responseCounter.get() / (float) second + " ops/s");
      log.info("==========================================");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {
      sessions.forEach(s -> {
        if (s != null) {
          try {
            s.close();
          } catch (IoTDBConnectionException e) {
            log.error(e.getMessage(), e);
          }
        }
      });
    }
  }

  private String genID() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}