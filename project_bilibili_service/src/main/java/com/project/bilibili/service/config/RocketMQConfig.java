package com.project.bilibili.service.config;

import com.project.bilibili.domain.constant.UserMomentsConstant;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Configuration
public class RocketMQConfig {
    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

//  用户动态相关生产者
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

//    用户动态相关消费者
    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
//      订阅内容，以及二级主题
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS,"*");
//      添加监听器，生产者把消息推送到MQ，MQ把消息推给消费者，消费者抓取消息，监听器监听到新增内容后进行推送
//        使用并发监听
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
//            并行处理监听器，拿到两个变量消息和上下文
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for(MessageExt messageExt:list)
                {
//                   消息打印
                    System.out.println(messageExt);
                }
//                返回成功状态
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
//        启动消费者
        consumer.start();
        return consumer;
    }

}
