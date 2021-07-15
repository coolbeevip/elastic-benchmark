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
package com.coolbeevip.benchmark.elasticsearch.template;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.coolbeevip.benchmark.common.args.AbstractElasticSearchArgs;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Parameters(commandNames = {"template"})
public class ElasticsearchTemplateCommand extends AbstractElasticSearchArgs {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Parameter(names = {"--name"}, description = "Index Name", required = true)
  private String name;

  @Parameter(names = {"--file"}, description = "Index Template JSON file", required = true)
  private String file;

  @Parameter(names = {"-D"})
  private boolean delete;

  public void execute() {
    HttpHost[] hosts = this.getHosts().stream().map(h -> {
      String[] hp = h.split(":");
      return new HttpHost(hp[0], Integer.valueOf(hp[1]), "http");
    }).toArray(HttpHost[]::new);

    RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(hosts));
    if (this.delete) {
      deleteIndex(client, this.name);
    }
    createIndex(client, this.name, this.file);
  }

  private void deleteIndex(RestHighLevelClient client, String name) {
    try {
      DeleteIndexRequest request = new DeleteIndexRequest(name);
      AcknowledgedResponse response = client.indices()
          .delete(request, RequestOptions.DEFAULT);
      if (response.isAcknowledged()) {
        log.info("delete index [{}}] {}", name, response.isAcknowledged()?"ok":"fails");
      } else {
        log.error("delete index [{}}] {}", name, response.isAcknowledged());
      }
    } catch (Exception e) {
      log.error("delete index [{}}] fails {}", e);
    }
  }

  private void createIndex(RestHighLevelClient client, String indexName, String file) {
    try {
      Path path = Paths.get(file);
      if (path.toFile().exists()) {
        Stream<String> lines = Files.lines(path);
        String data = lines.collect(Collectors.joining("\n"));
        Request request = new Request("PUT", indexName + "/?pretty");
        request.setJsonEntity(data);
        Response response = client.getLowLevelClient().performRequest(request);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
          log.info("create index [{}}] ok", name);
        } else {
          log.info("create index [{}}] fails, status {}", name,
              response.getStatusLine().getStatusCode());
        }
      } else {
        throw new RuntimeException("file " + file + " exists");
      }
    } catch (Exception e) {
      log.error("create index [{}}] fails {}", e);
      throw new RuntimeException(e);
    }
  }
}