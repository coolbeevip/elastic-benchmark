package com.coolbeevip.benchmark.elasticsearch.importcsv;

import com.coolbeevip.benchmark.common.UnitTools;
import com.coolbeevip.benchmark.elasticsearch.ElasticSearchClient;
import com.google.common.collect.Lists;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.client.RequestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchImportCsvWorker {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  final String name;
  final String id;
  final int threads;
  final int batch;
  final String templateFile;
  final String csvFile;
  final boolean completed;
  private final ElasticSearchClient searchClient;

  private Template template;
  private List<Path> csvFiles;
  private long cvsRecodeTotal;
  private long cvsSizeTotal;
  private AtomicLong cvsParseJsonTimeCounter = new AtomicLong(0);
  private AtomicLong cvsRecodeTotalProcessCounter = new AtomicLong(0);
  private AtomicLong errorCounter = new AtomicLong(0);

  public ElasticSearchImportCsvWorker(ElasticSearchClient searchClient,
      String name, String id, String templateFile, String csvFile, int threads, int batch,
      boolean completed) {
    this.searchClient = searchClient;
    this.name = name;
    this.id = id;
    this.templateFile = templateFile;
    this.csvFile = csvFile;
    this.threads = threads;
    this.batch = batch;
    this.completed = completed;
  }

  public void run() {
    try {
      initTemplate();
      initCsvData();
      execute();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private void execute() throws InterruptedException {
    List<List<Path>> subPaths = Lists
        .partition(this.csvFiles,
            this.csvFiles.size() > this.threads ? this.csvFiles.size() / this.threads : 1);
    long beginTime = System.currentTimeMillis();
    CountDownLatch concurrencyLatch = new CountDownLatch(subPaths.size());
    for (int i = 0; i < subPaths.size(); i++) {
      ElasticSearchImportCsvWorkExecute execute = new ElasticSearchImportCsvWorkExecute(
          searchClient,
          name,
          id,
          batch,
          completed,
          concurrencyLatch,
          template,
          subPaths.get(i),
          this.cvsRecodeTotal,
          this.cvsRecodeTotalProcessCounter, this.cvsParseJsonTimeCounter, this.errorCounter);
      new Thread(execute).start();
    }
    concurrencyLatch.await();
    long endTime = System.currentTimeMillis();
    printReport(endTime - beginTime);
    refreshIndex(this.name);
  }

  private void printReport(long millisecond) {
    long second = millisecond / 1000;
    if (second <= 0) {
      second = 1;
    }
    log.info("==========================================");
    log.info(" ElasticSearch Import CSV");
    log.info("----------------- params ------------------");
    log.info("index: " + this.name);
    log.info("id: " + this.id);
    log.info("csv count: " + this.cvsRecodeTotal);
    log.info("csv size: " + UnitTools.convertSize(this.cvsSizeTotal));
    log.info("batch: " + this.batch);
    log.info("threads: " + this.threads);
    log.info("----------------- output ------------------");
    log.info("succeed : " + (this.cvsRecodeTotal - this.errorCounter.get()));
    log.info("fails : " + this.errorCounter.get());
    log.info("total time: " + UnitTools.convertTime(millisecond));
    log.info("parse cvs time: " + UnitTools.convertTime(this.cvsParseJsonTimeCounter.get()));
    log.info("average size per: " + UnitTools.convertSize(this.cvsSizeTotal / this.cvsRecodeTotal));
    log.info("throughput records: " + this.cvsRecodeTotal / second + " ops/s");
    log.info("throughput size: " + UnitTools.convertSize((float) this.cvsSizeTotal / (float) second)
        + " ops/s");
    log.info("==========================================");
  }

  private void refreshIndex(String indexName) {
    try {
      long refreshBegin = System.currentTimeMillis();
      log.info("refresh [" + indexName + "] ...");
      RefreshRequest request = new RefreshRequest(indexName);
      RefreshResponse refreshResponse = searchClient.getClient().indices()
          .refresh(request, RequestOptions.DEFAULT);
      long refreshEnd = System.currentTimeMillis();
      log.info(
          "refresh status " + refreshResponse.getStatus() + " time " + (refreshEnd - refreshBegin)
              + "ms");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void initCsvData() throws IOException {
    log.info("统计 CSV 文件信息 ... ");
    ElasticSearchImportCsvDiscovery discovery = new ElasticSearchImportCsvDiscovery();
    discovery.discovery(this.csvFile).ifPresent(csv -> {
      this.csvFiles = csv;
    });

    long beginTime = System.currentTimeMillis();
    // 统计数据信息
    this.cvsRecodeTotal = this.csvFiles.stream().map(cvs -> {
      try {
        return Files.lines(cvs).count();
      } catch (IOException e) {
        e.printStackTrace();
        return 0l;
      }
    }).reduce(0l, Long::sum);

    this.cvsSizeTotal = this.csvFiles.stream().map(csv -> csv.toFile().length())
        .reduce(0l, Long::sum);

    long endTime = System.currentTimeMillis();
    if (this.cvsRecodeTotal > 0) {
      log.info(
          "CSV 文件 " + this.csvFiles.size() + "个 总计[" + this.cvsRecodeTotal + "]行 " + UnitTools
              .convertSize(this.cvsSizeTotal) + " 分析耗时 " + (endTime - beginTime) + "ms");
    } else {
      log.info("无数据文件 " + this.csvFile);
      System.exit(0);
    }
  }

  private void initTemplate() throws IOException {
    File tplFile = new File(this.templateFile);
    if (tplFile.exists()) {
      Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
      configuration.setDirectoryForTemplateLoading(tplFile.getParentFile());
      template = configuration.getTemplate(tplFile.getName());
    } else {
      log.error("数据模版文件 {} 不存在", this.templateFile);
    }
  }
}