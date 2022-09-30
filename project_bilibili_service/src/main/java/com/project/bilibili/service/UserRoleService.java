package com.project.bilibili.service;

import com.project.bilibili.dao.UserRoleDao;
import com.project.bilibili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserRoleService {
    @Autowired
    private UserRoleDao userRoleDao;
    public List<UserRole> getUserRolesByUserId(Long userId) {
        List<UserRole> userRoles = userRoleDao.getUserRolesByUserId(userId);
        return userRoles;
    }

    public void addUserRole(UserRole userRole) {
        userRole.setCreateTime(new Date());
        userRoleDao.addUserRole(userRole);
    }
}
