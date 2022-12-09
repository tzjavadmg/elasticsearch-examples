package com.codyzeng.esample.user;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.codyzeng.esample.user.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.index.Settings;
import org.springframework.data.elasticsearch.core.query.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: codyzeng@163.com
 * @date: 2022/12/8
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserOperationsTest {

    @Resource
    ElasticsearchOperations elasticsearchOperations;

    @Test
    @DisplayName("删除索引")
    @Order(1)
    void deleteIndex() {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(User.class);
        if (indexOperations.exists()) {
            indexOperations.delete();
        }
    }

    @Test
    @DisplayName("创建索引")
    @Order(2)
    void createIndex() {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(User.class);
        //设置索引基本信息
        Map<String, Object> settings = new HashMap<>();
        Map<String, Object> index = new HashMap<>();
        settings.put("index", index);
        index.put("number_of_shards", 3);
        index.put("number_of_replicas", 1);

        Document mapping = indexOperations.createMapping(User.class);
        indexOperations.create(settings, mapping);
    }

    @Test
    @DisplayName("查看索引信息")
    @Order(3)
    void indexInfo() {
        //查看索引完整信息
        IndexOperations indexOperations = elasticsearchOperations.indexOps(User.class);
        List<IndexInformation> informations = indexOperations.getInformation();
        informations.forEach(indexInformation -> System.out.println(JSON.toJSONString(indexInformation, JSONWriter.Feature.PrettyFormat)));

        //查看索引映射信息
        Map<String, Object> mapping = indexOperations.getMapping();
        System.out.println("------------mapping----------------");
        System.out.println(JSON.toJSONString(mapping, JSONWriter.Feature.PrettyFormat));

        //查看索引设置信息
        Settings settings = indexOperations.getSettings(true);
        System.out.println("------------settings----------------");
        System.out.println(JSON.toJSONString(settings, JSONWriter.Feature.PrettyFormat));

    }

    @Test
    @DisplayName("插入或更新文档")
    @Order(3)
    void createUser() {
        User user = User.builder()
                .id(99L)
                .name("老六")
                .age(33)
                .province("上海")
                .city("上海")
                .district("浦东新区")
                .address("上海市浦东新区花园石桥路28弄1-8号-汤臣一品")
                .location(new GeoPoint(31.238794, 121.508506))
                .about("预测世界杯四强：阿根廷，巴西，法国，葡萄牙")
                .build();
        //支持批量插入
        elasticsearchOperations.save(user);
    }

    @Test
    @DisplayName("获取单个文档")
    @Order(4)
    void getUser() {
        User user = elasticsearchOperations.get("99", User.class);
        assert user != null;
        Assertions.assertEquals("老六", user.getName());
    }

    @Test
    @DisplayName("删除单个文档")
    @Order(5)
    void deleteUser() {
        elasticsearchOperations.delete("99", User.class);
    }

    @Test
    @DisplayName("CriteriaQuery：寻找附近两公里18-30岁的妹纸")
    @Order(4)
    void criteriaQuery() {
        //位置坐标：九号线星中路地铁站 121.375569,31.163862
        GeoPoint location = new GeoPoint(31.163862, 121.375569);
        //按地址由近到远排序,相同小区年龄从大到小排
        Sort sort = Sort
                .by(new GeoDistanceOrder("location", location))
                .ascending()
                .and(Sort.by("age").descending());

        Query query = new CriteriaQuery(
                //查询星中路地铁站2公里内的小区
                new Criteria("location").within(location, "2km")
                        .and(new Criteria("sex").is("女"))
                        .and(new Criteria("age").lessThan(30).greaterThanEqual(18))
        );
        query.addSort(sort);
        SearchHits<User> searchHits = elasticsearchOperations.search(query, User.class);
        List<SearchHit<User>> searchHitList = searchHits.getSearchHits();

        searchHitList.forEach(searchHit -> {
                    User hero = searchHit.getContent();
                    log.info("\n{}", searchHit.getScore(), JSON.toJSONString(hero, JSONWriter.Feature.PrettyFormat));
                }
        );
    }

    @Test
    @DisplayName("StringQuery：寻找上海40-60岁的老男人")
    @Order(4)
    void stringQuery() {
        String dsl = """
                   {"bool":{"must":[{"match":{"city":"上海"}},{"match":{"sex":"男"}},{"range":{"age":{"gte":40,"lte":60}}}]}}
                """;
        Query query = new StringQuery(dsl);

        List<SearchHit<User>> searchHitList = elasticsearchOperations.search(query, User.class).getSearchHits();
        searchHitList.forEach(searchHit -> System.out.println(JSON.toJSONString(searchHit.getContent(), JSONWriter.Feature.PrettyFormat))
        );
    }

    @Test
    @DisplayName("NativeQuery：统计上海各区妹纸人数,并显示前3人信息")
    @Order(4)
    void nativeQuery() {
        //按距离和年龄排序，选择最近的和最年轻的
        Sort sort = Sort.by(new GeoDistanceOrder("location", new GeoPoint(31.163862, 121.375569))).ascending()
                .and(Sort.by("age").ascending());

        Query query = NativeQuery.builder()
                //定义一个名为byDistrict，按district字段分组统计的聚合。
                .withAggregation("byDistrict", Aggregation.of(a -> a.terms(ta -> ta.field("district").size(100))))
                //只统计妹纸
                .withQuery(q -> q.match(m -> m.field("sex").query("女")))
                //每页3条，显示第一页数据
                .withPageable(PageRequest.of(0, 3, sort))
                .build();
        SearchHits<User> searchHits = elasticsearchOperations.search(query, User.class);
        //获取聚合数据
        ElasticsearchAggregations aggregationsContainer = (ElasticsearchAggregations) searchHits.getAggregations();
        Map<String, ElasticsearchAggregation> aggregations = Objects.requireNonNull(aggregationsContainer).aggregationsAsMap();
        //获取指名称的聚合
        ElasticsearchAggregation aggregation = aggregations.get("byDistrict");
        Buckets<StringTermsBucket> buckets = aggregation.aggregation().getAggregate().sterms().buckets();
        //打印聚合信息
        buckets.array().forEach(bucket -> log.info("\n区名:{}\n人数：{}", bucket.key().stringValue(), bucket.docCount()));
        //打印聚合命中记录信息
        List<SearchHit<User>> searchHitList = searchHits.getSearchHits();
        searchHitList.forEach(searchHit -> System.out.println(JSON.toJSONString(searchHit.getContent(), JSONWriter.Feature.PrettyFormat)));
    }
}
