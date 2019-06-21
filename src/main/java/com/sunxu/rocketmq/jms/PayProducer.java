package com.sunxu.rocketmq.jms;

import org.springframework.stereotype.Component;

@Component
public class PayProducer {
    private String poducerGroup = "pay_group";
    private String nameServerAddr = "localhost:9876";

}
