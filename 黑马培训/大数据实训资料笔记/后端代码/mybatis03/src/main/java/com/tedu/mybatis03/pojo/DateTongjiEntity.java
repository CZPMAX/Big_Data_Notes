package com.tedu.mybatis03.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

//实体类
@TableName("date_tongji")
public class DateTongjiEntity {
   @TableField("register_date")
    String date; //date列

    @TableField("total")
    Integer total; //total列

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
