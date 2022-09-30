package com.project.bilibili.domain.auth;

import java.util.List;

public class UserAuthorities {

    //  操作权限
    List<AuthRoleElementOperation> roleElementOperationList;
//  页面菜单操作权限
    List<AuthRoleMenu> roleMenuList;

    public List<AuthRoleElementOperation> getRoleElementOperationList() {
        return roleElementOperationList;
    }

    public void setRoleElementOperationList(List<AuthRoleElementOperation> roleElementOperationList) {
        this.roleElementOperationList = roleElementOperationList;
    }

    public List<AuthRoleMenu> getRoleMenuList() {
        return roleMenuList;
    }

    public void setRoleMenuList(List<AuthRoleMenu> roleMenuList) {
        this.roleMenuList = roleMenuList;
    }
}
