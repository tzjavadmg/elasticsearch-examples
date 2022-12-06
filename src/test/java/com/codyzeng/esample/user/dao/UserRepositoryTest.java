package com.codyzeng.esample.user.dao;

import com.codyzeng.esample.user.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import static org.junit.jupiter.api.Assertions.*;

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
                .id(9L)
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
    @DisplayName("分页查询")
    @Order(2)
    void pageQuery() {
        Page<User> users = userRepository.findAll(PageRequest.of(3, 2));
        users.getContent().forEach(e->log.info(e.toString()));

        users = userRepository.findAll(PageRequest.of(3, 2));
        users.getContent().forEach(e->log.info(e.toString()));
    }
}