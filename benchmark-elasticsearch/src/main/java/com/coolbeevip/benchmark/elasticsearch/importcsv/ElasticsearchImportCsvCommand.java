package com.coolbeevip.benchmark.elasticsearch.importcsv;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.coolbeevip.benchmark.common.args.AbstractElasticSearchArgs;
import com.coolbeevip.benchmark.elasticsearch.ElasticSearchClient;
import com.coolbeevip.benchmark.elasticsearch.ElasticSearchClientFactory;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Parameters(commandNames = {"importcsv"})
public class ElasticsearchImportCsvCommand extends AbstractElasticSearchArgs {

  @Parameter(names = {"--name"}, required = true)
  private String name;

  @Parameter(names = {"--id"})
  protected String id;

  @Parameter(names = {"--batch"})
  private Integer batch = 1000;

  @Parameter(names = {"--threads"})
  private Integer threads = 4;

  @Parameter(names = {"--template"}, required = true)
  private String template;

  @Parameter(names = {"--csv"}, required = true)
  private String csv;

  @Parameter(names = {"--completed"})
  protected boolean completed;

  public void execute() {
    ElasticSearchClientFactory client = new ElasticSearchClientFactory();
    ElasticSearchClient searchClient = client.create(this.getHosts());
    ElasticSearchImportCsvWorker worker = new ElasticSearchImportCsvWorker(searchClient, name, id,
        template, csv, threads, batch, completed);
    worker.run();
  }
}