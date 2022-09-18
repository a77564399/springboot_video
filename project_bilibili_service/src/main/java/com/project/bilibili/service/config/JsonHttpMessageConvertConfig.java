package com.project.bilibili.service.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class JsonHttpMessageConvertConfig {
//    public static void main(String[] args) {
//        List<Object> list = new ArrayList<>();
//        Object o = new Object();
//        list.add(o);
//        list.add(o);
//        System.out.println(JSONObject.toJSONString(list));
//        System.out.println(JSONObject.toJSONString(list,SerializerFeature.DisableCircularReferenceDetect));
//    }

//@Bean自动装载
    @Bean
//@primary 优先级较高
    @Primary
    public HttpMessageConverters fastJsonHttpMessageConvertes(){
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(
//              json格式化输出：进行响应的缩进换行等
                SerializerFeature.PrettyFormat,
//              将空值的数据转为空字符串
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteMapNullValue,
//              对map进行排序
                SerializerFeature.MapSortField,
//              禁用循环引用
                SerializerFeature.DisableCircularReferenceDetect
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastConverter);
    }

}
