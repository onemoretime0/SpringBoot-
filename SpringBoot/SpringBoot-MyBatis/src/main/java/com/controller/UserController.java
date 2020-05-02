package com.controller;

import com.mapper.UserMapper;
import com.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/queryUserList")
    public List<User> queryUserList() {
        List<User> users = userMapper.queryUserList();
        return users;
    }

    @GetMapping("/queryUserById/{id}")
    public User queryUserById(@PathVariable("id") int id) {
        User user = userMapper.queryUserById(id);
        return user;
    }

    @GetMapping("/addUser")
    public String addUser() {
        int i = userMapper.addUser(new User(10,"jojo","admin"));
        if (i > 0) {
            return "添加成功";
        } else {
            return "添加失败";
        }
    }
}
