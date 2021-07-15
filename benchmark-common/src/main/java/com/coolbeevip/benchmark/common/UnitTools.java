package com.coolbeevip.benchmark.common;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class UnitTools {

  public static String convertTime(long millis) {
    return String.format("%02d min, %02d sec",
        TimeUnit.MILLISECONDS.toMinutes(millis),
        TimeUnit.MILLISECONDS.toSeconds(millis) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
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