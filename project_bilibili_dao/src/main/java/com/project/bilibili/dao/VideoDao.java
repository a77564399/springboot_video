package com.project.bilibili.dao;

import com.project.bilibili.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Mapper
public interface VideoDao {
    Integer addVideo(Video video);
//    视频标签类
    Integer batchAddVideoTags(List<VideoTag> videoTagList);

    Integer pageCountVideos(Map<String, Object> params);

    List<Video> pageListVideos(Map<String, Object> params);

    Video getVideoById(Long videoId);

    VideoLike getVideoLikeByVideoIdAndUserId(@Param("videoId") Long videoId,@Param("userId") Long userId);

    Integer deleteVideoLike(@Param("videoId") Long videoId,@Param("userId") Long userId);

    Long getVideoLikes(Long videoId);

    Integer deleteVideoCollection(@Param("videoId") Long videoId,@Param("userId") Long userId);

    Integer addVideoCollection(VideoCollection videoCollection);

    Long getVideoCollectionCounts(Long videoId);

    VideoCollection getVideoCollection(@RequestParam("videoId") Long videoId,@RequestParam("userId") Long userId);

    VideoCoin getVideoCoinByVideoIdAndUserId(@Param("videoId") Long videoId,@Param("userId") Long userId);

    Integer addVideoCoin(VideoCoin videoCoin);

    Integer updateVideoCoin(VideoCoin videoCoin);

    Integer addVideoComment(VideoComment videoComment);

    Integer pageCountVideoComments(Map<String, Object> param);

    List<VideoComment> pageVideoComments(Map<String, Object> param);

    List<VideoComment> batchGetVideoCommentsByRootId(List<Long> parentIdList);
}
