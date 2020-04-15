package com.tian;

import com.tian.springframework.annotation.SpringBootApplication;
import com.tian.springframework.config.SpringApplication;
import com.tian.springframework.annotation.ServletLoadOnStartUp;

/**
 * @Author: tian
 * @Date: 2020/4/14 1:30
 * @Desc:
 */
@SpringBootApplication
@ServletLoadOnStartUp(loadOnStartUp = 1)
public class Application {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(Application.class,args);
    }
}
