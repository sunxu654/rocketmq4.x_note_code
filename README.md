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

提示：分段锁Segment(不是单纯的禁止并发,而是在每一个Map上都加一个锁,同一个map只能被不同进程串行处理,但是多个Map是可以并发处理的)

## 消费端知识点

**Topic下队列的奇偶数会影响Customer个数里面的消费数量**  

+ 如果是4个队列，8个消息，4个节点则会各消费2条，如果不对等，则负载均衡会分配不均， 

+ 如果consumer实例的数量比message queue的总数量还多的话，多出来的consumer实例将无法分到queue，也就无法消费到消息，也就无法起到分摊负载的作用，所以需要控制让queue的总数量大于等于consumer的数量 


**集群模式（默认）：**  

Consumer实例平均分摊消费生产者发送的消息  

**广播模式：**  

广播模式下消费消息：投递到Broker的消息会被每个Consumer进行消费，一条消息被多个Consumer消费，广播消费中ConsumerGroup暂时无用  
怎么切换模式：通过setMessageModel0

**监听内容**  

一般是监听*，或者指定tag，||运算
不建议使用多个Tag，每个队列单一职责，多个队列多个职责

**消费者订阅关系要一致，不然会消费混乱，甚至消息丢失**  

订阅关系一致：订阅关系由Topic和Tag组成，同一个group name，订阅的topic和tag必须是一样的

**消息过滤方式**  

+ 在Broker 端进行MessageTag过滤，遍历message queue存储的message tag和订阅传递的tag的hashcode不一样则跳过，符合的则传输给Consumer，在consumer queue存储的是对应的hashcode，对比也是通过hashcode对比；Consumer收到过滤消息后也会进行匹配操作，但是是对比真实的message tag而不是hashcode oconsume queue存储使用hashcode定长，节约空间
+ 过滤中不访问commitlog，可以高效过滤
+ 如果存在hash冲突，Consumer端可以进行再次确认