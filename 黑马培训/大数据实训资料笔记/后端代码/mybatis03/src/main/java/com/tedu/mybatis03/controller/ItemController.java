package com.tedu.mybatis03.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tedu.mybatis03.mapper.ItemMapper;
import com.tedu.mybatis03.pojo.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class ItemController {

    //从spring框架中取数据访问层的对象
    @Autowired
    ItemMapper itemMapper;

    //查询某个分类下的商品
    @RequestMapping("/getItemById")
    // http://localhost:9002/getItemById?categoryId=1
    public List<ItemEntity> getItemById(Integer categoryId){
        //select * from item where category_id=1
        //queryWrapper生成where
        QueryWrapper queryWrapper=new QueryWrapper();
        //eq判断相等
        queryWrapper.eq("category_id",categoryId);

        List list = itemMapper.selectList(queryWrapper);

        return list;

    }
}
