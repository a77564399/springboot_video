package com.project.bilibili.api;

import com.project.bilibili.api.support.UserSupport;
import com.project.bilibili.domain.FollowingGroup;
import com.project.bilibili.domain.JsonResponse;
import com.project.bilibili.domain.UserFollowing;
import com.project.bilibili.service.UserFollowingService;
import com.project.bilibili.service.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserFollowingApi {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;

//  添加用户关注Api
//  先获取用户ID，通过RequestContext获取即可（因为前端传过来的userFollowing内部不含userId，需要在headeer里面获取id后设置进去）
    @PostMapping("/user-followings")
    public JsonResponse<String> addUserFollowings(@RequestBody UserFollowing userFollowing)
    {
        Long currentUserId = userSupport.getCurrentUserId();
        userFollowing.setUserId(currentUserId);
        userFollowingService.addUserFollowings(userFollowing);
        return JsonResponse.success();
    }

//  获取用户关注列表
    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>> getUserFollowings()
    {
        Long userId = userSupport.getCurrentUserId();
        return new JsonResponse<>(userFollowingService.getUserFollowings(userId));
    }

//  粉丝与关注者不同，粉丝没有分组，直接以following的形式返回
    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans()
    {
        Long userId = userSupport.getCurrentUserId();
        return new JsonResponse<>(userFollowingService.getUserFans(userId));
    }

//  新增用户分组,返回用户分组Id？作用?便于后续继续操作
    @PostMapping("/user-following-group")
    public JsonResponse<Long> addUserFollowingGroup(@RequestBody FollowingGroup followingGroup)
    {
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroup(followingGroup);
        return new JsonResponse(groupId);
    }
//  获取用户分组
    @GetMapping("/user-following-group")
    public JsonResponse<List<FollowingGroup>> getUserFollowingGroup()
    {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> list = userFollowingService.getUserFollowingGroup(userId);
        return new JsonResponse<>(list);
    }

}
