package com.sunxu.rocketmq.jms;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

@Component
public class FilterPayConsumer {

    private String consumerGroup = "consumer_group3";


    private DefaultMQPushConsumer consumer;

    public FilterPayConsumer() throws MQClientException {

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
        /**
         * 注意：消费者订阅关系要一致，不然会消费混乱，甚至消息丢失
         * 订阅关系一致：订阅关系由Topic和Tag组成，同一个group name，订阅的 topic和tag必须是一样的
         */
        consumer.subscribe(JmsConfig.TOPIC, "order1||order3");

        /**
         * 取出消息的消费
         */
        consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {

                MessageExt msg = list.get(0);
                System.out.printf("%s Recieve New Message %s (过滤order1)\n",Thread.currentThread().getName(),
                        new String(msg.getBody()));
                /**
                 * msg的重复消费次数,上限在consumer中设置
                 */


            try{
                String topic = msg.getTopic();
                String body = new String(msg.getBody(), "utf-8");
                String tags = msg.getTags();
                String keys = msg.getKeys();
//                /**
//                 * 模拟消费失败,producer发送的消息的key是123
//                 */
//                    if (msg.getKeys().equals("123")) {
//                        throw new Exception();
//                    }
                System.out.printf("topic = %s,body = %s,tags = %s,keys = %s", topic, body, tags, keys);

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

            } catch (Exception e) {
                e.printStackTrace();
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

        });


        consumer.start();
        System.out.println("consumer start...");
    }


}
