package com.codyzeng.esample.user;

import com.alibaba.fastjson2.JSON;
import com.codyzeng.esample.user.entity.User;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author: codyzeng@163.com
 * @date: 2022/12/7
 */
public class UserDataInitializer {

    public static List<User> loadUserData() {
        byte[] bytes = new byte[0];
        try {
            bytes = Objects.requireNonNull(UserDataInitializer.class.getResourceAsStream("/hero.json")).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSON.register(GeoPoint.class,GeoPointReader.INSTANCE);
        return JSON.parseArray(new String(bytes), User.class);
    }
}
