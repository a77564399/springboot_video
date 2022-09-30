package com.project.bilibili.dao;

import com.project.bilibili.domain.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface AuthRoleElementOperationDao {

//    由于参数是列表类型，因此要指定参数名称，让Mybatis知道引入列表类型且实际参数名不是list，而是指定的名称
    List<AuthRoleElementOperation> getRoleElementsByRoleIds(@Param("roleSet") Set<Long> roleSet);
}
