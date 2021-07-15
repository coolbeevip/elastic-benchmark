
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