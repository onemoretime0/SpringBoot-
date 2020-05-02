package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @RequestMapping("/user/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model, HttpSession session) {
        //具体业务
        if (!StringUtils.isEmpty(username) && "123".equals(password)) {
            //登录成功，重定向到main.html,我们在视图解析器中main.html映射到了dashboard.html，这样解决了地址栏显式的用户名和密码的问题
           //登录成功，向session中存储username
            session.setAttribute("LoginUser",username);
            return "redirect: /main.html";
        } else {
            //通知用户登陆失败
            model.addAttribute("msg", "用户名或者密码错误");
            return "index";
        }
    }
}
