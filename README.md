# 基准测试工具

## ElasticSearch

* ES 基准测试
* CSV 数据批量导入 ES

```
20:54:39.747 [main] INFO ==========================================
20:54:39.747 [main] INFO  ElasticSearch Import CSV
20:54:39.747 [main] INFO ----------------- params ------------------
20:54:39.747 [main] INFO index: address
20:54:39.747 [main] INFO id: 0
20:54:39.747 [main] INFO csv count: 34554758
20:54:39.748 [main] INFO csv size: 17.37 GB
20:54:39.748 [main] INFO batch: 10000
20:54:39.748 [main] INFO threads: 8
20:54:39.748 [main] INFO ----------------- output ------------------
20:54:39.748 [main] INFO succeed : 34554758
20:54:39.748 [main] INFO fails : 0
20:54:39.748 [main] INFO total time: 13 min, 16 sec
20:54:39.748 [main] INFO parse cvs time: 72 min, 53 sec
20:54:39.748 [main] INFO average size per: 0.53 KB
20:54:39.748 [main] INFO throughput records: 43410 ops/s
20:54:39.748 [main] INFO throughput size: 22.35 MB ops/s
20:54:39.748 [main] INFO ==========================================
```

## Release

```shell script
sh publish.sh git@github.com:coolbeevip/elastic-benchmark.git
```

