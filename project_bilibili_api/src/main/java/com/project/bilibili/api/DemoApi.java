package com.project.bilibili.api;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.project.bilibili.service.feign.MsDeclareService;
import com.project.bilibili.service.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.project.bilibili.service.DemoService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class DemoApi {
    @Autowired
    private DemoService demoService;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private MsDeclareService msDeclareService;

    @GetMapping("/query")
    public String getName(int id)
    {
        return demoService.queryName(id);
    }

    @GetMapping("/getDemo")
    public String getDemo(String id)
    {
        return "123";
    }

    @GetMapping("/slices")
    public void slices(MultipartFile file) throws IOException {
        fastDFSUtil.convertFileToSlice(file);
    }

    @GetMapping("/demos")
    public Long msget(@RequestParam Long id)
    {
        return msDeclareService.msget(id);
    }

    @PostMapping("/demos")
    public Map<String,Object> mspost(@RequestBody Map<String,Object> params)
    {
        return msDeclareService.mspost(params);
    }

    @GetMapping("/demoxx1")
    public Long msget1()
    {
        return msDeclareService.msget1();
    }


//    fallbackMethod:服务降级的时候调用的方法，commandProperties,指定一些熔断器属性(超时时间)
    @HystrixCommand(fallbackMethod = "error",
            commandProperties = {
                @HystrixProperty(
//                      属性名称:2s后超时熔断
                        name = "execution.isolation.thread.timeoutInMilliseconds",
                        value = "2000"
                )
            })
    @GetMapping("/timeout")
    public String circuitBreakerWithHystrix(@RequestParam Long time)
    {
        return msDeclareService.timeout(time);
    }

    public String error(Long time)
    {
        return "超时出错";
    }

}
