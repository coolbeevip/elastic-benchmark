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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class ElasticSearchImportCsvDiscovery {

  public Optional<List<Path>> discovery(String path) throws IOException {
    File file = new File(path);
    if (file.exists()) {
      if (file.isFile()) {
        return Optional.ofNullable(Arrays.asList(Paths.get(path)));
      } else if (file.isDirectory()) {
        return Optional.ofNullable(Files.list(file.toPath())
            .filter(p -> p.toFile().isFile())
            .filter(p -> p.toString().endsWith(".csv"))
            .collect(Collectors.toList()));
      } else {
        throw new RuntimeException("不支持的路径类型 " + path);
      }
    }else{
      throw new RuntimeException("路径不存在 " + path);
    }
  }
}