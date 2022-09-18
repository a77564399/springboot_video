package com.project.bilibili.domain;

public class JsonResponse<T> {
//    返回状态吗
    private String code;
//    返回提示语
    private String msg;
//
    private T data;

    public JsonResponse(String code,String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    public JsonResponse(T data)
    {
        this.data = data;
        this.code = "0";
        this.msg = "成功";
    }

//   一些不需要返回给前端数据但是请求成功的情况
    public static JsonResponse<String> success(){
        return new JsonResponse<>(null);
    }

//    系统登录成功之后返回给用户令牌的情况
    public static JsonResponse<String> success(String data){
        return new JsonResponse<>(data);
    }


    public static JsonResponse<String> fail(){
        return new JsonResponse<>("1","失败");
    }

//    需要前端定制化code和提示信息
    public static JsonResponse<String> fail(String code,String msg)
    {
        return new JsonResponse<>(code,msg);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
