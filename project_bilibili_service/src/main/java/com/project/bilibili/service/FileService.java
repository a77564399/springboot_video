package com.project.bilibili.service;

import com.project.bilibili.dao.FileDao;
import com.project.bilibili.domain.File;
import com.project.bilibili.service.utils.FastDFSUtil;
import io.netty.util.internal.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Service
public class FileService {
    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private FileDao fileDao;

    public String uploadFileBySlices(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalNo) throws Exception {
//      使用MD5获取文件
        File dbFileMD5 = fileDao.getFileByMD5(fileMd5);
//        有文件，直接返回文件的uel
        if(dbFileMD5!=null)
        {
            return dbFileMD5.getUrl();
        }
//      没有文件就上传并组装好放入数据库
        String url = fastDFSUtil.uploadFileBySlices(slice,fileMd5,sliceNo,totalNo);
        if(!StringUtil.isNullOrEmpty(url))
        {
            dbFileMD5 = new File();
            dbFileMD5.setMd5(fileMd5);
            dbFileMD5.setCreateTime(new Date());
            dbFileMD5.setUrl(url);
            dbFileMD5.setType(fastDFSUtil.getFileType(slice));
            fileDao.addFile(dbFileMD5);
        }
        return url;
    }

    public String getFileMD5(MultipartFile file) throws IOException {
//      输入流
        InputStream inputStream = file.getInputStream();
//      输出流：使用比特数组相关的输出流，方便后续进行MD5加密
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int byteRead;
        while ((byteRead=inputStream.read(buffer))>0)
        {
//          以每次1024byte进行读取，到buffer中，然后将其输入到byteArray
            byteArrayOutputStream.write(buffer,0,byteRead);
        }
        inputStream.close();
//      参考md5util中的加密方式，此处对byte数组进行加密
        return DigestUtils.md5Hex(byteArrayOutputStream.toByteArray());
    }
}
