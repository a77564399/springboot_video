package com.project.bilibili.service;

import com.project.bilibili.dao.AuthRoleElementOperationDao;
import com.project.bilibili.domain.auth.AuthRoleElementOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleElementOperationService {

    @Autowired
    private AuthRoleElementOperationDao authRoleElementOperationDao;
    public List<AuthRoleElementOperation> getRoleElementsByRoleIds(Set<Long> roleSet) {
//        for(Long i:roleSet)
//        {
//            System.out.println(i);
//        }
//        System.out.println(roleSet.size());
//        System.out.println(roleSet.size());
        return authRoleElementOperationDao.getRoleElementsByRoleIds(roleSet);
    }
}
