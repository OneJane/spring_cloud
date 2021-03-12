package com.onejane.demo.consumer.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("provider")
public interface ProviderFeignService {

    @RequestMapping("/test/user")
    public void test();

}