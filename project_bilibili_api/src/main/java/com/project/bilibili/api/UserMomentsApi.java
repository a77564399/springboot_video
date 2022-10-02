package com.project.bilibili.api;

import com.project.bilibili.api.support.UserSupport;
import com.project.bilibili.domain.JsonResponse;
import com.project.bilibili.domain.UserMoment;
import com.project.bilibili.domain.annotation.ApiLimitedRole;
import com.project.bilibili.domain.annotation.DataLimited;
import com.project.bilibili.domain.constant.AuthRoleConstant;
import com.project.bilibili.service.UserMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserMomentsApi {
    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;


    /**
     * 新建用户动态
     */
//  接口权限限制,Lv0不得访问此接口
    @ApiLimitedRole(limitedRoleCodeList = AuthRoleConstant.ROLE_LV0)
    @DataLimited
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception {
        Long userId = userSupport.getCurrentUserId();
//        System.out.println("userId==>"+userId);
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }


    /**
     * 查询用户订阅（关注的用户）的动态
     */
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments(){
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> userMomentList = userMomentsService.getUserSubscribedMoments(userId);
        System.out.println(userMomentList);
        return new JsonResponse<>(userMomentList);
    }

}
