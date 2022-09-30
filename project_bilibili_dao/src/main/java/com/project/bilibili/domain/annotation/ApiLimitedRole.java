package com.project.bilibili.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

//有效阶段：运行期
@Retention(RetentionPolicy.RUNTIME)
//注解位置：方法上
@Target({ElementType.METHOD})
//文档内包含，Spring注册
@Documented
@Component
public @interface ApiLimitedRole {
    String[] limitedRoleCodeList() default {};
}
