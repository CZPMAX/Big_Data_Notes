package com.tedu.mybatisplus01;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//入口类
@MapperScan("com.tedu.mybatisplus01.mapper")
//告诉mybatis框架接口在那个包中，mybatis框架为包中的接口创建实现类，
//再为实现类创建对象，对象放在spring框架中
public class Mybatisplus01Application {

    public static void main(String[] args) {

        SpringApplication.run(Mybatisplus01Application.class, args);
    }

}
