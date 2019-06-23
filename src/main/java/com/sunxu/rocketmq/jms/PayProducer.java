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
         * producer在发送之后的重试次数
         * 配置一般在创建对象成功后进行配置
         *
         * 通过set... 来配置
         *
         * 也可以写一个配置文件,通过${key}来配置
         *
         * 配置的默认值,可以查看setRetryTimesWhenSendFailed(2)内部的情况
         */
        producer.setRetryTimesWhenSendFailed(2);
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
