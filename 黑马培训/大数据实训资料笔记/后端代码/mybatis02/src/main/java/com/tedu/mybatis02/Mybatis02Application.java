package com.tedu.mybatis02;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tedu.mybatis02.mapper")
//1,mybatis框架为包下的接口创建实现类
//2,为实现类创建对象
//3,把对象放在spring框架容器中
public class Mybatis02Application {

    public static void main(String[] args) {
        SpringApplication.run(Mybatis02Application.class, args);
    }

}
