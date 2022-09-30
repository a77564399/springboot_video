package com.project.bilibili.dao;

import com.project.bilibili.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface AuthRoleMenuDao {

    List<AuthRoleMenu> getRoleMenusByRoleIds(@Param("roleSet") Set<Long> roleSet);
}
