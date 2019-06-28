package com.sunxu.rocketmq.jms;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class TransactionProducer {
    /**
     * producer的组和名称服务器
     */
    private String producerGroup = "transaction_producer_group";

    private TransactionListener transactionListener = new TransactionListenerImpl();

    private TransactionMQProducer producer = null;

    /**
     * 线程池
     * 推荐自定义线程池,
     * 可以方便的设置性能
     * <p>
     * 同时要给线程设置好名字,方便后期排查bug
     */
    ExecutorService executorService = new ThreadPoolExecutor(2,
            5, 100, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("client-transaction-msg-check-thread");
            return thread;
        }
    });

    /**
     * 这是一个构造器,构造器里面做好自己的主要类:transactionMQProducer,并把producer启动
     * <p>
     * 一个事务producer 需要设置
     * producer 组
     * nameserver
     * 监听器
     * 线程池
     */
    public TransactionProducer() {
        producer = new TransactionMQProducer();

        producer.setProducerGroup(producerGroup);
        producer.setNamesrvAddr(JmsConfig.NAME_SERVER);
        producer.setTransactionListener(transactionListener);
        producer.setExecutorService(executorService);
        //TODO 直接使用producer.start会抛异常(为什么?)
        start();
    }

    public void start() {
        try {
            /**
             * 启动的是producer
             *
             */
            this.producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        this.producer.shutdown();

    }

    public TransactionMQProducer getProducer() {
        return producer;
    }
}

class TransactionListenerImpl implements TransactionListener {


    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        System.out.println("--------executeLocalTransaction----------");
        String body = new String(msg.getBody());
        String key = msg.getKeys();
        String transactionId = msg.getTransactionId();
        System.out.println("body:" + body + ",key:" + key + ",tranID:" + transactionId);
        //TODO 执行本地事务开始
        // TODO 执行本地事务结束
        //假设本地事务返回status
        int status = Integer.parseInt(arg.toString());

        if (1 == status) {
            //二次确认,broker端的半消息变成全消息,可以被消费
            return LocalTransactionState.COMMIT_MESSAGE;
        } else if (2 == status) {
            //本地事务执行失败,broker端的半消息被删除
            return LocalTransactionState.ROLLBACK_MESSAGE;
        } else if (3 == status) {
            //broker端回查消息
            return LocalTransactionState.UNKNOW;
        } else {

            return null;
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {

        System.out.println("-------checkLocalTransaction--------");
        String body = new String(msg.getBody());
        String key = msg.getKeys();
        String transactionId = msg.getTransactionId();
        System.out.println("body:" + body + ",key:" + key + ",tranID:" + transactionId);

        //检查local事务之后,要么返回commit 要么返回rollback
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
