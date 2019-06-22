package com.sunxu.rocketmq.jms;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.stereotype.Component;

@Component
public class PayProducer {
    /**
     * producer的组和名称服务器
     */
    private String producerGroup = "pay_group";

    public DefaultMQProducer getProducer() {
        return this.producer;
    }

    private DefaultMQProducer producer;

    public PayProducer() {
        producer = new DefaultMQProducer(producerGroup);

        /**
         * 可以设置多个nameserver,形式:"<ip>,<ip>,<ip>"
         */
        producer.setNamesrvAddr(JmsConfig.NAME_SERVER);

        start();
    }

    /**
     * TODO 把start放在外面的原因是
     */
    public void start() {
        try {
            this.producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * git功能测试
     */

    public void shutdown() {
        this.producer.shutdown();

    }
}
