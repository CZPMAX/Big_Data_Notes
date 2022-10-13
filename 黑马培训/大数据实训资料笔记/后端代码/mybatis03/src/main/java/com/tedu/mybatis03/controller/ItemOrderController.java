package com.tedu.mybatis03.controller;

import com.tedu.mybatis03.mapper.ItemOrderMapper;
import com.tedu.mybatis03.pojo.ItemOrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//控制层
@RestController
@CrossOrigin
public class ItemOrderController {

    //从spring框架中取数据访问层对象
    @Autowired
    ItemOrderMapper itemOrderMapper;

    //添加订单
    @RequestMapping("/item/insert")
    // http://localhost:9002/item/insert?itemName=mate20&userId=8
    public String insert(ItemOrderEntity itemOrderEntity){
        int insertRow = itemOrderMapper.insert(itemOrderEntity);
        if (insertRow>=1){
            return "成功";
        }
        else {
            return "失败";
        }
    }
}
