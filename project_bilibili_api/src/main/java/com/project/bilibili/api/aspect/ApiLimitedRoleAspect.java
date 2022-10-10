package com.project.bilibili.api.aspect;

import com.project.bilibili.api.support.UserSupport;
import com.project.bilibili.domain.User;
import com.project.bilibili.domain.annotation.ApiLimitedRole;
import com.project.bilibili.domain.auth.UserRole;
import com.project.bilibili.exception.ConditionException;
import com.project.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//设置较高优先级以及注入
@Order(1)
//Component可以标注在配置项和工具类这些，和Controller、Service这些本质没有区别
@Component
//切面class
@Aspect
public class ApiLimitedRoleAspect {
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserRoleService userRoleService;

//  设置切点:告诉SpringBoot应该在什么地方进行切入：写正则或者annotation
//  当pointCut里面的注解被实际执行到的时候吗，就会被切入
    @Pointcut("@annotation(com.project.bilibili.domain.annotation.ApiLimitedRole)")
    public void check(){
    }

//    切入切点之后的处理逻辑 织入，由于在进入切面的时候传入了limitedRoleCodeList，需要获取到apiLimitedRole注解中的value并传给方法的参数
//   设置哪些方法之前执行此切点方法
//    定义需要匹配的切点表达式，同时需要匹配参数
    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole)
    {
//      获取用户ID以及其对应的角色列表
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoles = userRoleService.getUserRolesByUserId(userId);
//      获取受限用户的代码列表
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
//      获取当前用户的所有角色，以set(去重)返回
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
//      通过userRoles找出用户的所有roleCode
        Set<String> userRoleCode = userRoles.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
//      两个set比对去重
        limitedRoleCodeSet.retainAll(userRoleCode);
//      如果去重后的limitedRoleCodeSet>0就代表限制角色和当前角色有重合
        if(limitedRoleCodeSet.size()>0)
        {
            throw new ConditionException("权限不足！");
        }
    }
}
