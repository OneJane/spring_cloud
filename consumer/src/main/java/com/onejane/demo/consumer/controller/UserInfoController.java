package com.onejane.demo.consumer.controller;

import com.onejane.demo.consumer.feign.ProviderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class UserInfoController {
    @Autowired
    ProviderFeignService providerFeignService;
    @RequestMapping("/user")
    public void test(){
        providerFeignService.test();
    }
}
