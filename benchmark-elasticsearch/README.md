Create Index Template

```
java -jar benchmark-elasticsearch/target/benchmark-elasticsearch-1.0.0.jar template \
--host 10.1.207.180:9200,10.1.207.181:9200,10.1.207.182:9200 \
--name titanic \
--file /Users/zhanglei/asiainfo/gitlab/upstream/elastic-benchmark/data/elasticsearch/importcsv/titanic/titanic_index.json
```

Delete And Create Index Template 

```
java -jar benchmark-elasticsearch/target/benchmark-elasticsearch-1.0.0.jar template \
-D \
--host 10.1.207.180:9200,10.1.207.181:9200,10.1.207.182:9200 \
--name titanic \
--file /Users/zhanglei/asiainfo/gitlab/upstream/elastic-benchmark/data/elasticsearch/importcsv/titanic/titanic_index.json
```

Import CSV Files

```
java -jar benchmark-elasticsearch/target/benchmark-elasticsearch-1.0.0.jar importcsv \
--host 10.1.207.180:9200,10.1.207.181:9200,10.1.207.182:9200 \
--name titanic \
--batch 10000 \
--threads 8 \
--template /Users/zhanglei/asiainfo/gitlab/upstream/elastic-benchmark/data/elasticsearch/importcsv/titanic/titanic_csv.tpl \
--csv /Users/zhanglei/asiainfo/gitlab/upstream/elastic-benchmark/data/elasticsearch/importcsv/titanic/csv \
--id 1
```

## 优化

1. 禁用集群负载

```shell
curl -X PUT "10.1.207.180:9200/_cluster/settings?flat_settings=true&pretty" -H 'Content-Type: application/json' -d'
{
  "transient" : {
    "cluster.routing.rebalance.enable" : "none"
  }
}
'
```

2. 禁用副本、刷新间隔、日志写入策略等

```shell
curl -X PUT "10.1.207.180:9200/titanic/_settings?pretty" -H 'Content-Type: application/json' -d'
{
  "index": {
    "number_of_replicas": 0,
    "refresh_interval": "300s",
    "translog": {
      "durability": "async",
      "sync_interval": "120s",
      "flush_threshold_size": "2048mb"
    },
    "merge": {
      "scheduler": {
        "max_thread_count": 1
      }
    }
  }
}
'