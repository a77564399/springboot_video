package com.project.bilibili.service;

import com.project.bilibili.dao.UsercoinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UsercoinService {

    @Autowired
    private UsercoinDao usercoinDao;
    public Integer getUserCoinsAmount(Long userId) {
        return usercoinDao.getUserCoinsAmount(userId);
    }

    public void updateUserCoin(Long userId, Integer amount) {
        Date updateTime = new Date();
        usercoinDao.updateUserCoin(userId,amount,updateTime);
    }
}
