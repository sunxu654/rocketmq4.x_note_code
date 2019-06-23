package com.sunxu.rocketmq.jms;

/**
 * 抽取payconsumer和payproductor的配置
 *
 */
public class JmsConfig {
    /**
     * 增加一主一从,异步复制broker节点和双nameserver
     *
     * 一主一从异步模式的特性:
     * 主节点宕机之后,宕机之前的消息若未被消费,因为被同步到从节点,所以consumer可以消费从节点的数据,等主节点恢复后,从节点告诉主节点"你宕机的前
     * 未被消费的数据,我已经交给consumer消费了;实现从把主同步
     *
     * 但主节点宕机之后,producer不能再写入数据,因为从节点的数据只能来自主节点的异步复制(或者同步复制)
     *
     *
     */
    public static final String NAME_SERVER = "192.168.245.5:9876";
    public static final String TOPIC = "xdclass_pay_test_topic";

    /**
     * 讲解RocketMQ主从同步必备知识点
     * Broker分为master与slave，一个master可以对应多个Slave，但一个slave只能对应一个master，master与slave通过相同的Broker Name来匹配，不同的broker
     * ld来定义是master还是slave
     *
     * Broker向所有的NameServer结点建立长连接，定时注册Topic和发送元数据信息
     *
     * NameServer定时扫描（默认2分钟）所有存活broker的连接，如果超过时间没响应则断开连接（心跳检测），但是consumer客户端不能感知，consumer定时
     * （30s）从NameServer获取topic的最新信息，所以broker不可用时，consumer最多最需要30s才能发现（Producer的机制一样，在未发现broker宕机前
     * 发送的消息会失败）
     *
     * 只有master才能进行写入操作，slave不允许写入只能同步，同步策略取决于master的配置。
     *
     * 客户端消费可以从master和slave消费，默认消费者都从master消费，如果在master挂后，客户端从NameServer中感知到Broker宕机，就会从slave消费，
     * 感知非实时，存在一定的滞后性，slave不能保证master的消息100%都同步过来了，会有少量的消息丢失。但一旦master恢复，未同步过去的消息会被最终消费掉
     *
     * 如果consumer实例的数量比message queue的总数量还多的话，多出来的consumer实例将无法分到queue，也就无法消费到消息，也就无法起到分摊负载的作用，所以需要控制让queue的总数量大于等于consumer的数量
     */
}
