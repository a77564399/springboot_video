package com.project.bilibili.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.project.bilibili.domain.UserFollowing;
import com.project.bilibili.domain.UserMoment;
import com.project.bilibili.domain.constant.UserMomentsConstant;
import com.project.bilibili.service.UserFollowingService;
import com.project.bilibili.service.webSocket.WebSocketService;
import io.netty.util.internal.StringUtil;
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

import java.util.ArrayList;
import java.util.List;

import static com.project.bilibili.domain.constant.UserMomentsConstant.TOPIC_DANMUS;
import static com.project.bilibili.domain.constant.UserMomentsConstant.TOPIC_MOMENTS;

@Configuration
public class RocketMQConfig {
//    @Value("${rocketmq.name.server.address}")
    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;

//  用户动态相关生产者
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
//        producer.setVipChannelEnabled(false);
//        producer.setSendMsgTimeout(15000);
        producer.start();

//        producer.setSendMsgTimeout(10000);
        return producer;
    }

//    用户动态相关消费者
    @Bean("momentsConsume")
    public DefaultMQPushConsumer momentsConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
//        consumer.setVipChannelEnabled(false);
//      订阅内容，以及二级主题
        consumer.subscribe(TOPIC_MOMENTS,"*");
//      添加监听器，生产者把消息推送到MQ，MQ把消息推给消费者，消费者抓取消息，监听器监听到新增内容后进行推送
//        使用并发监听
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
//            并行处理监听器，拿到两个变量消息和上下文
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
//                for(MessageExt messageExt:list)
//                {
////                   消息打印
//                    System.out.println(messageExt);
//                }

//              由于每次添加动态，每次只默认向RocketMQ发送一条数据，因此list中只有一个元素
                MessageExt msg = list.get(0);
//              消息为空
                if(msg==null)
                {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                String bodyStr = new String(msg.getBody());
//                System.out.println("body:"+bodyStr);
//              把Json字符串转成实体类,获取到UserMoment实体类
                UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoment.class);
//                System.out.println(2);
//              获取到userID，然后可以查找谁订阅了这个用户动态(粉丝)
                Long userId = userMoment.getUserId();
//                System.out.println(userId);
                List<UserFollowing> userFans = userFollowingService.getUserFans(userId);
//              使用Redis将用户的动态push给所有的粉丝
                for(UserFollowing userFan:userFans)
                {
//                    System.out.println("userFan:"+userFan.getUserId());
//                    System.out.println("fan+"+userFan.getUserId());
//                   定义redis的key
                    String key = "subscribed-" + userFan.getUserId();
//                    System.out.println("key:"+key);
//                  通过redisTemplate中的相关方法，获取到这个key对应的内容
//                    由于一个用户会订阅很多up，所以里面的值会把各种用户的都取出来，也就是说取出来的应该是一个列表
                    String subscribedListStr = redisTemplate.opsForValue().get(key);
                    List<UserMoment> subscribedList;
                    if(StringUtil.isNullOrEmpty(subscribedListStr))
                    {
//                      没有其他订阅消息，生成一个新的列表
                        subscribedList = new ArrayList<>();
                    }else {
//                        有订阅消息，取出来,通过JSONArray把JSON字符串类型的数据转换回List
                        subscribedList = JSONArray.parseArray(subscribedListStr,UserMoment.class);
                    }
//                  将当前传入的动态放入订阅的list中，然后把list转为JSON的String放到redis里面
                    subscribedList.add(userMoment);
                    redisTemplate.opsForValue().set(key,JSONObject.toJSONString(subscribedList));
                }

                System.out.println("success");
//                返回成功状态
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
//        启动消费者
        consumer.start();
        return consumer;
    }




    //  弹幕相关生产者
    @Bean("danmusProducer")
    public DefaultMQProducer danmusProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_DANMUS);
        producer.setNamesrvAddr(nameServerAddr);
//        producer.setVipChannelEnabled(false);
//        producer.setSendMsgTimeout(15000);
        producer.start();

//        producer.setSendMsgTimeout(10000);
        return producer;
    }



    //  弹幕相关消费者
    @Bean("danmusConsume")
    public DefaultMQPushConsumer danmusConsumer() throws MQClientException {
//      存储分组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_DANMUS);
//      名称服务器
        consumer.setNamesrvAddr(nameServerAddr);
//      订阅内容，以及二级主题
        consumer.subscribe(TOPIC_DANMUS,"*");
//      添加监听器，生产者把消息推送到MQ，MQ把消息推给消费者，消费者抓取消息，监听器监听到新增内容后进行推送
//        使用并发监听
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
//            并行处理监听器，拿到两个变量消息和上下文
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
//              由于每次添加动态，每次只默认向RocketMQ发送一条数据，因此list中只有一个元素
                MessageExt msg = msgs.get(0);
                String bodyStr = new String(msg.getBody());
//                System.out.println("body:"+bodyStr);

//              将消息转换成JSONObject，然后获取sessionId（存储客户端和服务端回话）和message（弹幕消息）
                JSONObject jsonObject = JSONObject.parseObject(bodyStr);
                String sessionId = jsonObject.getString("sessionId");
                String message = jsonObject.getString("message");
//              通过sessionId取出WebsocketService从而获得会话（session）
                WebSocketService webSocketService = WebSocketService.WEBSOCKET_MAP.get(sessionId);
//              会话开启中，发送消息
                if(webSocketService.getSession().isOpen())
                {
                    webSocketService.sendMsg(message);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
//        启动消费者
        consumer.start();
        return consumer;
    }



}
