package com.project.bilibili.api;

import com.project.bilibili.domain.JsonResponse;
import com.project.bilibili.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileApi {
    @Autowired
    private FileService fileService;

    /**
     * 文件分片方法
     * @param slice：一片文件
     * @param fileMd5：文件的MD5
     * @param sliceNo：第几片？
     * @param totalNo：总共几片？
     * @return
     * @throws Exception
     */
    @PutMapping("/file-slices")
    public JsonResponse<String> uploadFileBySlices(MultipartFile slice,
                                                   String fileMd5,
                                                   Integer sliceNo,
                                                   Integer totalNo) throws Exception {
        String filePath = fileService.uploadFileBySlices(slice,fileMd5,sliceNo,totalNo);
        return new JsonResponse<>(filePath);
    }


    @PostMapping("/md5files")
    public JsonResponse<String> getFileMD5(MultipartFile file) throws IOException {
        String fileMD5 = fileService.getFileMD5(file);
        return new JsonResponse<>(fileMD5);
    }



}
