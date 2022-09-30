package com.project.bilibili.domain.auth;

import java.util.Date;

public class AuthRoleElementOperation {

    private Long id;

    private Long roleId;

    private Long elementOperationId;

    private Date createTime;

//  有联表 的情况，一个是role，一个是element，直接加进来进行联表查询，封装进去，减少数据库请求
    private AuthElementOperation authElementOperation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getElementOperationId() {
        return elementOperationId;
    }

    public void setElementOperationId(Long elementOperationId) {
        this.elementOperationId = elementOperationId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public AuthElementOperation getAuthElementOperation() {
        return authElementOperation;
    }

    public void setAuthElementOperation(AuthElementOperation authElementOperation) {
        this.authElementOperation = authElementOperation;
    }
}
