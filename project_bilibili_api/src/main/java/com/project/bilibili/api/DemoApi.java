package com.project.bilibili.api;

import com.project.bilibili.service.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.bilibili.service.DemoService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class DemoApi {
    @Autowired
    private DemoService demoService;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @GetMapping("/query")
    public String getName(int id)
    {
        return demoService.queryName(id);
    }

    @GetMapping("/slices")
    public void slices(MultipartFile file) throws IOException {
        fastDFSUtil.convertFileToSlice(file);
    }

}
