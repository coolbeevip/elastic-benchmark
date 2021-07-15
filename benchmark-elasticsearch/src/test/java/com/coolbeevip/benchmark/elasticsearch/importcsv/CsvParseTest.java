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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class CsvParseTest {

  @Test
  public void test() throws IOException {
    String record = "aaa,\\,\"a\tb\",2,\"11\"\"1\",\"\"\"1,11\"\"\",EMS=ENS-N31-1-P:ManagedElement=恩施/咸丰县/巴西坝-OLT001-ZX-C320:ONU=恩施/咸丰县/黄金洞乡/巴西坝村小区/黄金洞路27号/1组10\\145-FTTH-ZTE-AUTO";
    System.out.println(record);
    String[] fields = CsvSplit.split(record);
    Arrays.stream(fields).forEach(System.out::println);

    Map<String,String> values = new HashMap();
    values.put("name","\"a\tb\"");
    values.put("name1","\\");
    ObjectMapper mapper = new ObjectMapper();
    System.out.println(mapper.writeValueAsString(values));
  }
}