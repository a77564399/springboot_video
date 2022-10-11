package com.project.bilibili.service;

import com.project.bilibili.dao.VideoDao;
import com.project.bilibili.domain.*;
import com.project.bilibili.exception.ConditionException;
import com.project.bilibili.service.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoService {
    @Autowired
    private VideoDao videoDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private UsercoinService usercoinService;

    @Autowired
    private UserService userService;

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

    /**
     *
     * @param size
     * @param no
     * @param area:视频所属分区
     * @return
     */
    public PageResult<Video> pageListVideos(Integer size, Integer no, String area)
    {
        if(size==null||no==null)
        {
             throw new ConditionException("参数异常！");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("start",(no-1)*size);
        params.put("limit",size);
        params.put("area",area);
        List<Video> list = new ArrayList<>();
//      先看满足条件的数据有多少条
        Integer total = videoDao.pageCountVideos(params);
        if(total>0)
        {
            list = videoDao.pageListVideos(params);
        }
        return new PageResult<>(total,list);
    }

    public void viewViderOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        fastDFSUtil.viewViderOnlineBySlices(request,response,url);
    }

    public void addVideoLike(Long videoId, Long userId) {
        Video video = videoDao.getVideoById(videoId);
        if(video==null)
        {
            throw new ConditionException("非法视频！");
        }
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId,userId);
        if(videoLike!=null)
        {
            throw new ConditionException("已被赞过！");
        }
        videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userId);
        videoLike.setCreateTime(new Date());
    }

    public void deleteVideoLike(Long videoId, Long userId) {
        videoDao.deleteVideoLike(videoId,userId);
    }

    public Map<String, Object> getVideoLikes(Long videoId, Long userId) {
        Long count = videoDao.getVideoLikes(videoId);
        //查询用户是否给当前视频点赞了，并返回点赞情况
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId,userId);
        boolean like = videoLike!=null;
        Map<String,Object> result = new HashMap<>();
        result.put("count",count);
        result.put("like",like);
        return result;
    }

    @Transactional
    public void addVideoCollection(VideoCollection videoCollection, Long userId) {
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if(videoId==null||groupId==null)
        {
            throw new ConditionException("参数异常！");

        }
        Video video = videoDao.getVideoById(videoId);
        if(video==null)
            throw new ConditionException("非法视频！");
//      删除原视频收藏，然后添加
        videoDao.deleteVideoCollection(videoId,userId);
        videoCollection.setUserId(userId);
        videoCollection.setCreateTime(new Date());
        videoDao.addVideoCollection(videoCollection);
    }

    //videoId加了注解，userid有专门的获取通道，一定都存在
    public void deleteVideoCollection(Long userId, Long videoId) {
        videoDao.deleteVideoCollection(videoId,userId);
    }

    public Map<String,Object> getVideoCollection(Long videoId, Long userId) {
        Long count = videoDao.getVideoCollectionCounts(videoId);
        VideoCollection videoCollection = videoDao.getVideoCollection(videoId,userId);
        boolean userCollectVideo = videoCollection!=null;
        Map<String,Object> map = new HashMap<>();
        map.put("count",count);
        map.put("userCollectVideo",userCollectVideo);
        return map;
    }


    public void addVideoCoins(VideoCoin videoCoin, Long userId) {
        Long videoId = videoCoin.getVideoId();
        Integer amount = videoCoin.getAmount();
        if(videoId==null)
        {
            throw new ConditionException("参数异常！");
        }
        Video video = videoDao.getVideoById(videoId);
        if(video==null)
        {
            throw new ConditionException("非法异常！");
        }
//      查询当前登录用户是否拥有足够的硬币(用户金币相关的和Video没关系，建立新的Service)
        Integer userCoinsAmount = usercoinService.getUserCoinsAmount(userId);
        userCoinsAmount = userCoinsAmount==null?0:userCoinsAmount;
        if(amount>userCoinsAmount)
        {
            throw new ConditionException("硬币数量不足~");
        }
//      查询用户对当前视频已经投了多少币
        VideoCoin dbvideoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId,userId);
//      新增视频投币
        if(dbvideoCoin==null)
        {
            videoCoin.setUserId(userId);
            videoCoin.setCreateTime(new Date());
            videoDao.addVideoCoin(videoCoin);
        }else {
            Integer dbAmount = dbvideoCoin.getAmount();
            dbAmount+=videoCoin.getAmount();
//          更新视频投币
            videoCoin.setUserId(userId);
            videoCoin.setVideoId(videoId);
            videoCoin.setUpdateTime(new Date());
            videoCoin.setAmount(dbAmount);
            videoDao.updateVideoCoin(videoCoin);
        }
//      更新当前用户硬币数量(注意此处使用amount，从而传参为Integer)
        usercoinService.updateUserCoin(userId,(userCoinsAmount-amount));
    }

    public void addVideoComment(Long userId, VideoComment videoComment) {
        Long videoId = videoComment.getVideoId();
        if(videoId==null){
            throw new ConditionException("参数异常！");
        }
        Video video = videoDao.getVideoById(videoId);
        if(video==null){
            throw new ConditionException("非法视频！");
        }
        videoComment.setUserId(userId);
        videoComment.setCreateTime(new Date());
        videoDao.addVideoComment(videoComment);
    }

    /**
     * 分页查询视频评论
     * @param size
     * @param no
     * @param videoId
     * @return
     */
    public PageResult<VideoComment> getVideoComments(Integer size, Integer no, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if(video==null){
            throw new ConditionException("非法视频");
        }
        Map<String,Object> param = new HashMap<>();
        param.put("start",(no-1)*size);
        param.put("limit",size);
        param.put("videoId",videoId);
        Integer total = videoDao.pageCountVideoComments(param);
//      评论列表(可能是空的情况，直接不用查了)
        List<VideoComment> list = new ArrayList<>();
        if(total>0)
        {
            list = videoDao.pageVideoComments(param);
//          批量查询二级评论(通过评论的id，批量查询他们的二级评论,此处应该用list，因为set并不能保证有序排列)
            List<Long> parentIdList = list.stream().map(VideoComment::getId).collect(Collectors.toList());
            List<VideoComment> childCommentList = videoDao.batchGetVideoCommentsByRootId(parentIdList);
//          找出所有的userId(root、child、reply)
            Set<Long> parentUserIdList = list.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            Set<Long> childUserIdList = childCommentList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            Set<Long> replyUserIdList = childCommentList.stream().map(VideoComment::getReplyUserId).collect(Collectors.toSet());
//          将userId进行组合并进行查询
            Set<Long> userIdList = new HashSet<>();
            userIdList.addAll(parentUserIdList);
            userIdList.addAll(childUserIdList);
            userIdList.addAll(replyUserIdList);
            List<UserInfo> userInfos = userService.batchGetUserInfoByUserIds(userIdList);
//          将userInfo中的id与UserInfo对应取出，便于后续对应赋值
            Map<Long,UserInfo> userInfoMap = userInfos.stream().collect(Collectors.toMap(UserInfo::getUserId,userInfo -> userInfo));//对象本身赋值最常见的方式，也可以使用Function.identify()
//          将childList和userInfo进行匹配赋值
            list.forEach(videoComment -> {
                Long id = videoComment.getId();
                List<VideoComment> videoCommentList = new ArrayList<>();
                childCommentList.forEach(childVideoCommen->{
                    Long parentId = childVideoCommen.getRootId();
                    if(parentId.equals(id))
                    {
                        childVideoCommen.setUserInfo(userInfoMap.get(childVideoCommen.getUserId()));
                        childVideoCommen.setReplyUserInfo(userInfoMap.get(childVideoCommen.getReplyUserId()));
                        videoCommentList.add(childVideoCommen);
                    }
                    videoComment.setChildList(childCommentList);
                    videoComment.setUserInfo(userInfoMap.get(childVideoCommen.getUserId()));
                });
            });
        }
        return new PageResult<>(total,list);
    }

    public Map<String, Object> getVideoDetails(Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        Long userId = video.getUserId();
//      要尽量减少依赖的注入，因此是使用user里面的方法，这个方法可以同时获取t_user和t_user_info
        User user = userService.getUserInfo(userId);
        UserInfo userInfo = user.getUserInfo();
        Map<String,Object> result = new HashMap<>();
        result.put("video",video);
        result.put("userInfo",userInfo);
        return result;
    }
}
