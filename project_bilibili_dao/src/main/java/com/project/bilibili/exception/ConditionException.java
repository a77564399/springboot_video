package com.project.bilibili.exception;

import java.io.Serializable;

public class ConditionException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String code;
    public ConditionException(String name)
    {
        super(name);
        code="500";
    }
//  特殊错误可以使用500之外的状态码进行处理
    public ConditionException(String code,String name)
    {
        super(name);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
