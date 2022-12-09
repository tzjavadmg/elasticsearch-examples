package com.codyzeng.esample.user.dao;

import com.codyzeng.esample.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.annotations.SourceFilters;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoPage;

import java.awt.print.Book;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author: codyzeng@163.com
 * @date: 2022/12/6
 */
public interface UserRepository extends ElasticsearchRepository<User, Long> {

    /**
     * 城市分区总计
     *
     * @param district
     * @return
     */
    long countByDistrict(String district);

    /**
     * 城市分区搜索
     *
     * @param district
     * @return
     */
    List<User> findByDistrictOrderByIdDesc(String district);

    /**
     * 地址搜索
     *
     * @param address
     * @return
     */
    List<User> findByAddress(String address);

    List<User> findByDistrictOrAddress(String district, String address);

    List<User> findByDistrictAndAddress(String district, String address);

    List<User> findByAgeBetween(int min, int max);

    Page<User> findByAddress(String address, Pageable pageable);

    @Query(""" 
            {"match": {"about": {"query": "?0"}}}
            """)
    Stream<User> findByAbout(String name);

    @Query("""
            {"bool":{"must":[{"match":{"city":{"query": "?0"}}},{"match":{"sex":{"query": "?1"}}},{"range":{"age":{"gte":?2,"lte":?3}}}]}}
            """)
    @SourceFilters(includes = {"address","age","name"})
    Page<User> search(String city, String sex, Integer minAge, Integer maxAge, Pageable pageable);
}
