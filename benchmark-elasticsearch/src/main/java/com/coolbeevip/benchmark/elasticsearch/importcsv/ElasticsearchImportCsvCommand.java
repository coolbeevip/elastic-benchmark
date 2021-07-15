/**
 * Copyright © 2020 Lei Zhang (zhanglei@apache.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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