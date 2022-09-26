package com.project.bilibili.service;

import com.project.bilibili.dao.FollowingGroupDao;
import com.project.bilibili.dao.UserFollowingDao;
import com.project.bilibili.domain.*;
import com.project.bilibili.domain.constant.UserConstant;
import com.project.bilibili.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFollowingService {
    @Autowired
    private UserFollowingDao userFollowingDao;
    @Autowired
    private FollowingGroupService followingGroupService;
    @Autowired
    private UserService userService;

//    添加用户关注：将用户id和关注的id封装到一个类中，获取后调用Dao直接添加
//   添加事务支持，防止删除成功了但是新增报错了
    @Transactional
    public void addUserFollowings(UserFollowing userFollowing)
    {
//        获取到分组id
        Long groupId = userFollowing.getGroupId();
//        判断分组：分组是空加到默认分组，不是空但获取不到返回异常
        if(groupId == null)
        {
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        }else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if(followingGroup == null)
            {
                throw new ConditionException("关注的分组不存在！");
            }
        }
//      获取关注人的id
        Long followingId = userFollowing.getId();
//        获取到关注人且判断关注人是否存在
        User user = userService.getUserById(followingId);
        if(user == null)
        {
            throw new ConditionException("关注的用户不存在");
        }
//       删除语句在没有数据的情况下并不会报错
//      已关注的情况下需要先删除这个关联，然后重新添加以达到更新的目的，这样就不用重新写更新的方法了
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(),followingId);
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }

//  获取关注用户的列表
//    获取到列表后：1、根据关注用户的id查询关注用户的基本信息，2、将关注用户按关注分组进行分类

    /**
     * 过程：
     *  获取关注用户的列表（group里面包含List<userInfo>）
     *  先根据userId查询出用户关注的所有人员，将他们的id取出来，通过ids查询他们的userInfos(所有关注人员的info)
     *  通过userInfo的id与userFollowing中的userId匹配，将userinfo装配到userFollowing中
     *  分组：获取到用户的所有分组groupList，然后根据userFollowing中的groupId和groupList的id进行匹配，将userFollowing中的所有userInfo装到groupList的一个group中
     * @param userId
     * @return
     */

    public List<FollowingGroup> getUserFollowings(Long userId)
    {
//       查询到user关注的所有人(用户ID，关注人的id，分组id)
        List<UserFollowing> userFollowings = userFollowingDao.getUserFollowings(userId);
//       获取所有关注人的UserId
        Set<Long> followingUserIds = userFollowings.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
//      获取所有关注人的info
        List<UserInfo> userInfos = new ArrayList<>();
        if(followingUserIds.size()>0) {
//          查询到用户info的列表
            userInfos = userService.getUserInfoByUserIds(followingUserIds);
        }
//      将user关注列表和userInfos里面进行匹配，然后把userInfo信息放到user关注列表的userInfo信息里面去
//        现在获取到的userFollowings里面的UserInfo没有，需要按userId给他装配进去
        for(UserFollowing userFollowing:userFollowings)
        {
            for(UserInfo userInfo:userInfos)
            {
                if(userFollowing.getUserId().equals(userInfo.getId()))
                {
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
//      构建一个全部关注列表，里面是这个用户关注的所有人，用于展示所有关注人
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setUserInfos(userInfos);

//       返回值，所有分组信息（分组信息内部包含用户信息列表userInfos）
        List<FollowingGroup> res = new ArrayList<>();
        res.add(allGroup);

//       通过用户Id查询出他所有的分组情况 (FollowingGroup表里面全是分组名称这些，没有详细内容)
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);

//       将对应的分组加入到
        for(FollowingGroup followingGroup:groupList)
        {
            List<UserInfo> userInfoList = new ArrayList<>();
            for(UserFollowing userFollowing:userFollowings)
            {
                if(userFollowing.getGroupId().equals(followingGroup.getId()))
                {
                    userInfoList.add(userFollowing.getUserInfo());
                }
            }
            followingGroup.setUserInfos(userInfoList);
            res.add(followingGroup);
        }
        return res;
    }

    /**
     * 获取用户粉丝列表
     *过程：1、获取当前用户的所有粉丝  2、根据粉丝的userId查询基本信息  3、查询当前用户是否已关注该粉丝(互粉)
     * 说明：主要是通过userFollowing查询出所有粉丝，然后将粉丝的UserInfo装载进去(直接存数据库会造成大量冗余)，然后遍历用户关注列表查看是否互关(在UserInfo中设置followed标志，true表示当前用户被查询者关注)
     * @param userId
     * @return
     */
    public List<UserFollowing> getUserFans(Long userId)
    {
//      获取关注者(用于查看是否互粉)
        List<UserFollowing> userFollowings = userFollowingDao.getUserFollowings(userId);
//      获取所有粉丝(以UserFollow获取，包含用户和粉丝id)
        List<UserFollowing> userFans = userFollowingDao.getUserFans(userId);
//      获取所有粉丝的id
        Set<Long> fanUserIds = userFans.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());

        //获取所有粉丝的信息
        List<UserInfo> userInfoList=null;
        if(fanUserIds.size()>0)
        {
            userInfoList = userService.getUserInfoByUserIds(fanUserIds);
        }
//      粉丝信息装载
        for(UserFollowing fan:userFans)
        {
            for(UserInfo userInfo:userInfoList)
            {
                if(userInfo.getId().equals(fan.getUserId()))
                {
//                  进行匹配的时候，默认没有互关
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
//
            }
            //遍历当前用户所有关注者，如果id和当前粉丝id相同则为互关
            for(UserFollowing userFollowing:userFollowings)
            {
                if(userFollowing.getFollowingId().equals(fan.getUserId()))
                {
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }
        return userFans;
    }

    public Long addUserFollowingGroup(FollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        followingGroupService.addUserFollowingGroup(followingGroup);
        return followingGroup.getId();
    }

    public List<FollowingGroup> getUserFollowingGroup(Long userId) {
        return followingGroupService.getUserFollowingGroup(userId);
    }

    /**
     * 检查用户关注状态，给一个用户信息表，和当前用户Id进行校验赋值
     * @param userInfoList
     * @param userId
     * @return
     */
    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList,Long userId) {
//      注意：能调用一次Dao层方法就直接一次查完，最少程度减少IO时间
        List<UserFollowing> userFollowings = userFollowingDao.getUserFollowings(userId);
        for(UserInfo userInfo:userInfoList)
        {
            userInfo.setFollowed(false);
            for(UserFollowing userFollowing:userFollowings)
            {
                if(userFollowing.getUserId().equals(userInfo.getId()))
                {
                    userInfo.setFollowed(true);
                }
            }
        }
        return userInfoList;
    }
}
