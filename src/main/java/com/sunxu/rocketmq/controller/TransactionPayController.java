package com.sunxu.rocketmq.controller;

import com.sunxu.rocketmq.domain.ProductOrder;
import com.sunxu.rocketmq.jms.JmsConfig;
import com.sunxu.rocketmq.jms.PayProducer;
import com.sunxu.rocketmq.jms.TransactionProducer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

@RestController
public class TransactionPayController {

    @Autowired
    TransactionProducer transactionProducer;

    @RequestMapping("/api/v1/tran")
    public Object callback(String tag, String otherParam) throws Exception {

        Message message = new Message(JmsConfig.TRAN_TOPIC, tag,
                tag + "_key", tag.getBytes());
        TransactionSendResult sendResult = transactionProducer.getProducer().sendMessageInTransaction(message, otherParam);
        System.out.println("发送结果" + sendResult);

        return new HashMap<>();
    }
}
