package com.coolbeevip.benchmark.iotdb;

import com.beust.jcommander.JCommander;
import com.coolbeevip.benchmark.iotdb.insert.InsertCommand;
import com.coolbeevip.benchmark.iotdb.select.SelectCommand;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) {

    InsertCommand commandInsert = new InsertCommand();
    SelectCommand commandSelect = new SelectCommand();
    JCommander jc = JCommander.newBuilder()
        .addCommand(commandInsert)
        .addCommand(commandSelect)
        .addObject(args)
        .build();
    jc.parse(args);
    String parsedCmd = jc.getParsedCommand();
    try{
      switch (parsedCmd) {
        case "insert":
          commandInsert.execute();
          break;
        case "select":
          commandSelect.execute();
          break;
        default:
          log.error("Invalid command: {}", parsedCmd);
          System.exit(1);
      }
      System.exit(0);
    }catch (Exception e){
      log.error(e.getMessage(),e);
      System.exit(1);
    }
  }
}