package com.project.bilibili.dao;

import com.project.bilibili.domain.Danmu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface DanmuDao {

    List<Danmu> getDanmus(Map<String, Object> param);

    Integer addDanmu(Danmu danmu);
}
