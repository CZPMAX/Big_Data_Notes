package com.tedu.mybatis03.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

//实体类
@TableName("item_order")
public class ItemOrderEntity {
   @TableId(type = IdType.AUTO)//主键自动加1
    Integer orderId; //order_id列
    String itemName;//item_name列
   Integer userId; //user_id列

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
