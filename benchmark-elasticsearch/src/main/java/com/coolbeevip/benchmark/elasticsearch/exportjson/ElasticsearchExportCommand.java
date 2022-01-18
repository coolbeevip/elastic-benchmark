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
package com.coolbeevip.benchmark.elasticsearch.exportjson;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.coolbeevip.benchmark.common.args.AbstractElasticSearchArgs;
import com.coolbeevip.benchmark.elasticsearch.ElasticSearchClient;
import com.coolbeevip.benchmark.elasticsearch.ElasticSearchClientFactory;
import com.coolbeevip.benchmark.elasticsearch.importcsv.ElasticSearchImportCsvWorker;

@Parameters(commandNames = {"export"})
public class ElasticsearchExportCommand extends AbstractElasticSearchArgs {

  @Parameter(names = {"--name"}, required = true)
  private String name;

  @Parameter(names = {"--output"}, required = true)
  private String output;


  public void execute() {
    ElasticSearchClientFactory client = new ElasticSearchClientFactory();
    ElasticSearchClient searchClient = client.create(this.getHosts());
    ElasticSearchExportWorker worker = new ElasticSearchExportWorker(searchClient, name);
    worker.run();
  }
}