package com.sunxu.rocketmq.jms;

/**
 * 抽取payconsumer和payproductor的配置
 *
 */
public class JmsConfig {
    /**
     * 增加一主一从,异步复制broker节点和双nameserver
     */
    public static final String NAME_SERVER = "192.168.245.4:9876,192.168.245.5:9876";
    public static final String TOPIC = "dclass_pay_test_topic";

}
