package com.tedu.mybatis03;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tedu.mybatis03.mapper")
//1,告诉mybatis,数据访问层接口在mapper,mybatis框架为包下的接口创建实现类
//2,为实现类创建对象
//3,对象会放在spring框架容器中
public class Mybatis03Application {

    public static void main(String[] args) {
        SpringApplication.run(Mybatis03Application.class, args);
    }

}
