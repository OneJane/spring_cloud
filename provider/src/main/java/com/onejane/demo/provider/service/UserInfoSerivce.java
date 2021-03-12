package com.onejane.demo.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onejane.demo.provider.entity.UserInfoEntity;
import com.onejane.demo.provider.utils.PageUtils;

import java.util.Map;

public interface UserInfoSerivce extends IService<UserInfoEntity> {
    PageUtils queryPage(Map<String, Object> params);
}
