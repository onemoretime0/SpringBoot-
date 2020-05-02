package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class JdbcController {
    //获取JDBCTemplate
    @Autowired
    JdbcTemplate jdbcTemplate;

    //查询数据库中所有的信息
    @RequestMapping("/userList")
    public List<Map<String, Object>> getUserList() {
        String sql = "select * from user";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        return maps;
    }

    //增加一个用户
    @GetMapping("/addUser")
    public String addUser() {
        String sql = "insert into user(id,name,pwd) values(9,'大老师','admin')";
        int update = jdbcTemplate.update(sql);
        //SpringBoot自动提交事务
        if (update < 0) {
            return "执行失败";
        } else {
            return "执行成功";
        }
    }

    //修改一个用户
    @RequestMapping("/updateUser/{id}")
    public String updateUser(@PathVariable("id") int id) {
        String sql = "update mybatis.user set name= ? ,pwd= ? where id= " + id;
        Object[] objects = new Object[2];
        objects[0] = "川建国";
        objects[1] = "admin";
        int update = jdbcTemplate.update(sql, objects);
        if (update > 0) {
            return "更新成功";
        } else {
            return "更新失败";
        }
    }

    //修改一个用户
    @RequestMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        String sql = "delete from mybatis.user where id = ?";
        int update = jdbcTemplate.update(sql, id);
        if (update > 0) {
            return "删除成功";
        } else {
            return "删除失败";
        }
    }

}
