
## 操作系统设置

修改最大连接数限制

```shell
sysctl -w net.core.somaxconn=65365
echo 'net.core.somaxconn = 65365' >> /etc/sysctl.conf
sysctl -p
```

安装编译工具

```
yum install gcc make tcl
```

## 编译 Redis

```
wget http://download.redis.io/redis-6.2.4.tar.gz
tar -xvf redis-6.2.4.tar.gz
cd redis-6.2.4
make test
make
```

编译完毕后可执行文件在 redis-6.2.4/src 目录，配置文件在 redis-6.2.4/redis.conf 

## 修改 Redis 配置

修改配置文件 redis-6.2.4/redis.conf

设置以下参数, `maxmemory` 设置不能超过物理内存的 70%

```
tcp-keepalive 60
maxmemory 10G
maxmemory-policy volatile-lru
loglevel notice
timeout 300
```

禁用RDB持久化和AOF（纯内存模式，只适用于把redis当作临时数据交换的缓存），把以下内容注释掉

```
save 900 1
save 300 10
save 60 10000
rdbcompression no
rdbchecksum no
appendonly no
```

启动

```shell
cd redis-6.2.4/src
./redis-server ../redis.conf
```

基准测试

> 可以看到基本吞吐率都是 4万-7万之间

```shell
cd redis-6.2.4/src
./redis-benchmark -n 10000  -q
PING_INLINE: 41666.67 requests per second, p50=0.615 msec
PING_MBULK: 46728.97 requests per second, p50=0.503 msec
SET: 42735.04 requests per second, p50=0.591 msec
GET: 44247.79 requests per second, p50=0.607 msec
INCR: 40983.61 requests per second, p50=0.551 msec
LPUSH: 84745.77 requests per second, p50=0.311 msec
RPUSH: 64516.13 requests per second, p50=0.367 msec
LPOP: 57471.27 requests per second, p50=0.439 msec
RPOP: 61728.39 requests per second, p50=0.399 msec
SADD: 50505.05 requests per second, p50=0.415 msec
HSET: 60606.06 requests per second, p50=0.375 msec
SPOP: 51282.05 requests per second, p50=0.391 msec
ZADD: 79365.08 requests per second, p50=0.343 msec
ZPOPMIN: 75757.58 requests per second, p50=0.423 msec
LPUSH (needed to benchmark LRANGE): 90090.09 requests per second, p50=0.311 msec
LRANGE_100 (first 100 elements): 52083.33 requests per second, p50=0.487 msec
LRANGE_300 (first 300 elements): 22727.27 requests per second, p50=1.039 msec
LRANGE_500 (first 500 elements): 12836.97 requests per second, p50=1.591 msec
LRANGE_600 (first 600 elements): 9115.77 requests per second, p50=2.679 msec
MSET (10 keys): 43859.65 requests per second, p50=0.839 ms
```


