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
    public static final String NAME_SERVER = "192.168.245.4:9876,192.168.245.5:9876";
    public static final String TOPIC = "dclass_pay_test_topic";

}
