# IoTDB Benchmark

## Command

* Batch Insert

```shell
java -jar benchmark-iotdb-1.0.0-SNAPSHOT.jar insert \
--host 10.19.32.51:6667,10.19.32.52:6667,10.19.32.53:6667 \
--requests 1000000 \
--threads 1 \
--batchSize 2000
```

## 基准测试

* 100w指标，批量 2k，并发 1

```shell
12:50:34.061 [main] INFO ==========================================
12:50:34.061 [main] INFO  IoTDB Batch Insert
12:50:34.061 [main] INFO ----------------- params ------------------
12:50:34.061 [main] INFO requests: 1000000
12:50:34.061 [main] INFO batchSize: 2000
12:50:34.061 [main] INFO threads: 1
12:50:34.061 [main] INFO ----------------- output ------------------
12:50:34.063 [main] INFO total time: 00 min, 06 sec(6338 millis)
12:50:34.063 [main] INFO throughput request: 166666.67 ops/s
12:50:34.065 [main] INFO throughput request size: 0 KB ops/s
12:50:34.065 [main] INFO throughput response size: 0 KB ops/s
12:50:34.065 [main] INFO ==========================================
```

* 100w指标，批量 2k，并发 2

```shell
12:52:58.451 [main] INFO ==========================================
12:52:58.451 [main] INFO  IoTDB Batch Insert
12:52:58.451 [main] INFO ----------------- params ------------------
12:52:58.451 [main] INFO requests: 1000000
12:52:58.451 [main] INFO batchSize: 2000
12:52:58.451 [main] INFO threads: 2
12:52:58.451 [main] INFO ----------------- output ------------------
12:52:58.457 [main] INFO total time: 00 min, 10 sec(10495 millis)
12:52:58.457 [main] INFO throughput request: 100000.0 ops/s
12:52:58.458 [main] INFO throughput request size: 0 KB ops/s
12:52:58.458 [main] INFO throughput response size: 0 KB ops/s
12:52:58.458 [main] INFO ==========================================
```

* 100w指标，批量 5k，并发 1

```shell
12:51:37.011 [main] INFO ==========================================
12:51:37.011 [main] INFO  IoTDB Batch Insert
12:51:37.011 [main] INFO ----------------- params ------------------
12:51:37.011 [main] INFO requests: 1000000
12:51:37.011 [main] INFO batchSize: 5000
12:51:37.011 [main] INFO threads: 1
12:51:37.011 [main] INFO ----------------- output ------------------
12:51:37.013 [main] INFO total time: 00 min, 07 sec(7955 millis)
12:51:37.013 [main] INFO throughput request: 142857.14 ops/s
12:51:37.013 [main] INFO throughput request size: 0 KB ops/s
12:51:37.013 [main] INFO throughput response size: 0 KB ops/s
12:51:37.013 [main] INFO ==========================================
```

* 10w指标，批量 5k，并发 2

```shell
12:51:57.270 [main] INFO ==========================================
12:51:57.270 [main] INFO  IoTDB Batch Insert
12:51:57.270 [main] INFO ----------------- params ------------------
12:51:57.270 [main] INFO requests: 1000000
12:51:57.270 [main] INFO batchSize: 5000
12:51:57.270 [main] INFO threads: 2
12:51:57.270 [main] INFO ----------------- output ------------------
12:51:57.272 [main] INFO total time: 00 min, 07 sec(7979 millis)
12:51:57.272 [main] INFO throughput request: 142857.14 ops/s
12:51:57.273 [main] INFO throughput request size: 0 KB ops/s
12:51:57.273 [main] INFO throughput response size: 0 KB ops/s
12:51:57.273 [main] INFO ==========================================
```