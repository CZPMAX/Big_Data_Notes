package com.tedu.mybatisplus01.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

//实体类，放date_tongji表的数据
@TableName("date_tongji")
public class DateTongjiEntity {

    @TableField("register_date")
  String registerDate;  //register_date列

    @TableField("total")
    Integer total; //total列

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
