package com.project.bilibili.service;

import com.project.bilibili.dao.repository.VideoRepository;
import com.project.bilibili.domain.Video;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.fetch.subphase.highlight.Highlighter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ElasticSearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void addVideo(Video video){
        videoRepository.save(video);
    }

    public Video getVideos(String keyword)
    {
        return videoRepository.findByTitleLike(keyword);
    }

    public void deleteAllVideos()
    {
        videoRepository.deleteAll();
    }

    public List<Map<String, Object>> getContents(String keyword, Integer pageNo, Integer pageSize) throws IOException {
//      确定要搜索的类的索引
        String[] indices = {"videos","user-infos"};
//      建立搜索请求
        SearchRequest searchRequest = new SearchRequest();
//      建立搜索源(进行分页)
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(pageNo-1);
        searchSourceBuilder.size(pageSize);
//      进行搜索匹配,确定通过哪些字段进行匹配(视频标题，用户名称，描述)
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyword,"title","nick","description");
        searchSourceBuilder.query(matchQueryBuilder);
        searchRequest.source(searchSourceBuilder);
//      设置超时时间
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//      搜索完成



//      高亮显示
        String[] array = {"title","nick","description"};
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for(String key:array)
        {
            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
        }
//      注意：多个字段高亮显示的话，需要在此处设置为false
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
//      搜索源builder设置高亮builder
        searchSourceBuilder.highlighter(highlightBuilder);
//      执行搜索（request设置为默认） 返回searchResponse
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//      建立返回结果（把查询到的适合的地方）
        List<Map<String,Object>> arrayList = new ArrayList<>();
//      循环遍历所有被击中的位置
        for(SearchHit hit:searchResponse.getHits())
        {
//          处理高亮字段，string是字段名称(title) HighlightField高亮的位置
            Map<String, HighlightField> highlightBuilderFileds = hit.getHighlightFields();
            //处理完成之后的结果map
            Map<String,Object> sourceMap = hit.getSourceAsMap();
            for(String key:array)
            {
//              将对应位置的字段取出
                HighlightField field = highlightBuilderFileds.get(key);
                if(field!=null)
                {
//                  因为每一个搜索到的视频可能有多个位置，因此获取到的是碎片组
                    Text[] fragments = field.fragments();
//                  将所有碎片组 数组转为String
                    String str = Arrays.toString(fragments);
//                  取消掉前面的[ 后面的]
                    str = str.substring(1,str.length()-1);
                    sourceMap.put(key,str);
                }
            }
            arrayList.add(sourceMap);
        }
        return arrayList;
    }
}
