# IoTDB Benchmark

## Command

* Batch Insert

```shell
java -jar benchmark-iotdb-1.0.0-SNAPSHOT.jar insert \
--host 10.19.32.51:6667,10.19.32.52:6667,10.19.32.53:6667 \
--pathPrefix root.benchmark \
--storageGroup 省份.城市 \
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

## 批量查询

#### 时间区间查询（返回 10 条数据）

```shell
java -jar benchmark-iotdb-1.0.0-SNAPSHOT.jar select \
--host 10.19.32.51:6667,10.19.32.52:6667,10.19.32.53:6667 \
--requests 10000 \
--threads 4 \
--timeout 10000 \
--sql "select temperature from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763 where time > 2022-01-18T17:00:00.000+08:00 and time < 2022-01-18T17:01:00.000+08:00"
```

* 查询 1 万次，10 并发

```shell
19:15:38.610 [main] INFO ==========================================
19:15:38.613 [main] INFO  IoTDB Batch Insert
19:15:38.613 [main] INFO ----------------- params ------------------
19:15:38.613 [main] INFO requests: 10000
19:15:38.613 [main] INFO threads: 10
19:15:38.613 [main] INFO ----------------- output ------------------
19:15:38.613 [main] INFO succeed : 10000
19:15:38.613 [main] INFO fails : 0
19:15:38.616 [main] INFO total time: 00 min, 05 sec(5932 millis)
19:15:38.620 [main] INFO throughput request: 175.4386 ops/s
19:15:38.620 [main] INFO throughput request size: 50.03 KB ops/s
19:15:38.620 [main] INFO throughput response row size: 2105.2632 ops/s
19:15:38.620 [main] INFO ==========================================
```

* 查询 1 万次，50 并发

```shell
19:17:27.023 [main] INFO ==========================================
19:17:27.026 [main] INFO  IoTDB Batch Insert
19:17:27.026 [main] INFO ----------------- params ------------------
19:17:27.026 [main] INFO requests: 10000
19:17:27.026 [main] INFO threads: 50
19:17:27.026 [main] INFO ----------------- output ------------------
19:17:27.026 [main] INFO succeed : 10000
19:17:27.026 [main] INFO fails : 0
19:17:27.030 [main] INFO total time: 00 min, 02 sec(2761 millis)
19:17:27.034 [main] INFO throughput request: 81.30081 ops/s
19:17:27.035 [main] INFO throughput request size: 23.18 KB ops/s
19:17:27.035 [main] INFO throughput response row size: 975.60974 ops/s
19:17:27.035 [main] INFO ==========================================
```

#### 大于时间查询（limit 1000 条数据）

```shell
java -jar benchmark-iotdb-1.0.0-SNAPSHOT.jar select \
--host 10.19.32.51:6667,10.19.32.52:6667,10.19.32.53:6667 \
--requests 10000 \
--threads 50 \
--timeout 10000 \
--sql "select temperature from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763 where time > 2022-01-18T17:00:00.000+08:00 limit 1000"
```

* 查询 1 万次，10 并发

```shell
19:19:40.658 [main] INFO ==========================================
19:19:40.661 [main] INFO  IoTDB Batch Insert
19:19:40.662 [main] INFO ----------------- params ------------------
19:19:40.662 [main] INFO requests: 10000
19:19:40.662 [main] INFO threads: 10
19:19:40.662 [main] INFO ----------------- output ------------------
19:19:40.662 [main] INFO succeed : 10000
19:19:40.662 [main] INFO fails : 0
19:19:40.666 [main] INFO total time: 00 min, 08 sec(8291 millis)
19:19:40.671 [main] INFO throughput request: 123.45679 ops/s
19:19:40.672 [main] INFO throughput request size: 31.59 KB ops/s
19:19:40.672 [main] INFO throughput response row size: 123456.79 ops/s
19:19:40.672 [main] INFO ==========================================
```

* 查询 1 万次，50 并发

```shell
19:20:20.037 [main] INFO ==========================================
19:20:20.040 [main] INFO  IoTDB Batch Insert
19:20:20.040 [main] INFO ----------------- params ------------------
19:20:20.040 [main] INFO requests: 10000
19:20:20.040 [main] INFO threads: 50
19:20:20.040 [main] INFO ----------------- output ------------------
19:20:20.040 [main] INFO succeed : 10000
19:20:20.040 [main] INFO fails : 0
19:20:20.044 [main] INFO total time: 00 min, 04 sec(4731 millis)
19:20:20.048 [main] INFO throughput request: 44.84305 ops/s
19:20:20.048 [main] INFO throughput request size: 11.47 KB ops/s
19:20:20.048 [main] INFO throughput response row size: 44843.05 ops/s
19:20:20.048 [main] INFO ==========================================
```

#### 大于时间排序查询（limit 1000 条数据）

```shell
java -jar benchmark-iotdb-1.0.0-SNAPSHOT.jar select \
--host 10.19.32.51:6667,10.19.32.52:6667,10.19.32.53:6667 \
--requests 10000 \
--threads 50 \
--timeout 10000 \
--sql "select temperature from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763 where time > 2022-01-18T17:00:00.000+08:00 order by time limit 1000"
```

* 查询 1 万次，10 并发

```shell
19:21:20.410 [main] INFO ==========================================
19:21:20.413 [main] INFO  IoTDB Batch Insert
19:21:20.413 [main] INFO ----------------- params ------------------
19:21:20.413 [main] INFO requests: 10000
19:21:20.413 [main] INFO threads: 10
19:21:20.413 [main] INFO ----------------- output ------------------
19:21:20.413 [main] INFO succeed : 10000
19:21:20.413 [main] INFO fails : 0
19:21:20.422 [main] INFO total time: 00 min, 08 sec(8900 millis)
19:21:20.427 [main] INFO throughput request: 116.27907 ops/s
19:21:20.429 [main] INFO throughput request size: 31.34 KB ops/s
19:21:20.430 [main] INFO throughput response row size: 116279.07 ops/s
19:21:20.430 [main] INFO ==========================================
```

* 查询 1 万次，50 并发

```shell
19:21:47.794 [main] INFO ==========================================
19:21:47.797 [main] INFO  IoTDB Batch Insert
19:21:47.797 [main] INFO ----------------- params ------------------
19:21:47.797 [main] INFO requests: 10000
19:21:47.797 [main] INFO threads: 50
19:21:47.797 [main] INFO ----------------- output ------------------
19:21:47.797 [main] INFO succeed : 10000
19:21:47.797 [main] INFO fails : 0
19:21:47.802 [main] INFO total time: 00 min, 05 sec(5020 millis)
19:21:47.812 [main] INFO throughput request: 43.47826 ops/s
19:21:47.813 [main] INFO throughput request size: 11.72 KB ops/s
19:21:47.813 [main] INFO throughput response row size: 43478.26 ops/s
19:21:47.813 [main] INFO ==========================================
```

#### 时间区间聚合查询

```shell
java -jar benchmark-iotdb-1.0.0-SNAPSHOT.jar select \
--host 10.19.32.51:6667,10.19.32.52:6667,10.19.32.53:6667 \
--requests 10000 \
--threads 50 \
--timeout 10000 \
--sql "select count(temperature) from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763 group by ([2022-01-18T00:00:00, 2022-01-19T00:00:00),1h)"
```

* 查询 1 万次，10 并发

```shell
19:24:11.859 [main] INFO ==========================================
19:24:11.866 [main] INFO  IoTDB Batch Insert
19:24:11.866 [main] INFO ----------------- params ------------------
19:24:11.866 [main] INFO requests: 10000
19:24:11.867 [main] INFO threads: 10
19:24:11.867 [main] INFO ----------------- output ------------------
19:24:11.867 [main] INFO succeed : 10000
19:24:11.867 [main] INFO fails : 0
19:24:11.870 [main] INFO total time: 00 min, 06 sec(6833 millis)
19:24:11.879 [main] INFO throughput request: 161.29033 ops/s
19:24:11.880 [main] INFO throughput request size: 42.84 KB ops/s
19:24:11.880 [main] INFO throughput response row size: 3870.9678 ops/s
19:24:11.880 [main] INFO ==========================================
```

* 查询 1 万次，50 并发

```shell
19:24:36.485 [main] INFO ==========================================
19:24:36.487 [main] INFO  IoTDB Batch Insert
19:24:36.487 [main] INFO ----------------- params ------------------
19:24:36.487 [main] INFO requests: 10000
19:24:36.488 [main] INFO threads: 50
19:24:36.488 [main] INFO ----------------- output ------------------
19:24:36.488 [main] INFO succeed : 10000
19:24:36.488 [main] INFO fails : 0
19:24:36.492 [main] INFO total time: 00 min, 03 sec(3031 millis)
19:24:36.495 [main] INFO throughput request: 72.9927 ops/s
19:24:36.496 [main] INFO throughput request size: 19.39 KB ops/s
19:24:36.496 [main] INFO throughput response row size: 1751.8248 ops/s
19:24:36.496 [main] INFO ==========================================
```

## 常用查询

时间比较

```sql
select temperature from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763 where time > 2022-01-18T17:00:00.000+08:00
```

时间范围

```sql
select temperature from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763 where time > 2022-01-18T17:00:00.000+08:00 and time < 2022-01-18T17:01:00.000+08:00
```

排序 

```sql
select temperature from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763 where time > 2022-01-18T17:00:00.000+08:00 order by time limit 10;
```

翻页

```sql
select temperature from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763 where time > 2022-01-18T17:00:00.000+08:00 order by time desc offset 10 limit 10;
```

空值填充

```sql
select temperature from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763 where time = 2022-01-18T17:00:00.000+08:00 fill(float[previous, 1m]) 
```

聚合-数量

```
select count(temperature) from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763
```

聚合-时间

```sql
select count(temperature) from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763 group by ([2022-01-18T00:00:00, 2022-01-22T00:00:00),1d);
```

聚合-最大值，最小值

```
select max_value(temperature) from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763
select min_value(temperature) from root.benchmark.省份.城市3.SITE-e06ed84f654a479a89dc80fce8e8f578.VENDORS-b21b59965f644ed3b137e618ccd03571.ELEMENT-fe81bc80ea394f8c8179962b158d5ac0.CARD.CARD-52b0fae4bf0940d4b7eb4bf9d9fc4763
```

## 其他

#### 0.13.0 版本变化

PATH 名称中 `-` 字符作为保留字，使用时需要使用  ` 扩起来，例如:

```
root.test.`NET-1`.s1
```

#### 升级注意事项

1. 先在 CLI 中 flush
2. 停止老版本程序
3. 如果 data/wal 目录还存在则手动删除
4. 升级程序
5. 启动新版本程序

## 集群可用性

### 三节点

* consistency_level=mid 
* default_replica_num=2

1. 启动 A,B,C(首次启动三个节点都启动成功后才可以对外提供服务)
2. 插入，查询正常
3. 停止 C
4. 查询正常(等待重新选举leader后，查询正常)
   > IoTDB> show timeseries
   show timeseries
   Msg: 411: Error occurred in query process: check consistency failed, error message=mid consistency, localAppliedId is smaller than the leaderCommitId
5. 插入，查询正常

