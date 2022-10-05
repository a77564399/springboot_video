package com.project.bilibili.dao;

import com.project.bilibili.domain.Video;
import com.project.bilibili.domain.VideoTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoDao {
    Integer addVideo(Video video);
//    视频标签类
    Integer batchAddVideoTags(List<VideoTag> videoTagList);
}
