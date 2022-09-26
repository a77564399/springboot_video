package com.project.bilibili.service;

import com.project.bilibili.dao.UserMomentsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMomentsService {
    @Autowired
    private UserMomentsDao userMomentsDao;
}
