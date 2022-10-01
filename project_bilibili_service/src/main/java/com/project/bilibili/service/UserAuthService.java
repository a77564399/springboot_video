package com.project.bilibili.service;

import com.project.bilibili.domain.User;
import com.project.bilibili.domain.auth.*;
import com.project.bilibili.domain.constant.AuthRoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAuthService {


    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleAuthService roleAuthService;

    @Autowired
    private AuthRoleMenuService authRoleMenuService;
    public UserAuthorities getUserAuthorities(Long userId) {
//       根据userId获取其角色关联，可能不止一个，所以返回列表
        List<UserRole> userRoles = userRoleService.getUserRolesByUserId(userId);
//        System.out.println(userRoles.size());
//       获取到用户对应的所有角色的Ids
        Set<Long> roleSet = userRoles.stream().map(UserRole::getUserId).collect(Collectors.toSet());
//       通过roleid的set获取到userAuthorities的所有相关组件：AuthRoleElementOperation，AuthRoleMenu，当然都是list，即这个用户可以的所有操作和前端使用的所有按钮
        List<AuthRoleElementOperation> authRoleElementOperations = roleAuthService.getRoleElementsByRoleIds(roleSet);
        List<AuthRoleMenu> authRoleMenus = authRoleMenuService.getRoleMenusByRoleIds(roleSet);
//       新建用户权限对象，将查询出来的内容赋值进去
        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleElementOperationList(authRoleElementOperations);
        userAuthorities.setRoleMenuList(authRoleMenus);
        return userAuthorities;
    }

    //新建默认角色(LV0)，把用户ID和角色ID一块传到数据库表即可
    public void addDefaultUserRole(Long id) {
        UserRole userRole = new UserRole();
//      通过常量中LV0的code获取到ID(严谨)
        AuthRole role = roleAuthService.getRoleByCode(AuthRoleConstant.ROLE_LV0);
//        设置id和roleid
        userRole.setUserId(id);
        userRole.setRoleId(role.getId());
        userRoleService.addUserRole(userRole);
    }
}
