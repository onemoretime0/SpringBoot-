package com.controller;

import com.service.HelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Reference
    HelloService helloService;

    @RequestMapping("/hello/{message}")
    public String hello(@PathVariable("message") String message){
        return helloService.sayHello(message);
    }
}
