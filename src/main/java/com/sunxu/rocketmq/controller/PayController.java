package com.sunxu.rocketmq.controller;

import com.sunxu.rocketmq.jms.JmsConfig;
import com.sunxu.rocketmq.jms.PayProducer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class PayController {

    @Autowired
    PayProducer payProducer;


    @RequestMapping("/api/v1/pay_cb")
    public Object callback(String text) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        /**
         * message需要topic tag 和 字节数组
         * 也有其他的构造函数,传不同的参数
         */
        Message message = new Message(JmsConfig.TOPIC, "taga", "123", ("hello rocketmq = " + text).getBytes());
        /**
         * message 由producer发送到broker之后,不立即被消费,而是等待一个timelevel之后
         *
         * timelevel每个level对应的时间是由rocketmq的源码包里:E:\GitHub\rocketmq-master\store\src\main\java\org\apache\\rocketmq
         * \store\config\MessageStoreConfig.java 里面的
         * private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h"
         * 决定的;
         *
         * messageDelayLevel的值不可以通过配置文件修改,但是可以修改源码的值,再重新编译,来做一份自定义版的rocketmq
         *
         * 应用场景:消息生产和消费有时间窗口要求：比如在天猫电商交易中超时未支付关闭订单的场景，在订单创建时会发送一条延时消息.这条消息将会在30分钟
         * 以后投递给消费者，消费者收到此消息后需要判断对应的订单是否已完成支付。如支付未完成，则关闭订单。如已完成支付则忽略
         *
         *
         */
        message.setDelayTimeLevel(3);

//       发送方式 1 : 同步发送
//        /**
//         * 把message通过producer发送出去
//         * 发送到消息队列 等待消费者消费
//         */
//        SendResult sendResult = payProducer.getProducer().send(message);
//        System.out.println(sendResult);

//        发送方式2 :异步发送
        /**
         *
         * 异步发送消息和回调
         * 在发送消息的同时,开一个线程来做一些对时间要求比较敏感的业务逻辑
         * 比如用户注册成功后,直接在本地的onsuccess里面调用发放优惠券服务,而不需要用户的消息传输完成再发送
         */
        payProducer.getProducer().send(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.printf("发送结果= %s , sendResult详细信息=%s",sendResult.getSendStatus(),sendResult.toString());
            }

            @Override
            public void onException(Throwable e) {
                e.printStackTrace();
                //TODO 补偿机制,比如重新发送
            }
        });

//        发送方式3: send one way
        /**
         * 用于给logserver等不需要响应,对性能要求比较高,对可靠性要求比较低的服务 发送请求
         * 返回值为空,ublic void sendOneway(Message msg)
         */
        payProducer.getProducer().sendOneway(message);
        return new HashMap<>();
    }

}
