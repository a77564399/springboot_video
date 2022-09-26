package com.project.bilibili.service.utils;


import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

public class RocketMQUtil {
    /**
     * 发送MQ消息的工具类
     * 业务场景对业务发送的回执不是很关心可以走异步发送消息，如果关心回执的话，采用同步发送消息(出异常)
     * @param producer
     * @param message
     * @throws Exception
     */
//   同步发送消息
    public static void syncSendMsg(DefaultMQProducer producer, Message message) throws Exception {
//      发送消息，返回发送结果
        SendResult result = producer.send(message);
        System.out.println(result);
    }

//    异步发送消息，需要消息生产者和消息本身
    public static void asyncSendMsg(DefaultMQProducer producer,Message message) throws Exception
    {
//      消息进行两次发送
        int messageCount = 2;
//      倒计时器，计数
        CountDownLatch2 countDownLatch2 = new CountDownLatch2(messageCount);
        for (int i = 0; i < messageCount; i++) {
//          生产者发送消息到broker,并获取反馈callback
            producer.send(message, new SendCallback() {
//              成功情况下的情况
                @Override
                public void onSuccess(SendResult sendResult) {
//                    计数器-1
                    countDownLatch2.countDown();
//                  打印发送消息的id
                    System.out.println(sendResult.getMsgId());
                }
//              发送消息出现异常
                @Override
                public void onException(Throwable e) {
//                  计数器-1
                    countDownLatch2.countDown();
//                  打印消息：发生异常
                    System.out.println("发送消息时，发生了异常" + e);
                    e.printStackTrace();
                }
            });
        }
//      消息发送结束，计时器停留5秒
        countDownLatch2.await(5, TimeUnit.SECONDS);
    }
}
