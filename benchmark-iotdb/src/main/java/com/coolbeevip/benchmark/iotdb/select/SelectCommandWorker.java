package com.coolbeevip.benchmark.iotdb.select;


import java.lang.invoke.MethodHandles;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.session.SessionDataSet;
import org.apache.iotdb.tsfile.read.common.RowRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectCommandWorker implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final Session session;
  private final String sql;
  private final int total;
  private final int timeout;
  private final AtomicLong timerCounter;
  private final AtomicLong requestSizeCounter;
  private final AtomicLong errorCounter;
  private final CountDownLatch concurrencyLatch;
  private final AtomicLong requestCounter;
  private final AtomicLong responseSizeCounter;

  public SelectCommandWorker(CountDownLatch concurrencyLatch, Session session, String sql,
      int timeout, int total,
      AtomicLong timerCounter, AtomicLong requestCounter, AtomicLong requestSizeCounter,
      AtomicLong responseSizeCounter, AtomicLong errorCounter) {
    this.timerCounter = timerCounter;
    this.requestCounter = requestCounter;
    this.requestSizeCounter = requestSizeCounter;
    this.responseSizeCounter = responseSizeCounter;
    this.errorCounter = errorCounter;
    this.concurrencyLatch = concurrencyLatch;
    this.session = session;
    this.sql = sql;
    this.total = total;
    this.timeout = timeout;
  }

  @Override
  public void run() {
    try {
      for (int row = 0; row < total; row++) {
        requestSizeCounter.addAndGet(sql.length());
        this.requestCounter.incrementAndGet();
        long beginTime = System.currentTimeMillis();
        try {
          SessionDataSet dataSet = session.executeQueryStatement(sql, timeout);
          long counter = 0;
          while (dataSet.hasNext()) {
            RowRecord record = dataSet.next();
            counter++;
          }
          this.responseSizeCounter.addAndGet(counter);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          errorCounter.incrementAndGet();
        } finally {
          timerCounter.addAndGet(System.currentTimeMillis() - beginTime);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {
      concurrencyLatch.countDown();
    }
  }
}