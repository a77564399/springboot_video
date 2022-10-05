package com.project.bilibili.service.utils;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.project.bilibili.exception.ConditionException;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Component
public class FastDFSUtil {
    /**
     * 注册自带工具，其中的upload放啊不适合大文件的上传，断网后会丢失
     */
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

//  默认分组
    private static final String DEFAULT_GROUP = "group1";

    private static final String PATH_KEY = "path-key:";

    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";
    private static final String UPLOADED_NO_KEY = "uploaded-no-key:";
//  分片大小，单位kb 2MB
    private static final int SLICE_SIZE = 2*1024*1024;

    //    使用redis进行记录
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Value("${remote.addr}")
    private String remoteIp;

    private static final String DFS_PORT = "8999";

    /**
     * 获取文件类型
     * MultipartFile:spring定义的一种文件格式，与file无本质区别
     * @return
     */
    public String getFileType(MultipartFile file)
    {
        if(file==null)
        {
            /**
             * 黑客暴力破解的时候会使用抓破获取所有接口传输的信息，异常提示写的越详细就越容易被才出来后端的逻辑
             */
            throw new ConditionException("非法文件！");
        }

        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index+1);
    }


    //上传
    public String uploadCommenFile(MultipartFile file) throws Exception {
        /**
         *MetaData:存储文件的一些相关信息,可以写可不写
         */
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = getFileType(file);
        //使用fastFileStorageClient中自带的uploadFile方法，前面获取前其需要的参数，上传完成后获取到存储的路径
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }


//    可以断点续传的文件:前端进行分片，后端直接进行

    /**
     * uploadAppenderFile上传第一个分片，获取路径然后使用modifyAppenderFile继续上传
     * 实际使用：对文件进行md5加密，进行秒传
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadAppenderFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String fileType = getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

//  分片文件添加
    public void modifyAppenderFile(MultipartFile file,String filePath,long offset) throws Exception {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP,filePath,file.getInputStream(),file.getSize(),offset);
    }


    public String uploadFileBySlices(MultipartFile file,String fileMd5,Integer sliceNo,Integer totalNo) throws Exception {
        if(file==null||sliceNo==null||totalNo==null)
        {
            throw new ConditionException("参数异常");
        }
//      当前上传文件路径的key
        String pathKey = PATH_KEY+fileMd5;
//      已经上传了的文件的大小
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
//       目前已经上传的分片数目
        String uploadedNoKey = UPLOADED_NO_KEY+fileMd5;
        System.out.println(uploadedSizeKey);
//      判断当前有没有已经传输的分片的大小
        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        Long uploadedSize = 0L;
//        判断是否是上传的第一个分片
        if(!StringUtil.isNullOrEmpty(uploadedSizeStr))
        {
//          不是第一个分片，将当前文件已上传的大小进行赋值
            uploadedSize = Long.valueOf(uploadedSizeStr);
        }
        String type = getFileType(file);
//      判断是否是第一个分片
        if(sliceNo==1)
        {
            String path = this.uploadAppenderFile(file);
            if(StringUtil.isNullOrEmpty(path))
            {
                throw new ConditionException("上传失败");
            }

//          把path保存在redis中
            redisTemplate.opsForValue().set(pathKey,path);
//          添加分片数到redis:因为判断了是第一片，直接写1
            redisTemplate.opsForValue().set(uploadedNoKey,"1");
        }else {
//          不是第一个分片的情况
//          获取当前文件已上传的路径
            String filePath = redisTemplate.opsForValue().get(pathKey);
//          判断path是否正确
            if(StringUtil.isNullOrEmpty(filePath))
                throw new ConditionException("上传失败！");
//          继续上传文件
//          前面获取已经上传的进度uploadedSize，也就是偏移量
            System.out.println(uploadedSize);
            this.modifyAppenderFile(file,filePath,uploadedSize);
//          更新redis中的值
//          上传的分片数目+1
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }
        //设置上传了文件的大小
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey,String.valueOf(uploadedSize));
//      判断上传是否可以结束(分片是否是最后一个)
        String upLoadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        Integer upLoadedNo = Integer.parseInt(upLoadedNoStr);
//      最后一个分片备用，如果是最后一个分片，就把路径赋值给临时变量并返回（因为内部直接删除了）
        String resultPath = "";
        System.out.println("upNo:"+upLoadedNo);
        System.out.println("total:"+totalNo);
        if(upLoadedNo.equals(totalNo))
        {
            resultPath = remoteIp+":"+DFS_PORT+"/"+DEFAULT_GROUP+"/"+redisTemplate.opsForValue().get(pathKey);
            System.out.println(resultPath);
//          是最后一个分片就清空相关参数
//          使用list存储所有的key，然后使用redis的相关方法一次性删除
            List<String> keyList = Arrays.asList(pathKey, uploadedSizeKey, uploadedNoKey);
            redisTemplate.delete(keyList);
        }
        return resultPath;
    }


//   分片方法
    public void convertFileToSlice(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String fileType = getFileType(file);
        File tempFile = this.multipartFileToFile(file);
        long fileLength = tempFile.length();
//      分片计数
        int count=1;
        for (int i = 0; i < fileLength; i+=SLICE_SIZE) {
//          RandomAccessFile:随机访问文件，可以从文件的任意位置访问文件
            RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "r");
//          定位起始点
            randomAccessFile.seek(i);
            byte[] bytes = new byte[SLICE_SIZE];
//          最后一个分片大小小于2M需要获取一下长度
            int len = randomAccessFile.read(bytes);
            String path = "D:\\javaweb-related\\tempFile\\"+count+"."+fileType;
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes,0,len);
//          关闭流
            fos.close();
            randomAccessFile.close();
//          分片数+1
            count++;
        }
//      删除临时文件
        tempFile.delete();
    }

//   将multifile转为file
    public File multipartFileToFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String[] fileName = originalFilename.split("\\.");
//      生成一个名称和multi一样的临时文件，用于写入。createTempFile生成文件不自动加.，需要手动加一下
        File file1 = File.createTempFile(fileName[0], "."+fileName[1]);
        file.transferTo(file1);
        return file1;
    }


    //删除
    public void deleteFile(String filePath)
    {
        fastFileStorageClient.deleteFile(filePath);
    }


}
