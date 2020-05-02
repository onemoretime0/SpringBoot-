package com.config;


import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.LinkedHashMap;

import java.util.Map;

@Configuration
public class ShiroConfig {

    //ShiroFilterFactoryBean
    @Bean(name = "shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();

        //绑定DefaultWebSecurityManager,设置安全管理器
        bean.setSecurityManager(securityManager);

        //添加Shiro的内置过滤器,setFilterChainDefinitionMap()方法的参数是一个Map集合
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/", "anon");//主页所有人都可以访问
        filterMap.put("/user/*", "authc");
        //授权，正常情况下，没有授权会跳转到未授权页面
        //访问/user/add的用户必须有add权限


        bean.setFilterChainDefinitionMap(filterMap);

        //设置登录页面，当请请求没有权限的资源的时候，跳转到登录页面
        bean.setLoginUrl("/toLogin");

        //设置未授权的页面
        bean.setUnauthorizedUrl("/noauth");

        return bean;
    }

    //DefaultWebSecurityManager
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联Realm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    //创建Realm对象，此对象需要自定义类并继承AuthorizingRealm
    @Bean
    public UserRealm userRealm() {
        return new UserRealm();
    }

    //整合Thymeleaf,ShiroDialect对象
    @Bean
    public ShiroDialect getShiroDialect() {
        return new ShiroDialect();
    }
}
