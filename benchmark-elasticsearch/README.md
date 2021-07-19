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

* Search

```shell
java -jar benchmark-elasticsearch-1.0.0.jar search \
--host 10.1.207.180:9200,10.1.207.181:9200,10.1.207.182:9200 \
--name titanic \
--requests 100 \
--threads 10 \
--file example/elasticsearch/importcsv/titanic/search.json
```

## Improve write throughput performance

### Important System Configuration

1. Disable swapping

```shell
swapoff -a
sed -i "s/^.*swap/#&/g" /etc/fstab
```

2. Configure swappiness

```shell
echo "vm.swappiness=1" >> /etc/sysctl.conf
sysctl -p
```

3. Increase file descriptors

`/etc/security/limits.conf`

```shell
* soft nofile 65535
* hard nofile 65535
```

4. Ensure sufficient threads

`/etc/security/limits.conf`

```shell
* soft nproc 65535
* hard nproc 65535
```

5. Ensure sufficient virtual memory

```shell
echo "vm.max_map_count=262144" >> /etc/sysctl.conf
sysctl -p
```

6. TCP retransmission timeout

You can decrease the maximum number of TCP retransmissions to 5 by running the following command as root. Five retransmissions corresponds with a timeout of around six seconds.

```shell
echo "net.ipv4.tcp_retries2=5" >> /etc/sysctl.conf
sysctl -p
```

### Important Elasticsearch Static Configuration

1. elasticsearch.yml settings

```properties
path.data: /data01/elasticsearch/data
path.logs: /data01/elasticsearch/logs
indices.memory.index_buffer_size: 50%
indices.queries.cache.size: 30%
thread_pool.search.queue_size: 10000
thread_pool.get.queue_size: 1000
thread_pool.write.queue_size: 10000
```

2. jvm.options settings

Set Xms and Xmx to no more than 50% of your total memory. Elasticsearch requires memory for purposes other than the JVM heap. For example, Elasticsearch uses off-heap buffers for efficient network communication and relies on the operating system’s filesystem cache for efficient access to files. The JVM itself also requires some memory. It’s normal for Elasticsearch to use more memory than the limit configured with the Xmx setting.

Heap size settings

```properties
-Xms8g
-Xmx8g
```

JVM heap dump path setting

```properties
-XX:HeapDumpPath=/data01/elasticsearch/dump
```

Temporary directory settings

```properties
-Djava.io.tmpdir=/data01/elasticsearch/temp
```

JVM fatal error log setting

```properties
-XX:ErrorFile=/data01/elasticsearch/logs/hs_err_pid%p.log
```

GC logging settings

```properties
9-:-Xlog:gc*,gc+age=trace,safepoint:file=/data01/elasticsearch/logs/gc.log:utctime,pid,tags:filecount=32,filesize=64m
```

### Important Elasticsearch Dynamic Configuration

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

3. Add slow log configuration

```shell
curl -X PUT "10.1.207.180:9200/onu/_settings?pretty" -H 'Content-Type: application/json' -d'
{
  "index.search.slowlog.threshold.query.warn": "10s",
  "index.search.slowlog.threshold.query.info": "5s",
  "index.search.slowlog.threshold.query.debug": "2s",
  "index.search.slowlog.threshold.query.trace": "500ms",
  "index.search.slowlog.threshold.fetch.warn": "1s",
  "index.search.slowlog.threshold.fetch.info": "800ms",
  "index.search.slowlog.threshold.fetch.debug": "500ms",
  "index.search.slowlog.threshold.fetch.trace": "200ms",
  "index.search.slowlog.level": "info"
}'
