package com.project.bilibili.dao.repository;

import com.project.bilibili.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//继承elastic的抽象类，并指明实体类和主键id
public interface VideoRepository extends ElasticsearchRepository<Video,Long> {
//   springData会自动拆解方法名，然后根据title进行查询，find by title like
    Video findByTitleLike(String keyword);
}
