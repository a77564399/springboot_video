package com.project.bilibili.service;

import com.mysql.cj.util.StringUtils;
import com.project.bilibili.dao.UserDao;
import com.project.bilibili.domain.JsonResponse;
import com.project.bilibili.domain.User;
import com.project.bilibili.domain.UserInfo;
import com.project.bilibili.exception.ConditionException;
import com.project.bilibili.service.utils.MD5Util;
import com.project.bilibili.service.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public void addUser(User user) {
        String phone = user.getPhone();
        if(StringUtils.isNullOrEmpty(phone))
        {
            throw new ConditionException("手机号不能为空！");
        }
        User dbUser = this.getUserByPhone(phone);
        if(dbUser != null)
        {
            throw new ConditionException("该手机号已被注册");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPassword;
//        前端传过来的是经过RSA加密的password，解密之后使用MD5加密，放入数据库
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }

        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);

//      添加用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.getNick()

    }

    public User getUserByPhone(String phone)
    {
        return userDao.getUserByPhone(phone);
    }
}
