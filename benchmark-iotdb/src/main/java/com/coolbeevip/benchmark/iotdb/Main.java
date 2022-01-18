package com.coolbeevip.benchmark.iotdb;

import com.beust.jcommander.JCommander;
import com.coolbeevip.benchmark.iotdb.insert.InsertCommand;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) {

    InsertCommand commandNc = new InsertCommand();
    JCommander jc = JCommander.newBuilder()
        .addCommand(commandNc)
        .addObject(args)
        .build();
    jc.parse(args);
    String parsedCmd = jc.getParsedCommand();
    try{
      switch (parsedCmd) {
        case "insert":
          commandNc.execute();
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