package com.project.bilibili.dao;

import com.project.bilibili.domain.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FollowingGroupDao {

    FollowingGroup getByType(String type);

    FollowingGroup getById(Long id);

    List<FollowingGroup> getByUserId(Long userId);


    Integer addUserFollowingGroup(FollowingGroup followingGroup);

    List<FollowingGroup> getUserFollowingGroup(Long userId);
}
