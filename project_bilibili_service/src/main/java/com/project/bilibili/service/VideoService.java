package com.project.bilibili.service;

import com.project.bilibili.dao.VideoDao;
import com.project.bilibili.domain.Video;
import com.project.bilibili.domain.VideoTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class VideoService {
    @Autowired
    private VideoDao videoDao;

    @Transactional
    public void addVideo(Video video) {
        Date now = new Date();
//      video存入数据库
        video.setCreateTime(now);
        videoDao.addVideo(video);
//      获取video的tag并存入数据库
        Long videoId = video.getId();
        List<VideoTag> tagList = video.getVideoTagList();
        tagList.forEach(item->{
            item.setCreateTime(now);
            item.setVideoId(videoId);
        });
        videoDao.batchAddVideoTags(tagList);
    }
}
