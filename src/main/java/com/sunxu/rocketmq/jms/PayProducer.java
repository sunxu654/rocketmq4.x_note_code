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
    private String nameServerAddr = "localhost:9876";

    private DefaultMQProducer producer;

    public PayProducer() {
        producer = new DefaultMQProducer(producerGroup);

        /**
         * 可以设置多个nameserver,形式:"<ip>,<ip>,<ip>"
         */
        producer.setNamesrvAddr(nameServerAddr);

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

}
