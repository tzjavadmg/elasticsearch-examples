package com.codyzeng.esample.user.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.LocalDate;

/**
 * @author: codyzeng@163.com
 * @date: 2022/12/6
 */
@Document(indexName = "user",createIndex = true)
@Setting(shards = 3, replicas = 1,refreshInterval="1ms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Keyword)
    private String sex;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Date,format={DateFormat.basic_date, DateFormat.year_month_day})
    private LocalDate birthday;

    @Field(type = FieldType.Keyword)
    private String province;

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Keyword)
    private String district;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String address;

    @GeoPointField
    private GeoPoint location;

    @Field(index = false, type = FieldType.Keyword)
    private String photo;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String about;
}
