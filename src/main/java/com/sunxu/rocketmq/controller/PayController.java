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
        Message message = new Message(JmsConfig.TOPIC, "taga","123",("hello rocketmq = " + text).getBytes());
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
