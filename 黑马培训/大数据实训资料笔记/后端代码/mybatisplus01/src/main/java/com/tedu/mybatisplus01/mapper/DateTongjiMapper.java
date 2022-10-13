package com.tedu.mybatisplus01.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tedu.mybatisplus01.pojo.DateTongjiEntity;
import org.apache.ibatis.annotations.Mapper;
//DateTongjiEntity 对应date_tongji表
@Mapper
public interface DateTongjiMapper extends BaseMapper<DateTongjiEntity> {
}
