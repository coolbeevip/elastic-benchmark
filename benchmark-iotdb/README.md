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

* requests: 发送总量
* threads: 并发线程数(session 数)
* batchSize: 每批次数量

## 基准测试

IoTDB Cluster

| CPU | MEM | OS | IoTDB | 
| -- | ---- | ---- | ---- |
| Intel Xeon Processor (Skylake, IBRS) 4Core 2GHz | 32G | CentOS Linux release 7.9.2009 | JDK8 -Xms400M -Xmx8043M |
| Intel Xeon Processor (Skylake, IBRS) 4Core 2GHz | 32G | CentOS Linux release 7.9.2009 | JDK8 -Xms400M -Xmx8043M |
| Intel Xeon Processor (Skylake, IBRS) 4Core 2GHz | 32G | CentOS Linux release 7.9.2009 | JDK8 -Xms400M -Xmx8043M |

Benchmark Client

| CPU | MEM | OS | IoTDB |
| -- | ---- | ---- | ---- |
| Intel Xeon Processor (Skylake, IBRS) 4Core 2GHz | 32G | CentOS Linux release 7.9.2009 | JDK8 |

#### 批量写入

* 100w指标，批量 2k，并发 1

```shell
14:15:50.401 [main] INFO ==========================================
14:15:50.401 [main] INFO  IoTDB Batch Insert
14:15:50.401 [main] INFO ----------------- params ------------------
14:15:50.401 [main] INFO requests: 1000000
14:15:50.401 [main] INFO batchSize: 2000
14:15:50.401 [main] INFO threads: 1
14:15:50.401 [main] INFO ----------------- output ------------------
14:15:50.402 [main] INFO total time: 00 min, 05 sec(5070 millis)
14:15:50.403 [main] INFO throughput request: 200000.0 ops/s
14:15:50.403 [main] INFO throughput request size: 6.27 KB ops/s
14:15:50.403 [main] INFO ==========================================
```

* 100w指标，批量 2k，并发 2

```shell
14:16:15.060 [main] INFO ==========================================
14:16:15.060 [main] INFO  IoTDB Batch Insert
14:16:15.060 [main] INFO ----------------- params ------------------
14:16:15.060 [main] INFO requests: 1000000
14:16:15.060 [main] INFO batchSize: 2000
14:16:15.060 [main] INFO threads: 2
14:16:15.060 [main] INFO ----------------- output ------------------
14:16:15.062 [main] INFO total time: 00 min, 08 sec(8163 millis)
14:16:15.062 [main] INFO throughput request: 125000.0 ops/s
14:16:15.062 [main] INFO throughput request size: 7.84 KB ops/s
14:16:15.062 [main] INFO ==========================================
```

* 100w指标，批量 5k，并发 1

```shell
14:16:38.817 [main] INFO ==========================================
14:16:38.817 [main] INFO  IoTDB Batch Insert
14:16:38.817 [main] INFO ----------------- params ------------------
14:16:38.817 [main] INFO requests: 1000000
14:16:38.817 [main] INFO batchSize: 5000
14:16:38.817 [main] INFO threads: 1
14:16:38.817 [main] INFO ----------------- output ------------------
14:16:38.818 [main] INFO total time: 00 min, 06 sec(6416 millis)
14:16:38.818 [main] INFO throughput request: 166666.67 ops/s
14:16:38.819 [main] INFO throughput request size: 13.04 KB ops/s
14:16:38.819 [main] INFO ==========================================
```

* 100w指标，批量 5k，并发 2

```shell
14:16:59.265 [main] INFO ==========================================
14:16:59.265 [main] INFO  IoTDB Batch Insert
14:16:59.265 [main] INFO ----------------- params ------------------
14:16:59.265 [main] INFO requests: 1000000
14:16:59.265 [main] INFO batchSize: 5000
14:16:59.265 [main] INFO threads: 2
14:16:59.266 [main] INFO ----------------- output ------------------
14:16:59.267 [main] INFO total time: 00 min, 09 sec(9646 millis)
14:16:59.267 [main] INFO throughput request: 111111.11 ops/s
14:16:59.268 [main] INFO throughput request size: 17.38 KB ops/s
14:16:59.268 [main] INFO ==========================================
```

结论：经过测试最优参数为使用单线程已每批次2000的数量写入，写入可达到 20W/QPS