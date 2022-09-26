package com.project.bilibili.api.support;

import com.project.bilibili.exception.ConditionException;
import com.project.bilibili.service.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//Bean注入
@Component
//与用户相关的支撑方法
public class UserSupport {
//    获取UserID
    public Long getCurrentUserId(){
//      通过抓取请求上下文方法获取相关信息
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        token保存在请求头中，因此
        String token = requestAttributes.getRequest().getHeader("token");
//        System.out.println(token);
//       通过token获取UserID
        Long userId = TokenUtil.vertifyToken(token);
//        由于UserId是数据库生成，不会有小于0的数，验证一下
        if(userId<0)
        {
            throw new ConditionException("非法用户！");
        }
        return userId;
    }
}
