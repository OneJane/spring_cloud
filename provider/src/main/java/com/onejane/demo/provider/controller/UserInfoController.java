package com.onejane.demo.provider.controller;

import com.onejane.demo.provider.service.UserInfoSerivce;
import com.onejane.demo.provider.utils.PageUtils;
import com.onejane.demo.provider.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("test")
public class UserInfoController {

    @Value("${coupon.user.name}")
    private String name;
    @Value("${coupon.user.age}")
    private Integer age;
    @Autowired
    UserInfoSerivce userInfoSerivce;

    @RequestMapping("/user")
    public void test(){
        System.out.println(name+":"+age);
    }

    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userInfoSerivce.queryPage(params);
        return R.ok().put("page", page);
    }

    @RequestMapping("/delete")
    public R delete(Long[] ids){
        userInfoSerivce.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}