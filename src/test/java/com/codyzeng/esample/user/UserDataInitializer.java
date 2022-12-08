package com.codyzeng.esample.user;

import com.alibaba.fastjson2.JSON;
import com.codyzeng.esample.user.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author: codyzeng@163.com
 * @date: 2022/12/7
 */
public class UserDataInitializer {

    public static List<User> loadUserData() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        byte[] bytes = new byte[0];
        try {
            bytes = Objects.requireNonNull(loader.getResourceAsStream("/hero.json")).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return JSON.parseArray(bytes, User.class);
    }
}
