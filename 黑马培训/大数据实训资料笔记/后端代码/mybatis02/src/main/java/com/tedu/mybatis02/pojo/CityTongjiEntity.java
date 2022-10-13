package com.tedu.mybatis02.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

//实体类
@TableName("city_tongji")
public class CityTongjiEntity {
 @TableField("city")
    String city;   //city列

    @TableField("total")
    Integer total;//total列

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
