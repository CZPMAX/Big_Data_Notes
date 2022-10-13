package com.tedu.mybatis03.controller;

import com.tedu.mybatis03.mapper.CategoryMapper;
import com.tedu.mybatis03.pojo.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//控制层
@RestController
@CrossOrigin
public class CategoryController {
    //从spring框架中取数据访问层的对象
    @Autowired
    CategoryMapper categoryMapper;

    @RequestMapping("/getCategory")
    // http://localhost:9002/getCategory
    public List<CategoryEntity> getCategory(){
        List<CategoryEntity> list = categoryMapper.selectList(null);

        return list;
    }
}
