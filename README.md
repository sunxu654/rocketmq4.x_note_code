## RocketMQ高性能的原因分析，高可用架构
+ MQ架构配置
    + 顺序写，随机读，零拷贝
    + 同步刷盘SYNC_FLUSH和异步刷盘ASYNC_FLUSH，通过flushDiskType配置o同步复制和异步复制，通过brokerRole配置，ASYNC_MASTER，SYNC_MASTER，SLAVE
    + 推荐同步复制（双写），异步刷盘
+ 发送端高可用
  + 双主双从架构：创建Topic对应的时候，MessageQueue创建在多个Broker组上即相同的Broker名称，不同的brokerid；当一个Master不可用时，组内其他的Master仍然可用。
  + 但是机器资源不足的时候，需要手工把slave转成master，目前不支持自动转换，可用shell处理
+ 消费高可用
  + 主从架构：Broker角色，Master提供读写，Slave只支持读
  + Consumer不用配置，当Master不可用或者繁忙的时候，Consumer会自动切换到Slave节点进行能读取
+ 提高消息的消费能力
  + 并行消费
  + 增加多个节点
  + 增加单个Consumer的并行度，修改consumer ThreadMin和consumer ThreadMax
  + 批量消费，设置Consumer的consumerMessageBatchMaxSize，默认是1，如果为N，则消息多的时候，每次收到的消息为N条
+ 择LinuxExt4文件系统，了EXt4文件系统删除1G大小的文件通常耗时小于50ms，而Ext3文件系统耗时需要1s，删除文件时磁盘IO压力极大，会导致I0操作超时

## 讲解如何保证消息的可靠性，处理消息丢失的问题
**producer端**  
不采用oneway发送，使用同步或者异步方式发送，做好重试，但是重试的Message key必须唯一o投递的日志需要保存，关键字段，投递时间、投递状态、重试次数、请求体、响应体  
**broker端**  
双主双从架构，NameServer需要多节点  
同步双写、异步刷盘（同步刷盘则可靠性更高，但是性能差点，根据业务选择）   
**consumer**  
消息消费务必保留目志，即消息的元数据和消息体  
消费端务必做好霉等性处理  
**投递到broker端后**
机器断电重启：异步刷盘，消息丢失；同步刷盘消息不丢失  
硬件故障：可能存在丢失，看队列架构

## 顺序消费与分段锁思想

应用:订单的顺序加工(下单,支付,存档,返回)

MessagelListenerOrderly 

Censumer会平均分配queue的数量

并不是简单禁止并发处理，而是为每个Consumer Quene加个锁，消费每个消息前，需要获得这个消息所在的Queue的锁，这样同个时间，同个Queue的消息不被并发消费，但是不同Queue的消息可以并发处理

扩展思维：为什么高并发情况下ConcurrentHashMap比HashTable和HashMap更高效且线程安全？

提示：分段锁Segment
