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
package com.coolbeevip.benchmark.common;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class UnitTools {

  public static String convertTime(long millis) {
    return String.format("%02d min, %02d sec(%02d millis)",
        TimeUnit.MILLISECONDS.toMinutes(millis),
        TimeUnit.MILLISECONDS.toSeconds(millis) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),millis
    );
  }

  public static String convertSize(double size_bytes) {
    String cnt_size;
    double size_kb = size_bytes / 1024;
    double size_mb = size_kb / 1024;
    double size_gb = size_mb / 1024;
    NumberFormat numberFormat = NumberFormat.getInstance();
    numberFormat.setMaximumFractionDigits(2);
    if (size_gb > 1) {
      cnt_size = numberFormat.format(size_gb) + " GB";
    } else if (size_mb > 1) {
      cnt_size = numberFormat.format(size_mb) + " MB";
    } else {
      cnt_size = numberFormat.format(size_kb) + " KB";
    }
    return cnt_size;
  }
}