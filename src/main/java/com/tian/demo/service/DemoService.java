package com.tian.demo.service;

import com.tian.demo.entity.User;

/**
 * author: tian
 * date: 2020-1-1 17:16
 * desc:
 **/
public interface DemoService {

    String get(String userName);

    User selectByName(String name);

    void test();
}
