package com.tedu.mybatis03.controller;

import com.tedu.mybatis03.mapper.UsersMapper;
import com.tedu.mybatis03.pojo.UsersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UsersController {
    //从spring框架容器中取对象
    @Autowired
    UsersMapper usersMapper;

    @RequestMapping("/register")
    //如果方法接收的是一个对象，?属性名=value&属性名=value
    // http://localhost:9002/register?username=张六&password=1&gender=男&age=18&city=新乡
    public String register(UsersEntity usersEntity){
        //调用数据访问层
        int insertRow = usersMapper.insert(usersEntity);
        if(insertRow>=1){
            return "注册成功";
        }else {
            return "注册失败";
        }
    }
}
