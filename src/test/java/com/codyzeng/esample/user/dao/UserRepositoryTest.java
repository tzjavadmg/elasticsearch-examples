package com.codyzeng.esample.user.dao;

import com.alibaba.fastjson2.JSON;
import com.codyzeng.esample.user.UserDataInitializer;
import com.codyzeng.esample.user.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.GeoDistanceOrder;

import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class UserRepositoryTest {

    @Resource
    private UserRepository userRepository;

    @Test
    @DisplayName("插入或更新单个文档")
    @Order(1)
    void createUser() {
        userRepository.save(User.builder()
                .id(99L)
                .username("老六")
                .age(33)
                .province("上海")
                .city("上海")
                .district("浦东新区")
                .address("上海市浦东新区花园石桥路28弄1-8号-汤臣一品")
                .location(new GeoPoint(31.238794, 121.508506))
                .about("槟榔妹真好玩啊")
                .build());
    }

    @Test
    @DisplayName("批量插入或更新单个文档")
    @Order(2)
    void bulkCreateUser() {
        List<User> users = UserDataInitializer.loadUserData();
        userRepository.saveAll(users);
    }

    @Test
    @DisplayName("分页查询")
    @Order(3)
    void pageQuery() {
        //九号线星中路地铁站 121.375569,31.163862
        Sort sort = Sort.by(new GeoDistanceOrder("location", new GeoPoint(31.163862, 121.375569))).ascending();
        Page<User> userPage = userRepository.findByAddress("闵行", PageRequest.of(0, 5, sort));
        userPage.getContent().forEach(e -> log.info(e.toString()));
    }

    @Test
    @DisplayName("相似度查询")
    @Order(4)
    void searchSimilar() {
        User user = User.builder().id(7L).build();
        String[] fields = new String[]{"district"};
        Page<User> users = userRepository.searchSimilar(user, fields, PageRequest.of(0, 10));
        users.getContent().forEach(e -> log.info(e.toString()));
    }

    @Test
    @DisplayName("城市区域统计")
    @Order(4)
    void countByDistrict() {
        long count = userRepository.countByDistrict("浦东新区");
        Assertions.assertEquals(count, 2);

        count = userRepository.countByDistrict("浦东");
        Assertions.assertEquals(count, 0);
    }

    @Test
    @DisplayName("城市区域查询")
    @Order(4)
    void findByDistrict() {
        List<User> users = userRepository.findByDistrictOrderByIdDesc("闵行区");
        Assertions.assertEquals(users.size(), 4);
        Assertions.assertEquals(users.get(0).getId(), 7);
    }

    @Test
    @DisplayName("地址查询")
    @Order(4)
    void findByAddress() {
        List<User> users = userRepository.findByAddress("浦东");
        Assertions.assertEquals(users.size(), 3);

        users = userRepository.findByAddress("古北壹号");
        Assertions.assertEquals(users.size(), 1);

        users = userRepository.findByAddress("汤臣一品");
        Assertions.assertEquals(users.size(), 3);
    }

    @Test
    @DisplayName("按城市区域或地址查询")
    @Order(4)
    void findByDistrictOrAddress() {
        List<User> users = userRepository.findByDistrictAndAddress("浦东", "浦东");
        Assertions.assertEquals(users.size(), 0);

        users = userRepository.findByDistrictOrAddress("浦东", "浦东");
        Assertions.assertEquals(users.size(), 2);

    }

    @Test
    @DisplayName("按城市区域或地址查询")
    @Order(4)
    void findByAgeBetween() {
        List<User> users = userRepository.findByAgeBetween(50, 90);
        Assertions.assertEquals(users.size(), 1);
        // 18,22,25,31
        users = userRepository.findByAgeBetween(18, 31);
        Assertions.assertEquals(users.size(), 4);

    }
}