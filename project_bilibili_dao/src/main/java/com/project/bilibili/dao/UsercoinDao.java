package com.project.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface UsercoinDao {

    Integer getUserCoinsAmount(Long userId);

    Integer updateUserCoin(@Param("userId") Long userId,@Param("amount") Integer amount,@Param("updateTime") Date updateTime);
}
