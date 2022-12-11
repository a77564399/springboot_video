package com.project.bilibili.api;

import com.project.bilibili.api.support.UserSupport;
import com.project.bilibili.domain.JsonResponse;
import com.project.bilibili.domain.auth.UserAuthorities;
import com.project.bilibili.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class UserAuthApi {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserAuthService userAuthService;

    @GetMapping("/user-authority")
    public JsonResponse<UserAuthorities> getUserAuthorities()
    {
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthorities(userId);
        Set<Character> set = new HashSet<>();
        return new JsonResponse<>(userAuthorities);

    }
}
