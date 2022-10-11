package com.project.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.bilibili.dao.DanmuDao;
import com.project.bilibili.domain.Danmu;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DanmuService {

    private static final String DANMU_KEY = "dm-video-";

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    private DanmuDao danmuDao;



    /**
     * 查询弹幕：优先查redis中的弹幕数据，没有查到的话查询数据库，然后把查询到的数据写入到redis中
     * @param videoId
     * @param startTime
     * @param endTime
     * @return
     */

    public List<Danmu> getDanmus(Long videoId, String startTime, String endTime) throws ParseException {
//      先从redis中查询弹幕
        String key = DANMU_KEY + videoId;
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> list;
        if(!StringUtil.isNullOrEmpty(value))
        {
//          redis中存在当前视频弹幕,json对象转对象
            list = JSONArray.parseArray(value,Danmu.class);
            if(!StringUtil.isNullOrEmpty(startTime)&&!StringUtil.isNullOrEmpty(endTime))
            {
//              转变开始结束时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
//              查找符合时间的弹幕
                List<Danmu> childList = new ArrayList<>();
                for(Danmu danmu:list){
                    Date createTime = danmu.getCreateTime();
                    if(createTime.after(startDate)&&createTime.before(endDate))
                    {
                        childList.add(danmu);
                    }
                }
                list = childList;
            }
        }else {
//          redis里面没有这些弹幕，从数据库中进行查找并存入redis
            Map<String,Object> param = new HashMap<>();
            param.put("videoId",videoId);
            param.put("startTime",startTime);
            param.put("endTime",endTime);
            list = danmuDao.getDanmus(param);
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
        }
        return list;
    }




    public void addDanmusToRedis(Danmu danmu)
    {
        String key = DANMU_KEY+danmu.getVideoId();
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> list = new ArrayList<>();
        if(!StringUtil.isNullOrEmpty(value))
        {
            list = JSONArray.parseArray(value,Danmu.class);
        }
        list.add(danmu);
        redisTemplate.opsForValue().set(key,JSONObject.toJSONString(list));
    }

    /**
     * 保存弹幕到数据库
     */
    public void addDanmu(Danmu danmu) {
        danmuDao.addDanmu(danmu);
    }

    /**
     * 由于用户对弹幕的保存需求没有那么大，因此可以保存不成功，可以使用异步方式调用
     */
    @Async
    public void asyncAddDanmu(Danmu danmu) {
        danmuDao.addDanmu(danmu);
    }
}
