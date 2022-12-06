package com.codyzeng.esample.user.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

/**
 * @author: codyzeng@163.com
 * @date: 2022/12/6
 */
@Document(indexName = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type=FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Keyword)
    private String province;

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Keyword)
    private String district;

    @Field(type = FieldType.Text,analyzer = "ik_smart")
    private String address;

    @GeoPointField
    private GeoPoint location;

    @Field(index = false,type = FieldType.Keyword)
    private String photo;

    @Field(type = FieldType.Text,analyzer = "ik_smart")
    private String about;
}
