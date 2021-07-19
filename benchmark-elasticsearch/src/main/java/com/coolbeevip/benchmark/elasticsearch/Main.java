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
package com.coolbeevip.benchmark.elasticsearch;

import com.beust.jcommander.JCommander;
import com.coolbeevip.benchmark.elasticsearch.importcsv.ElasticsearchImportCsvCommand;
import com.coolbeevip.benchmark.elasticsearch.search.ElasticsearchSearchCommand;
import com.coolbeevip.benchmark.elasticsearch.template.ElasticsearchTemplateCommand;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) {

    ElasticsearchTemplateCommand commandTemplate = new ElasticsearchTemplateCommand();
    ElasticsearchImportCsvCommand commandImportCsv = new ElasticsearchImportCsvCommand();
    ElasticsearchSearchCommand searchCommand = new ElasticsearchSearchCommand();
    JCommander jc = JCommander.newBuilder()
        .addCommand(commandImportCsv)
        .addCommand(commandTemplate)
        .addCommand(searchCommand)
        .addObject(args)
        .build();
    jc.parse(args);
    String parsedCmd = jc.getParsedCommand();
    try{
      switch (parsedCmd) {
        case "importcsv":
          if (commandImportCsv.isHelp()) {
            jc.usage();
          }else{
            commandImportCsv.execute();
          }
          break;
        case "template":
          if (commandTemplate.isHelp()) {
            jc.usage();
          } else {
            commandTemplate.execute();
          }
          break;
        case "search":
          if(searchCommand.isHelp()){
            jc.usage();
          }else{
            searchCommand.execute();
          }
          break;
        default:
          log.error("Invalid command: {}", parsedCmd);
          System.exit(1);
      }
      System.exit(0);
    }catch (Exception e){
      System.exit(1);
    }
  }
}