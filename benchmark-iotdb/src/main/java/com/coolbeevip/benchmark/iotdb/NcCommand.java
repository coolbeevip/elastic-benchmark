package com.coolbeevip.benchmark.iotdb;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.coolbeevip.benchmark.common.UnitTools;
import com.coolbeevip.benchmark.iotdb.core.SessionFactory;
import com.coolbeevip.benchmark.iotdb.devices.CardDevice;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Parameters(commandNames = {"insert"})
public class NcCommand {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final AtomicLong timerCounter = new AtomicLong(0);
  private final AtomicLong requestCounter = new AtomicLong(0);
  private final AtomicLong requestSizeCounter = new AtomicLong(0);
  private final AtomicLong responseSizeCounter = new AtomicLong(0);
  @Parameter(names = {"--host", "-H"}, required = true)
  protected List<String> hosts;
  @Parameter(names = {"--pathPrefix"})
  private String pathPrefix = "root.benchmark";
  @Parameter(names = {"--storageGroup"})
  private String storageGroup = "省份.城市";
  @Parameter(names = {"--requests"})
  private Integer requests = 1;
  @Parameter(names = {"--threads"})
  private Integer threads = 1;
  @Parameter(names = {"--batchSize"})
  private Integer batchSize = 2000;
  private List<Session> sessions = new ArrayList<>();
  private List<CardDevice> cardDevices = new ArrayList<>();

  public void execute() {
    try {
      SessionFactory factory = new SessionFactory(this.hosts);
      for (int i = 0; i < this.threads; i++) {
        Session ss = factory.createSession();
        ss.open();

        String storageGroupName =  pathPrefix + "." + storageGroup + i;

        CardDevice card = CardDevice.builder().session(ss)
            .storageGroup(storageGroupName)
            .addPath("SITE-" + genID())
            .addPath("VENDORS-" + genID())
            .addPath("ELEMENT-" + genID())
            .addPath("CARD")
            .addPath("CARD-" + genID())
            .addMeasurement("temperature", TSDataType.DOUBLE, TSEncoding.GORILLA,
                CompressionType.SNAPPY, null, null, null, "板卡温度").build();
        card.initStorageGroup();
        card.initTimeSeries();
        cardDevices.add(card);
        sessions.add(ss);
      }

      int total = this.requests / this.threads;
      CountDownLatch concurrencyLatch = new CountDownLatch(threads);
      for (int i = 0; i < threads; i++) {
        NcCommandWorker worker = new NcCommandWorker(concurrencyLatch, cardDevices.get(i), total,
            batchSize,
            timerCounter, requestCounter, requestSizeCounter, responseSizeCounter);
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
      log.info("batchSize: " + this.batchSize);
      log.info("threads: " + this.threads);
      log.info("----------------- output ------------------");
      log.info("total time: " + UnitTools.convertTime(timerCounter.get()));
      log.info("throughput request: " + (float) requests / (float) second + " ops/s");
      log.info("throughput request size: " + UnitTools.convertSize(
          (float) this.requestSizeCounter.get() / (float) second) + " ops/s");
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