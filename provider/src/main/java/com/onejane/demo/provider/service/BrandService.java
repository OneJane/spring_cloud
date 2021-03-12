package com.onejane.demo.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onejane.demo.provider.entity.BrandEntity;
import com.onejane.demo.provider.utils.PageUtils;

import java.util.Map;

public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
}