
```shell
scp -P 22022 target/elastic-benchmark-exec.jar \
    data/elasticsearch/importcsv/*.tpl \
    data/elasticsearch/importcsv/*.json \
    elasticsearch@10.1.207.180:/opt/elasticsearch/benchmark


scp -P 22022 target/elastic-benchmark-exec.jar \
    data/elasticsearch/importcsv/*.tpl \
    data/elasticsearch/importcsv/*.json \
    elasticsearch@10.1.207.181:/opt/elasticsearch/benchmark
```
## ElasticSearch

#### 索引

创建索引

```shell
java -cp elastic-benchmark-exec.jar com.coolbeevip.benchmark.elasticsearch.index.ElasticsearchTemplateMain \
-H 10.1.207.180:9200 \
-H 10.1.207.181:9200 \
-H 10.1.207.182:9200 \
--command create \
--index onu_zl_test \
--template /opt/elasticsearch/benchmark/ONU_INDEX_TPL.json
```

```
--host 10.1.207.180:9200,10.1.207.181:9200,10.1.207.182:9200 --name zl_test --file /opt/elasticsearch/benchmark/ONU_INDEX_TPL.json
```

删除索引

```shell
java -cp elastic-benchmark-exec.jar com.coolbeevip.benchmark.elasticsearch.index.ElasticsearchTemplateMain \
-H 10.1.207.180:9200 \
-H 10.1.207.181:9200 \
-H 10.1.207.182:9200 \
--command delete \
--index onu_zl_test
```

#### 导入数据

定义导入数据模版 `data/elasticsearch/importcsv/ONU_DATA_TPL.tpl`，这是一个 JSON 格式的数据文件，启用用 `C` 代表对应 CSV 文件中的列序号（从 C0 开始）

当需要集中大批量导入数据时，建议先优化 ES 参数

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
curl -X PUT "10.1.207.180:9200/onu_zl_test/_settings?pretty" -H 'Content-Type: application/json' -d'
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
```

导入 CSV 文件 `data/elasticsearch/importcsv/csv`

```
java -Xmx8g -Xms8g \
-cp elastic-benchmark-exec.jar com.coolbeevip.benchmark.elasticsearch.importcsv.ElasticSearchImportCsvMain \
-H 10.1.207.180:9200 -H 10.1.207.181:9200 -H 10.1.207.182:9200 \
--index onu_zl_test --batch 10000 --concurrency 8 --template /opt/elasticsearch/benchmark/ONU_DATA_TPL.tpl --csv /opt/elasticsearch/benchmark/onu --id 0
```

* -H ES 服务器地址端口
* --index 索引名称
* --batch 最大批次
* --concurrency 并发数
* --template 数据模版
* --csv CSV 文件所在目录（文件名要以 .csv 结尾）
* --id 用 CSV 哪里数据作为 ID（如果不配置，则自动生成）
* --completed 导入成功后将 `.csv` 扩展名改为 `.COMPLETED`

插入数据

```shell
java -Xmx8g -Xms8g \
-cp elastic-benchmark-exec.jar com.coolbeevip.benchmark.ElasticSearchDataMain \
-H 10.1.207.180:9200 \
-H 10.1.207.181:9200 \
-H 10.1.207.182:9200 \
--index onu_zl_test \
--total 2000000 \
--threads 20 \
--batch 10000 \
--async \
--template /opt/elasticsearch/benchmark/ONU_DATA_TPL.tpl
```

