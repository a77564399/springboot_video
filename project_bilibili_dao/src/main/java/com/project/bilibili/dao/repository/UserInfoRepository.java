package com.project.bilibili.dao.repository;

import com.project.bilibili.domain.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserInfoRepository extends ElasticsearchRepository<UserInfo,Long> {
}
