package com.project.bilibili.api.aspect;

import com.project.bilibili.api.support.UserSupport;
import com.project.bilibili.domain.UserMoment;
import com.project.bilibili.domain.annotation.ApiLimitedRole;
import com.project.bilibili.domain.auth.UserRole;
import com.project.bilibili.domain.constant.AuthRoleConstant;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//设置较高优先级以及注入
@Order(1)
//Component可以标注在配置项和工具类这些，和Controller、Service这些本质没有区别
@Component
//切面class
@Aspect
public class DataLimitedAspect {
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserRoleService userRoleService;

//  设置切点:告诉SpringBoot应该在什么地方进行切入：写正则或者annotation
//  当pointCut里面的注解被实际执行到的时候吗，就会被切入
    @Pointcut("@annotation(com.project.bilibili.domain.annotation.DataLimited)")
    public void check(){

    }

//    切入切点之后的处理逻辑 织入,只需对注解进行判断，无需参数
//   设置哪些方法之前执行此切点方法
    @Before("check()")
    public void doBefore(JoinPoint joinPoint)
    {
//      获取用户ID以及其对应的角色列表
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoles = userRoleService.getUserRolesByUserId(userId);
        Set<String> userRoleCodes = userRoles.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
//      获取切点中的参数(切点也就是要限制的方法，参数也就是方法传入的参数)
        Object args[] = joinPoint.getArgs();
//      对参数进行遍历，如果当前方法传入的参数是我们想要限制的类型
        for(Object arg:args)
        {
//          如果参数是UserMoment类型
            if(arg instanceof UserMoment)
            {
                UserMoment userMoment = (UserMoment) arg;
                String type = userMoment.getType();
//                如果用户的角色里面包含LV0并且type不是0，就返回参数错误
                if(userRoleCodes.contains(AuthRoleConstant.ROLE_LV0) && !"0".equals(type))
                {
                    throw new ConditionException("参数错误！");
                }
            }
        }
    }
}
