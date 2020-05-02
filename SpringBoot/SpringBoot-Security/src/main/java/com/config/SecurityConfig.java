package com.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//AOP面向切面编程，只需要添加一个类，就可以完成配置，而不用修改之前的代码
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //请求授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //配置规则
        //首页所有人都可以访问，但是首页中的功能页只有有权限的人才可以访问
        //链式编程
        http
                .authorizeRequests().                           //授权请求
                antMatchers("/").permitAll().       //permitAll： 首页所有人都可以访问
                antMatchers("/level1/**").hasRole("vip1"). // level1下的所有页面只有vip1才可以访问
                antMatchers("/level2/**").hasRole("vip2").
                antMatchers("/level3/**").hasRole("vip3");
        //用户在没有权限的情况下，默认会跳转到登录页面
        //使用loginPage()设置我们自己的登录页面,loginProcessingUrl()设置的登陆提交的url
        http.formLogin().loginPage("/toLogin")
                .usernameParameter("user")
                .passwordParameter("pwd")
                .loginProcessingUrl("/login");

        http.csrf().disable();
        //开启注销功能,注销后跳转首页
        http.logout().logoutSuccessUrl("/");

        //开启记住我功能,对于记住我按钮，也就设置是接收的参数名
        http.rememberMe().rememberMeParameter("remember");

    }


    /**
     * 利用configure的重载方法来编写认证配置
     * springboot 2.1.xx 版本可以直接使用，但是springboot2.2.xx及以上密码需要加密
     * 否则会报一个There is no PasswordEncoder mapped for the id "null" 错误
     * 解决：Spring Security 5.0+ 版本中增加了很多的加密方式
     * auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
     * XXXPasswordEncoder都是框架提供的加密规则
     * 然后在password中设置编码规则
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //用户可以在数据库中拿: auth.jdbcAuthentication()，也可以直接在内存中拿：auth.inMemoryAuthentication()
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())

                .withUser("root").password(new BCryptPasswordEncoder().encode("admin")).roles("vip1", "vip2", "vip3")
                .and()  //使用and()方法来拼接下一个
                .withUser("hnl").password(new BCryptPasswordEncoder().encode("123")).roles("vip1")
                .and()
                .withUser("zhangsan").password(new BCryptPasswordEncoder().encode("123")).roles("vip2")
                .and()
                .withUser("lisi").password(new BCryptPasswordEncoder().encode("123")).roles("vip3");
    }
}
