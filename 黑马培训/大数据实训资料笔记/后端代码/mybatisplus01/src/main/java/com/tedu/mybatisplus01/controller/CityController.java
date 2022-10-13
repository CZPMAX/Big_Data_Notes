package com.tedu.mybatisplus01.controller;

import com.tedu.mybatisplus01.mapper.DateTongjiMapper;
import com.tedu.mybatisplus01.pojo.DateTongjiEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//控制层
@RestController
public class CityController {

    //从spring框架中得到数据访问层对象,赋值给dateTongjiMapper
    @Autowired
    DateTongjiMapper dateTongjiMapper;
    // http://localhost:9002/d
    //重启服务器
    @RequestMapping("/d")
    public List<DateTongjiEntity> getDateTongji(){
        //null表示没有where
        List<DateTongjiEntity> list = dateTongjiMapper.selectList(null);
        return list;
    }

    // http://localhost:9002/test
    //重启服务器
    @RequestMapping("/test")
    public String test(){
        return "数据库统计结果";
    }
}
