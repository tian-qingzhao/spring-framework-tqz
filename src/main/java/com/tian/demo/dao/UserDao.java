package com.tian.demo.dao;

import com.tian.demo.entity.User;

/**
 * author: tian
 * date: 2020-1-2 10:42
 * desc:
 **/
public interface UserDao {

    User selectByName(String name);
}
