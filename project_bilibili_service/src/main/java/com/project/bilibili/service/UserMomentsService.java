package com.project.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.bilibili.dao.UserMomentsDao;
import com.project.bilibili.domain.UserMoment;
import com.project.bilibili.domain.constant.UserMomentsConstant;
import com.project.bilibili.service.utils.RocketMQUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class UserMomentsService {
    @Autowired
    private UserMomentsDao userMomentsDao;

//  可以获取到整个项目中的Bean、配置等
    @Autowired
    private ApplicationContext applicationContext;

//   通过redis取出用户订阅的动态
    @Autowired
    private RedisTemplate redisTemplate;

    public void addUserMoments(UserMoment userMoment) throws Exception {
        userMoment.setCreateTime(new Date());
        userMomentsDao.addUserMoments(userMoment);
//      通过application的getBean获取到RocketMQ的生产者
        DefaultMQProducer producer = (DefaultMQProducer)applicationContext.getBean("momentsProducer");
//      rocket发送消息需要message和producer两方面内容
//      message这块不是很懂，Message需要传入两方面的参数：topic和一个消息体，消息体要求是Bytes数组格式
//      此处message设置的消息的主题，topic设置成Topic-Moments，消息体设置为用户动态，把用户动态对象转换成json格式的字符串然后转成byte数组传入
        Message msg = new Message(UserMomentsConstant.TOPIC_MOMENTS, JSONObject.toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));
//      同步发送消息,进入消息处理
        RocketMQUtil.syncSendMsg(producer,msg);
    }

    public List<UserMoment> getUserSubscribedMoments(Long userId) {
//      用户动态相关内容都放在了Redis中，所以直接从Redis里面取即可
//        System.out.println("user"+userId);
        String key = "subscribed-"+userId;
        System.out.println(key);
        String listStr = (String) redisTemplate.opsForValue().get(key);
//        System.out.println(listStr);
//      将字符串转换成list并返回
        return JSONArray.parseArray(listStr,UserMoment.class);
    }
}
