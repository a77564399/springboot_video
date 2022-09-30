package com.project.bilibili.service;

import com.project.bilibili.dao.AuthRoleMenuDao;
import com.project.bilibili.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleMenuService {
    @Autowired
    private AuthRoleMenuDao authRoleMenuDao;
    public List<AuthRoleMenu> getRoleMenusByRoleIds(Set<Long> roleSet) {
        return authRoleMenuDao.getRoleMenusByRoleIds(roleSet);
    }
}
