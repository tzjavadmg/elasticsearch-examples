package com.codyzeng.esample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author: codyzeng@163.com
 * @date: 2022/12/6
 */
@SpringBootApplication
@EnableWebMvc
@EnableElasticsearchRepositories(basePackages = "com.codyzeng.esample.dao")
public class EsampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsampleApplication.class, args);
    }
}
