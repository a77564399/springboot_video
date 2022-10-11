package com.project.bilibili.api;

import com.project.bilibili.api.support.UserSupport;
import com.project.bilibili.domain.Danmu;
import com.project.bilibili.domain.JsonResponse;
import com.project.bilibili.service.DanmuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
public class DanmuApi {
    @Autowired
    private DanmuService danmuService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 获取弹幕
     */
    @GetMapping("/danmus")
    public JsonResponse<List<Danmu>> getDanmus(@RequestParam Long videoId,String startTime,String endTime) throws ParseException {
        List<Danmu> list;
//      若是登录用户则可以删选时间段查看弹幕，非登陆用户不可以
        try {
            userSupport.getCurrentUserId();
            list = danmuService.getDanmus(videoId,startTime,endTime);
        }catch (Exception ignore)
        {
            list = danmuService.getDanmus(videoId,null,null);
        }
        return new JsonResponse<>(list);
    }
}
