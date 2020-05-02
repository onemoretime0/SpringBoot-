package com.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class UserController {

    @RequestMapping("/user/add")
    public String add() {
        return "user/add";
    }

    @RequestMapping("/user/update")
    public String update() {
        return "user/update";
    }

    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/login")
    public String login(String username, String password, Model model) {
        //获取获取当前的用户,在QuickStart类中,操作参考QuickStart
        Subject subject = SecurityUtils.getSubject();
        //封装用户的登陆数据
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        //登录
        try {
            subject.login(token);//login()方法帮我们执行登录的方法，没有异常则就Ok
            return "index";
        } catch (UnknownAccountException uae) {
            //用户名不存在
            model.addAttribute("msg", "用户名不存在");
            return "login";
        } catch (IncorrectCredentialsException ice) {
            model.addAttribute("msg", "密码错误");
            return "login";
        }
    }

    @RequestMapping("/noauth")
    @ResponseBody
    public String unauthorized(){
        return "未经授权无法访问此页面";
    }
}
