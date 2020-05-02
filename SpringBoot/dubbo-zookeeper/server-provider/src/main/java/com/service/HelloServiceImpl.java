package com.service;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

@Component
@Service //此处的Service一定要导入Dubbo的包：org.apache.dubbo.config.annotation.Service;
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String message) {
        return "hello"+message;
    }
}
