package com.tedu.springboot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//控制层，接收浏览器发过来的请求，调用数据库，返回结果
@RestController
public class CityTongjiController {
    //http://localhost:8080/test
    @RequestMapping("/test")
    public String test(){
        return "大数据统计结果";
    }
}
