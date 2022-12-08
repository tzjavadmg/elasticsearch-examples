package com.codyzeng.esample.user;

import com.codyzeng.esample.user.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.GeoDistanceOrder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.io.IOException;
import java.util.*;

/**
 * @author: codyzeng@163.com
 * @date: 2022/12/6
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserIndexTest {

    @Resource
    ElasticsearchTemplate elasticsearchTemplate;
    @Resource
    ElasticsearchOperations elasticsearchOperations;

    @Test
    @DisplayName("删除索引")
    @Order(1)
    void deleteUserIndex() {
        IndexOperations indexOperations = elasticsearchTemplate.indexOps(User.class);
        if (indexOperations.exists()) {
            indexOperations.delete();
        }
    }

    @Test
    @DisplayName("创建索引")
    @Order(2)
    void createUserIndex() {
        IndexOperations indexOperations = elasticsearchTemplate.indexOps(User.class);

        Map<String, Object> settings = new HashMap<>();
        Map<String, Object> index = new HashMap<>();
        settings.put("index", index);
        index.put("number_of_shards", 3);
        index.put("number_of_replicas", 1);

        Document mapping = indexOperations.createMapping(User.class);
        indexOperations.create(settings, mapping);
    }

    @Test
    @DisplayName("批量插入或更新文档")
    @Order(3)
    void bulkCreateUser() throws IOException {
        List<User> users = UserDataInitializer.loadUserData();
        elasticsearchTemplate.save(users);
    }


    @Test
    @DisplayName("插入或更新单个文档")
    @Order(4)
    void createUser() {
        elasticsearchTemplate.save(User.builder()
                .id(99L)
                .username("老六")
                .age(33)
                .province("上海")
                .city("上海")
                .district("浦东新区")
                .address("上海市浦东新区花园石桥路28弄1-8号-汤臣一品")
                .location(new GeoPoint(31.238794, 121.508506))
                .about("槟榔妹真好玩")
                .build());
    }

    @Test
    @DisplayName("获取单个文档")
    @Order(5)
    void getUser() {
        User user = elasticsearchTemplate.get("9", User.class);
        assert user != null;
        Assertions.assertEquals("老六", user.getUsername());
    }

    @Test
    @DisplayName("更新索引结构")
    @Order(6)
    void updateUserIndex() {
        IndexOperations indexOperations = elasticsearchTemplate.indexOps(User.class);
        indexOperations.putMapping();
    }

    @Test
    @DisplayName("按距离范围搜索")
    @Order(4)
    void searchWithin() {
        //九号线星中路地铁站 121.375569,31.163862
        GeoPoint location = new GeoPoint(31.163862, 121.375569);
        Distance distance = new Distance(2.0, Metrics.KILOMETERS);
        Sort sort = Sort.by(new GeoDistanceOrder("location", location)).ascending();
        Query query = new CriteriaQuery(
                new Criteria("location").within(location, "400m")
        );
        query.addSort(sort);
        List<SearchHit<User>> searchHits = elasticsearchOperations.search(query, User.class).getSearchHits();
        searchHits.forEach(searchHit -> log.info(searchHit.getContent().toString()));
    }
}
