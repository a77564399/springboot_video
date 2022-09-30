package com.project.bilibili.service;

import com.project.bilibili.dao.AuthRoleDao;
import com.project.bilibili.domain.auth.AuthRole;
import com.project.bilibili.domain.auth.AuthRoleElementOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.List;
import java.util.Set;

@Service
public class RoleAuthService {
    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;

    @Autowired
    private AuthRoleDao authRoleDao;
    public List<AuthRoleElementOperation> getRoleElementsByRoleIds(Set<Long> roleSet) {
        return authRoleElementOperationService.getRoleElementsByRoleIds(roleSet);
    }

    public AuthRole getRoleByCode(String roleCode) {
        return authRoleDao.getRoleByCode(roleCode);
    }
}
