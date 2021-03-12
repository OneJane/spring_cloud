package com.onejane.demo.provider.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.onejane.demo.provider.entity.BrandEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {
	
}