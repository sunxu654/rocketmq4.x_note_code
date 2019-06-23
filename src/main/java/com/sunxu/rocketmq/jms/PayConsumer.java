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


    private DefaultMQPushConsumer consumer;

    public PayConsumer() throws MQClientException {

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
        consumer.subscribe(JmsConfig.TOPIC, "*");

        /**
         * 取出消息的消费
         */
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
                                                            ConsumeConcurrentlyContext consumeConcurrentlyContext) {

                    MessageExt msg = list.get(0);
                    System.out.printf("%s Recieve New Message %s %n",Thread.currentThread().getName(),
                            new String(msg.getBody()));
                    /**
                     * msg的重复消费次数,上限在consumer中设置
                     */
                    int times = msg.getReconsumeTimes();
                    System.out.println("重试消费次数"+times);


                try{
                    String topic = msg.getTopic();
                    String body = new String(msg.getBody(), "utf-8");
                    String tags = msg.getTags();
                    String keys = msg.getKeys();
                    /**
                     * 模拟消费失败,producer发送的消息的key是123
                     */
                    if (msg.getKeys().equals("123")) {
                        throw new Exception();
                    }
                    System.out.printf("topic = %s,body = %s,tags = %s,keys = %s", topic, body, tags, keys);

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

                } catch (Exception e) {
                    System.out.println("消费异常");

                    if (times == 2) {
                        System.out.println("重复消费次数大于二,记录下异常,等待开发人员排插");
                        //TODO 记录数据库,等待开发人员排插
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

            }

        });


        consumer.start();
        System.out.println("consumer start...");
    }


}
