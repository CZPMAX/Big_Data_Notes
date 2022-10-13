package com.tedu.mybatis03.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

//实体类
@TableName("category")
public class CategoryEntity {
    Integer id;//id列
    String name;//name列

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
