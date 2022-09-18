package com.project.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoDao {
    String queryName(int id);

}
