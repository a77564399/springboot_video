package com.project.bilibili.dao;

import com.project.bilibili.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

import javax.management.relation.Role;

@Mapper
public interface AuthRoleDao {
    AuthRole getRoleByCode(String roleCode);
}