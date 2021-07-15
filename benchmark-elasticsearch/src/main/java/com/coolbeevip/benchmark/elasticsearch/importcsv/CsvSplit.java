package com.coolbeevip.benchmark.elasticsearch.importcsv;

import java.util.ArrayList;
import java.util.List;

public class CsvSplit {

  public static String[] split(String record) {
    List<String> words = new ArrayList<>();
    boolean notInsideComma = true;
    int start = 0, end = 0;
    for (int i = 0; i < record.length() - 1; i++) {
      if (record.charAt(i) == ',' && notInsideComma) {
        words.add(fix(record.substring(start, i)));
        start = i + 1;
      } else if (record.charAt(i) == '"') {
        notInsideComma = !notInsideComma;
      }
    }
    words.add(fix(record.substring(start)));
    return words.toArray(new String[0]);
  }

  private static String fix(String value){
    value = value.replace("\\","/");
    if(value.startsWith("\"")){
      value = value.substring(1,value.length());
    }
    if(value.endsWith("\"")){
      value = value.substring(0,value.length()-1);
    }
    value = value.replace("\"\"","\\\"");
    value = value.replaceAll("\t","");
    value = value.replace("/\\","/");
    return value;
  }
}