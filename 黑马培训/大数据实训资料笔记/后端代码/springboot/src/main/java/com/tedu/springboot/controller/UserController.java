package com.tedu.springboot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//控制层
@RestController
public class UserController {
    // http://localhost:9090/userLogin?username=a&password=1
    // http://localhost:9090/userLogin?username=a&password=2
    //重启
    @RequestMapping("/userLogin")
    public String login(String username,String password){
        if ("a".equals(username) && "1".equals(password)){
           return "成功";
        }else {
            return "失败";
        }

    }
}
