package com.onejane.demo.consumer.controller;

import com.onejane.demo.consumer.feign.ProviderFeignService;
import com.onejane.demo.consumer.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("test")
public class UserInfoController {
    @Autowired
    ProviderFeignService providerFeignService;
    @Autowired
    private UserServiceImpl userService;
    @RequestMapping("/user")
    public void test(){
        providerFeignService.test();
    }
    @GetMapping(value = "/arthas")
    public HashMap<String, Object> getUser(Integer uid) throws Exception {
        // 模拟用户查询
        userService.get(uid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("name", "name" + uid);
        return hashMap;
    }
}
