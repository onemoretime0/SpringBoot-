package com.controller;

import com.pojo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;



@RestController
public class HelloController {

    @GetMapping("/hellp")
    public String hello() {
        return "Hello";
    }

    //只要我们的接口中返回值中存在实体类，就会被扫描到Swagger文档中
    @PostMapping("/user")
    public User user() {
        return new User();
    }

    //@ApiOperation:接口，可以注释接口的业务功能等
    @ApiOperation("SayHi,username")
    @PostMapping("/hello/{username}")
    public String hello(@PathVariable("username") @ApiParam("参数是用户名") String username) {
        return "Hello," + username;
    }
}
