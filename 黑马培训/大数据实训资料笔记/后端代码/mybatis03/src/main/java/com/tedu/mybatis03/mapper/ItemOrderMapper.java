package com.tedu.mybatis03.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tedu.mybatis03.pojo.ItemOrderEntity;
import org.apache.ibatis.annotations.Mapper;

//数据访问层
@Mapper
public interface ItemOrderMapper extends BaseMapper<ItemOrderEntity> {
}
