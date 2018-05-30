package com.szlib.seeker.dubbo.service.provider;

import com.szlib.seeker.dubbo.service.DemoService;

public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
