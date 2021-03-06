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