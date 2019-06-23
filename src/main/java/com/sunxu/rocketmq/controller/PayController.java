package com.sunxu.rocketmq.controller;

import com.sunxu.rocketmq.jms.JmsConfig;
import com.sunxu.rocketmq.jms.PayProducer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
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
        Message message = new Message(JmsConfig.TOPIC, "taga","123",("hello rocketmq = " + text).getBytes());
        /**
         * 把message通过producer发送出去
         * 发送到消息队列 等待消费者消费
         */
        SendResult sendResult = payProducer.getProducer().send(message);

        System.out.println(sendResult);
        return new HashMap<>();
    }

}
