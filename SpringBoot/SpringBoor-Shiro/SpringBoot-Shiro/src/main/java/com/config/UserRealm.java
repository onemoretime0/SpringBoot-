package com.config;


import com.pojo.User;
import com.service.UserServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.relation.Role;


public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserServiceImpl userService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了授权功能");
        //给用户授权
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Subject subject = SecurityUtils.getSubject();
        //通过subject拿到已经登陆的用户
        User currentUser = (User) subject.getPrincipal();
        
        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("认证");
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;

        User user = userService.queryUserByName(userToken.getUsername());
        if (user == null) {
            return null;
        }
        //可以加密，MD5 ， MD5盐值加密
        return new SimpleAuthenticationInfo(user, user.getPwd(), "");
    }
}
