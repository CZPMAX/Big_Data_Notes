package com.tedu.mybatis03.controller;

import com.tedu.mybatis03.mapper.DateTongjiMapper;
import com.tedu.mybatis03.pojo.DateTongjiEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//控制层
@RestController
@CrossOrigin//重启服务器
public class DateController {

    //从spring框架容器中取数据访问层的对象
    @Autowired
    DateTongjiMapper dateTongjiMapper;

    @RequestMapping("/getDate")
    // http://localhost:9002/getDate
    // 重启
    //1,接收浏览器的请求
    public List<DateTongjiEntity> getDate(){
        //2,调用数据访问层
        List<DateTongjiEntity> list = dateTongjiMapper.selectList(null);
        //3,返回结果给浏览器
        return list;
    }
}
