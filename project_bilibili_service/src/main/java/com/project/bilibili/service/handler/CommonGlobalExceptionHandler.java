package com.project.bilibili.service.handler;


import com.project.bilibili.domain.JsonResponse;
import com.project.bilibili.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
//设置优先级：（全局）优先级最高
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommonGlobalExceptionHandler {
//    表示异常处理器，全局异常，只要是异常他就会处理
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request,Exception e)
    {
        String errorMsg = e.getMessage();
//      如果是正常的exception类型，那么就直接返回给前端状态码，让他直接显示错误信息即可
        if(e instanceof ConditionException)
        {
            String errorCode = ((ConditionException)e).getCode();
            return new JsonResponse<>(errorCode,errorMsg);
        }else {
            e.printStackTrace();
//           如果不是condition类型，那么就新建一种类型，状态码为500
            return new JsonResponse<>("500",errorMsg);
        }
    }
}
