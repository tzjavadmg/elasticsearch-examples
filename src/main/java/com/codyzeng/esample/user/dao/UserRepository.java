package com.codyzeng.esample.user.dao;

import com.codyzeng.esample.user.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author: codyzeng@163.com
 * @date: 2022/12/6
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long>, CrudRepository<User, Long> {

}
