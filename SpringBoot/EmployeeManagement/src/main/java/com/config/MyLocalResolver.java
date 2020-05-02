package com.config;



import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class MyLocalResolver implements LocaleResolver {
    //解析请求
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        //获取请求参数中的信息，判断是中文还是英文
        String language = request.getParameter("l");
        //参照AcceptHeaderLocaleResolver的源码编写
        Locale locale = Locale.getDefault();//如果没有就是用默认的

        //如果请求的参数不为空，就是员工
        if(!StringUtils.isEmpty(language)){
            //zh_CN 分割字符串
            String[] s = language.split("_");
            locale = new Locale(s[0], s[1]);

        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}
