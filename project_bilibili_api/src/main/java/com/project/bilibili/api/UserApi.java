package com.project.bilibili.api;

import com.project.bilibili.domain.JsonResponse;
import com.project.bilibili.domain.User;
import com.project.bilibili.service.UserService;
import com.project.bilibili.service.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserApi {
    @Autowired
    private UserService userService;

    @GetMapping("/rsa-pks")
    private JsonResponse<String> getRsaPublicKety()
    {
        String pk = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(pk);
    }

    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user)
    {
        userService.addUser(user);
    }

}
