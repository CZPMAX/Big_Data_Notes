package com.tedu.mybatis02.controller;

import com.tedu.mybatis02.mapper.CityTongjiMapper;
import com.tedu.mybatis02.pojo.CityTongjiEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//控制层
@RestController
@CrossOrigin//允许任何网站访问我，服务器必须重启
public class CityController {

    //从spring框架容器中取数据访问层的对象
    @Autowired
    CityTongjiMapper cityTongjiMapper;


    //1,接收浏览器发过来的请求
    @RequestMapping("/getCity")
    // http://localhost:9002/getCity
    // 重启
    public List<CityTongjiEntity> getCity(){
        //2,调用数据访问层
        List<CityTongjiEntity> list = cityTongjiMapper.selectList(null);
        //3,返回结果给浏览器
        return list;
    }


}
