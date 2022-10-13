package com.project.bilibili.service.config;

import org.apache.rocketmq.client.ClientConfig;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {
    @Value("${elasticsearch.url}")
    private String esUrl;


    /**
     * 高级restful客户端,连接es操作
     * @return
     */
    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo(esUrl).build();
        return RestClients.create(clientConfiguration).rest();
    }
}
