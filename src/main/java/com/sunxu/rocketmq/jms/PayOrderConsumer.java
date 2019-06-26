package com.sunxu.rocketmq.jms;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PayOrderConsumer {

    private String consumerGroup = "consumer_group2";


    private DefaultMQPushConsumer consumer;

    public PayOrderConsumer() throws MQClientException {

        /**
         * 构造器里创建归属某个组的cosumer实例
         */
        consumer = new DefaultMQPushConsumer(consumerGroup);
        /**
         * 设置nameserver地址
         */
        consumer.setNamesrvAddr(JmsConfig.NAME_SERVER);

        /**
         * 消费信息的起始方式
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        /**
         * 订阅某个主题,并消费标签匹配为 * 的消息
         */
        consumer.subscribe(JmsConfig.ORDER_TOPIC, "*");

        /**
         * 取出消息的消费
         */
        consumer.registerMessageListener((MessageListenerOrderly) (list, context) -> {
            MessageExt msg = list.get(0);
            System.out.printf("%s Recieve New Message %s %n", Thread.currentThread().getName(),
                    new String(msg.getBody()));

            try {
                String topic = msg.getTopic();
                String body = new String(msg.getBody(), "utf-8");
                String tags = msg.getTags();
                String keys = msg.getKeys();

                System.out.printf("消费信息:topic = %s,body = %s,tags = %s,keys = %s\n", topic, body, tags, keys);

                return ConsumeOrderlyStatus.SUCCESS;

            } catch (Exception e) {

                e.printStackTrace();
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }

        });

        consumer.start();
        System.out.println("consumer start...");
    }


}
