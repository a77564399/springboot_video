package com.project.bilibili.api;

import com.alibaba.fastjson.JSONObject;
import com.project.bilibili.api.support.UserSupport;
import com.project.bilibili.domain.JsonResponse;
import com.project.bilibili.domain.PageResult;
import com.project.bilibili.domain.User;
import com.project.bilibili.domain.UserInfo;
import com.project.bilibili.service.UserFollowingService;
import com.project.bilibili.service.UserService;
import com.project.bilibili.service.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserApi {
    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserFollowingService userFollowingService;

//   获取RSA公钥

    /**
     * 获取公钥
     * @return
     */
    @GetMapping("/rsa-pks")
    private JsonResponse<String> getRsaPublicKety()
    {
        String pk = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(pk);
    }

//   通过userId获取用户信息
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo(){
        Long userId = userSupport.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        return new JsonResponse<>(user);
    }

    /**
     * 添加用户信息
     * @param user
     * @return
     */
    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user)
    {
        System.out.println(user.getPassword());
        userService.addUser(user);
        return JsonResponse.success();
    }

    /**
     * 登录方法
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return new JsonResponse<>(token);
    }

    @PutMapping("/user")
    public JsonResponse<String> updateUser(@RequestBody User user) throws Exception {
//        System.out.println(user.getId());
        Long userId = userSupport.getCurrentUserId();
        user.setId(userId);
        userService.updateUsers(user);
        return JsonResponse.success();
    }

    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfos(@RequestBody UserInfo userInfo)
    {
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfos(userInfo);
        return JsonResponse.success();
    }

    /**
     * JSONObject fastJson里面封装的一个类，可以直接当作map使用
     * 分页查询用户信息api：可以使用昵称模糊匹配
     * @param no:页数
     * @param size：每页的数据条数
     * @param nick：根据昵称模糊查询
     * @return
     */
    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer no,@RequestParam Integer size, String nick)
    {
//      获取当前用户Id
        Long userId = userSupport.getCurrentUserId();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("no",no);
        jsonObject.put("size",size);
        jsonObject.put("nick",nick);
//     分页获取用户信息
        PageResult<UserInfo> pageResult = userService.pageListUserInfos(jsonObject);
//      将用户的关注状态添加进去：与关注表关联，所以使用userService中的方法
        if(pageResult.getTotal()>0)
        {
            List<UserInfo> userInfoList = userFollowingService.checkFollowingStatus(pageResult.getList(),userId);
//          取出来，检查完，放进去
            pageResult.setList(userInfoList);
        }

        return new JsonResponse<>(pageResult);
    }
}
