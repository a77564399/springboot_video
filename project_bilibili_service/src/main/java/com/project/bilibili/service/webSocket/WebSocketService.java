package com.project.bilibili.service.webSocket;

import com.alibaba.fastjson.JSONObject;
import com.project.bilibili.domain.Danmu;
import com.project.bilibili.domain.UserMoment;
import com.project.bilibili.domain.constant.UserMomentsConstant;
import com.project.bilibili.service.DanmuService;
import com.project.bilibili.service.config.WebSocketConfig;
import com.project.bilibili.service.utils.RocketMQUtil;
import com.project.bilibili.service.utils.TokenUtil;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Component
//注解注明是与websocket相关的服务类，value是一个路径(api)
@ServerEndpoint("/imserver/{token}")
public class WebSocketService {
//  把这个类放到logger中，从而获取这个类的日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//   AtomicInteger原子性操作类，在高并发过程中线程不一定安全，atomic保证原子性和线程安全
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

//   ConcurrentHashMap保证线程安全的map,保存每个客户端获取的websocket，因为springBoot是单例模式，websocket需要多例，因此使用一个map进行保存
    public static final ConcurrentHashMap<String,WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();

//  获取当前客户端的session，通过唯一标识在map中获取到websocket，然后获取到session
    private Session session;

//   用于唯一标识当前的客户端
    private String sessionId;

//  由于springBoot使用的是单例模式，因此一个客户端获取了webSocket之后，后面的客户端就不会再有WebSocket了，因此要采用全员公用的方式进行注入
//    @Autowired
//    RedisTemplate<String,String> redisTemplate;

//  使用静态成员变量，全部客户端共享
    private static ApplicationContext APPLIICATION_CONTEXT;

//  userId
    private Long userId;
//  在启动类中调用方法将app进行注入
    public static void setApplicationContext(ApplicationContext applicationContext)
    {
        WebSocketService.APPLIICATION_CONTEXT = applicationContext;
    }
    /**
     * websocket建立连接
     * @param session
     */
//  标识webSocket建立连接
    @OnOpen
    public void openConnection(Session session,@PathParam("token") String token)
    {
//      通过共享的applicationContext上下文来获取redis
        RedisTemplate<String,String> redisTemplate = (RedisTemplate<String, String>) WebSocketService.APPLIICATION_CONTEXT.getBean("redisTemplate");

//      通过token获取到userId
        try {
            this.userId = TokenUtil.vertifyToken(token);
        }catch (Exception e){}

//      通过session获取当前客户端的唯一标识sessionId
        sessionId = session.getId();
        this.session = session;
        if(WEBSOCKET_MAP.containsKey(sessionId))
        {
//         当前客户端连接过，重新更新
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId,this);
        }else
        {
//            当前客户端放入map
            WEBSOCKET_MAP.put(sessionId,this);
//            当前在线人数++
            ONLINE_COUNT.getAndIncrement();
        }
        logger.info("用户连接成功："+sessionId+",当前在线人数为："+ONLINE_COUNT.get());
        try {
            this.sendMsg("0");
        }catch (Exception e){
            logger.error("连接异常");
        }
    }


    /**
     * websocket关闭连接（当页面关闭或者刷新）
     */
    @OnClose
    public void closeConnection(Session session)
    {
//      map中的客户端移除
        if(WEBSOCKET_MAP.containsKey(sessionId))
        {
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }
        logger.info("用户退出："+sessionId+"当前在线人数为："+ONLINE_COUNT.get());
    }

//    /**
//     * 当前端把消息发到后端(有消息通信)
//     */
//    @OnMessage
//    public void onMessage(String message)
//    {
//
//        logger.info("用户信息："+sessionId+",报文："+message);
////      收到弹幕消息，将弹幕群发给所有正在连接的客户端
//        if(!StringUtil.isNullOrEmpty(message))
//        {
//            try {
////              群发消息
//                for(Map.Entry<String,WebSocketService> entry: WebSocketService.WEBSOCKET_MAP.entrySet())
//                {
//                    WebSocketService webSocketService = entry.getValue();
////                   当前遍历的客户端session正在打开，就发送消息
//                    if(webSocketService.session.isOpen())
//                    {
//                        webSocketService.sendMsg(message);
//                    }
//                }
//                //发送弹幕只有登录用户才可以，因此要获取到userId(openSession中获取)
//                if(this.userId!=null)
//                {
////                  保存弹幕到数据库
//                    Danmu danmu = JSONObject.parseObject(message,Danmu.class);
//                    danmu.setUserId(userId);
//                    danmu.setCreateTime(new Date());
//                    DanmuService danmuService = (DanmuService) APPLIICATION_CONTEXT.getBean("danmuService");
//                    danmuService.addDanmu(danmu);
//                    //将弹幕保存到数据库和redis
//                    danmuService.addDanmusToRedis(danmu);
//                }
//            }catch (Exception e){
//                logger.error("弹幕接收出现问题");
//                e.printStackTrace();
//            }
//        }
//    }




    /**
     * 当前端把消息发到后端(有消息通信)
     * 性能优化
     */
    @OnMessage
    public void onMessage(String message)
    {

        logger.info("用户信息："+sessionId+",报文："+message);
//      收到弹幕消息，将弹幕群发给所有正在连接的客户端
        if(!StringUtil.isNullOrEmpty(message))
        {
            try {
//              群发消息
                for(Map.Entry<String,WebSocketService> entry: WebSocketService.WEBSOCKET_MAP.entrySet())
                {
                    WebSocketService webSocketService = entry.getValue();
//                  生产消息并放到rocketMQ的生产者中
                    DefaultMQProducer danmuProducer = (DefaultMQProducer) APPLIICATION_CONTEXT.getBean("danmuProducer");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message",message);
                    jsonObject.put("sessionId",webSocketService.getSessionId());
//                  根据userMoment的构建中，找到producer中message的构建过程,输入topic和转成byte数组的JSONObject
                    Message msg = new Message(UserMomentsConstant.TOPIC_DANMUS,JSONObject.toJSONString(jsonObject).getBytes(StandardCharsets.UTF_8));
//                  使用异步发送消息(要用异步并发)
                    RocketMQUtil.asyncSendMsg(danmuProducer,msg);

                }
                //发送弹幕只有登录用户才可以，因此要获取到userId(openSession中获取)
                if(this.userId!=null)
                {
//                  保存弹幕到数据库
                    Danmu danmu = JSONObject.parseObject(message,Danmu.class);
                    danmu.setUserId(userId);
                    danmu.setCreateTime(new Date());
                    DanmuService danmuService = (DanmuService) APPLIICATION_CONTEXT.getBean("danmuService");
                    danmuService.asyncAddDanmu(danmu);
                    //将弹幕保存到数据库和redis
                    danmuService.addDanmusToRedis(danmu);
                }
            }catch (Exception e){
                logger.error("弹幕接收出现问题");
                e.printStackTrace();
            }
        }
    }

//  每隔5秒发送一次
    @Scheduled(fixedRate = 5000)
    private void noticeOnlineCount() throws IOException{
//      通知每一个客户端
        for(Map.Entry<String,WebSocketService> entry:WebSocketService.WEBSOCKET_MAP.entrySet())
        {
            WebSocketService webSocketService = entry.getValue();
            if(webSocketService.getSession().isOpen())
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("onlineCount",ONLINE_COUNT.get());
                jsonObject.put("msg","当前在线人数为"+ONLINE_COUNT.get());
                webSocketService.sendMsg(jsonObject.toJSONString());
            }
        }
    }



    /**
     * 处理过程中遇到错误的时候
     * @param error
     */
    @OnError
    public void onError(Throwable error)
    {

    }

    public void sendMsg(String message) {
//      通过session中自带的发送信息的方法进行发送(非重点)
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Session getSession() {
        return session;
    }

    public String getSessionId() {
        return sessionId;
    }
}
