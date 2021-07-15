# Elasticsearch Benchmark

## Command

* Create Index Template

```shell
java -jar benchmark-elasticsearch-1.0.0.jar template \
--host 10.1.207.180:9200,10.1.207.181:9200,10.1.207.182:9200 \
--name titanic \
--file example/elasticsearch/importcsv/titanic/titanic_index.json
```

* Delete And Create Index Template 

```shell
java -jar benchmark-elasticsearch-1.0.0.jar template \
-D \
--host 10.1.207.180:9200,10.1.207.181:9200,10.1.207.182:9200 \
--name titanic \
--file example/elasticsearch/importcsv/titanic/titanic_index.json
```

* Import CSV Files

```shell
java -jar benchmark-elasticsearch-1.0.0.jar importcsv \
--host 10.1.207.180:9200,10.1.207.181:9200,10.1.207.182:9200 \
--name titanic \
--batch 10000 \
--threads 8 \
--template example/elasticsearch/importcsv/titanic/titanic_csv.tpl \
--csv example/elasticsearch/importcsv/titanic/csv \
--id 1
```

## Improve write throughput performance

1. Improve Cluster Settings
 
* Disable shard reallocation of all shards

```shell
curl -X PUT "10.1.207.180:9200/_cluster/settings?flat_settings=true&pretty" -H 'Content-Type: application/json' -d'
{
  "transient" : {
    "cluster.routing.rebalance.enable" : "none"
  }
}
'
```

2. Improve Index Settings

* Disable replicase
* Increase index.refresh_interval
* Set index.translog.durability to Async
* Decrease merge scheduler to 1 for spinning platter drives

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