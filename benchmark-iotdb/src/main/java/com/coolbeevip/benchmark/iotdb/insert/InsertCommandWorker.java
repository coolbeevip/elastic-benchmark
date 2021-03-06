package com.coolbeevip.benchmark.iotdb.insert;


import com.coolbeevip.benchmark.iotdb.core.DeviceEntity;
import com.coolbeevip.benchmark.iotdb.core.MeasurementValue;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertCommandWorker implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final AtomicLong timerCounter;
  private final AtomicLong requestSizeCounter;
  private final CountDownLatch concurrencyLatch;
  private final AtomicLong requestCounter;
  private final int total;
  private final int batchSize;
  private final DeviceEntity device;

  public InsertCommandWorker(CountDownLatch concurrencyLatch, DeviceEntity device, int total, int batchSize,
      AtomicLong timerCounter, AtomicLong requestCounter, AtomicLong requestSizeCounter,
      AtomicLong responseSizeCounter) {
    this.timerCounter = timerCounter;
    this.requestCounter = requestCounter;
    this.requestSizeCounter = requestSizeCounter;
    this.concurrencyLatch = concurrencyLatch;
    this.device = device;
    this.total = total;
    this.batchSize = batchSize;
  }

  @Override
  public void run() {

    long timestamp = System.currentTimeMillis();
    List<MeasurementValue> measurementValues = new ArrayList<>();
    for (int row = 0; row < total; row++) {
      int temperature = 50 + (int) Math.random() * 51;
      measurementValues.add(new MeasurementValue(timestamp, temperature));
      timestamp = timestamp + 5000;
    }
    try {
      requestSizeCounter.addAndGet(this.device.getDevicePath().length()+(8+8)*batchSize);
      long beginTime = System.currentTimeMillis();
      this.device.batchInsert("temp", measurementValues, batchSize);
      timerCounter.addAndGet(System.currentTimeMillis() - beginTime);
      this.requestCounter.incrementAndGet();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {
      concurrencyLatch.countDown();
    }
  }
}