package com.sunxu.rocketmq.jms;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Component
public class PayConsumer {

    private String consumerGroup = "consumer_group";
    private String nameServerAddr = "192.168.245.4:9876";

    public static final String topic = "xdclass_pay_test_topic";

    private DefaultMQPushConsumer consumer;

    public PayConsumer() throws MQClientException {

        /**
         * 构造器里创建归属某个组的cosumer实例
         */
        consumer = new DefaultMQPushConsumer(consumerGroup);
        /**
         * 设置nameserver地址
         */
        consumer.setNamesrvAddr(nameServerAddr);

        /**
         * 消费信息的起始方式
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        /**
         * 订阅某个主题,并消费标签匹配为 * 的消息
         */
        consumer.subscribe(topic, "*");

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
                                                            ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                try{

                    Message msg = list.get(0);
                    System.out.printf("%s Recieve New Message %s %n",Thread.currentThread().getName(),
                            new String(msg.getBody()));

                    String topic = msg.getTopic();
                    String body = new String(msg.getBody(), "utf-8");
                    String tags = msg.getTags();
                    String keys = msg.getKeys();
                    System.out.printf("topic = %s,body = %s,tags = %s,keys = %s", topic, body, tags, keys);

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

            }

        });


        consumer.start();
        System.out.println("consumer start...");
    }


}
