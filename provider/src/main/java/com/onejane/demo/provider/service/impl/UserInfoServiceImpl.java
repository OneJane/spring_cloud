package com.onejane.demo.provider.service.impl;

import com.onejane.demo.provider.dao.UserInfoDao;
import com.onejane.demo.provider.entity.UserInfoEntity;
import com.onejane.demo.provider.service.UserInfoSerivce;
import com.onejane.demo.provider.utils.PageUtils;
import com.onejane.demo.provider.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("userInfoService")
public class UserInfoServiceImpl extends ServiceImpl<UserInfoDao, UserInfoEntity> implements UserInfoSerivce {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserInfoEntity> page = this.page(
                new Query<UserInfoEntity>().getPage(params),
                new QueryWrapper<UserInfoEntity>()
        );

        return new PageUtils(page);
    }

}