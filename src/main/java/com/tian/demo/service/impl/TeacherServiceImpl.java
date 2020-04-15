package com.tian.demo.service.impl;

import com.tian.demo.service.DemoService;
import com.tian.demo.service.TeacherService;
import com.tian.springframework.annotation.Autowired;
import com.tian.springframework.annotation.Service;

/**
 * @Author: tian
 * @Date: 2020/4/9 15:17
 * @Desc:
 */
//@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private DemoService demoService;
    
    public void test() {
        demoService.test();
    }
}
