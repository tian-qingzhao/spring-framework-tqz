package com.tian.demo.service.impl;

import com.tian.demo.dao.UserDao;
import com.tian.demo.dao.impl.UserDaoImpl;
import com.tian.demo.entity.User;
import com.tian.demo.service.DemoService;
import com.tian.demo.service.TeacherService;
import com.tian.springframework.annotation.Autowired;
import com.tian.springframework.annotation.Lazy;
import com.tian.springframework.annotation.Service;

/**
 * author: tian
 * date: 2020-1-1 17:16
 * desc:
 **/
@Service
@Lazy //是否懒加载，默认值为true
public class DemoServiceImpl implements DemoService {

//    @Autowired
    private TeacherService teacherService;
    
    public String get(String userName) {
       int i = 1/0;
       return "my name is " + userName;
    }

    public User selectByName(String name) {
//        int i = 1/0;
        UserDao userDao = new UserDaoImpl();
        User user = userDao.selectByName(name);
        return user;
    }

    public void test() {
        teacherService.test();
        System.out.println("chengogn");
    }
}


