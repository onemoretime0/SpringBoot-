

# 1.SpringSecurity是什么



Spring Security，这是一种基于 Spring AOP 和 Servlet 过滤器的安全框架。它提供全面的安全性解决方案，同时在 Web 请求级和方法调用级处理身份确认和授权

Spring Security 是 Spring 家族中的一个安全管理框架，实际上，在 Spring Boot 出现之前，Spring Security 就已经发展了多年了，但是使用的并不多，安全管理这个领域，一直是 Shiro 的天下。

相对于 Shiro，在 SSM/SSH 中整合 Spring Security 都是比较麻烦的操作，所以，Spring Security 虽然功能比 Shiro 强大，但是使用反而没有 Shiro 多（Shiro 虽然功能没有 Spring Security 多，但是对于大部分项目而言，Shiro 也够用了）。

自从有了 Spring Boot 之后，Spring Boot 对于 Spring Security 提供了 自动化配置方案，可以零配置使用 Spring Security。

因此，一般来说，常见的安全管理技术栈的组合是这样的：

- SSM + Shiro
- Spring Boot/Spring Cloud + Spring Security

**注意，这只是一个推荐的组合而已，如果单纯从技术上来说，无论怎么组合，都是可以运行的。**

# 2.Spring Security-环境搭建

- 创建SpringBoot项目
- 导入静态资源
- 编写Contorller完成页面跳转功能

```java
@Controller
public class RouterController {

    //访问首页
    @RequestMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    //跳转到登录页面
    @RequestMapping("/toLogin")
    public String toLogin() {
        return "views/login";
    }

    @RequestMapping("/level1/{id}")
    public String toLevel1(@PathVariable("id") int id) {
        return "views/level1/" + id;
    }

    @RequestMapping("/level2/{id}")
    public String toLevel2(@PathVariable("id") int id) {
        return "views/level2/" + id;
    }

    @RequestMapping("/level3/{id}")
    public String toLevel3(@PathVariable("id") int id) {
        return "views/level3/" + id;
    }
}

```

# 3.Spring Security-使用

## 3.1 如何使用

要使用Spring Security只需要导入``spring-boot-starter-security``模块，进行少量配置就可以了！

**记住几个类：**

- ``WebSecurityConfigurerAdapter``：自定义Security策略
- ``AuthenticationManagerBuilder``：自定义认证策略
- ``@EnableWebSecurity``：开启WebSecurity模式（在SpringBoot中，使用@EnableXXX 开启某个功能）

Spring Security的两个主要的目标就**认证**和**授权**（访问控制）

- 认证（Authentication）
- 授权（Authorization）

这个概念是通用的而不是仅仅在Spting Security中存在。

官方文档：https://docs.spring.io/spring-security/site/docs/5.3.2.BUILD-SNAPSHOT/reference/html5/

SpringBoot+Security：https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-security.html

官方依赖地址：https://github.com/spring-projects/spring-boot/blob/v2.1.9.RELEASE/spring-boot-project/spring-boot-starters/spring-boot-starter-security/pom.xml

### 配置

在pom.xml文件中导入Spring Security的启动器

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**然后开始配置**

创建Security的Config类，继承WebSecurityConfigurerAdapter即可，首先看官方给出的例子：

```java
@EnableWebSecurity
public class Config extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .apply(customDsl())
                .flag(true)
                .and()
            ...;
    }
}
/*
定义自己的Security的配置类，只需要继承WebSecurityConfigurerAdapter类，然后用@EnableWebSecurity注解标注
然后重写需要的方法即可
```



## 3.2 开始使用



### 授权

**自己的配置类：**

````java
//AOP面向切面编程，只需要添加一个类，就可以完成配置，而不用修改之前的代码
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //配置规则
        //首页所有人都可以访问，但是首页中的功能页只有有权限的人才可以访问
        //定义请求授权的规则
        //链式编程
        http
                .authorizeRequests().                           //请求授权
                antMatchers("/").permitAll().       //permitAll： 首页所有人都可以访问
                antMatchers("/level1/**").hasRole("vip1"). // level1下的所有页面只有vip1才可以访问
                antMatchers("/level2/**").hasRole("vip2").
                antMatchers("/level3/**").hasRole("vip3");

    }
}
````

这样在首页中请求功能页的时候，就会403没有权限

![](.\image\安全框架\Security-请求授权效果.png)

但是这样不符合业务流程，应该在用户请求没有权限的时候跳转到登陆页面，所以再增加一个视图跳转。只需要一行代码：

````java
 //用户在没有权限的情况下，默认会跳转到登录页面,开启登录页面
http.formLogin();
//注意：这行代码还是在刚在配置类中的configure方法中编写，它会自动帮我们跳转到登录页面，我们设置没有这个Controller，而且登录页面设置不是我们自己写的
/*
官网给出的说明：
 * The most basic configuration defaults to automatically generating a login page at
	 * the URL "/login", redirecting to "/login?error" for authentication failure. 
	 *默认情况下，最基本的配置是在URL“ / login”处自动生成一个登录页面，并重定向到“ / login？error”来进行身份			验证失败。
*/
````

![](.\image\安全框架\Security-fronLogin.png)



### 认证

利用configure的重载方法来编写认证配置（多看源码，源码的注释中已经帮我们写好了如何去编写一个认证配置或者授权配置）

````java
 @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //用户可以在数据库中拿: auth.jdbcAuthentication()，也可以直接在内存中拿：auth.inMemoryAuthentication()
        auth.inMemoryAuthentication()
                .withUser("root").password("admin").roles("vip1", "vip2", "vip3")
                .and()  //使用and()方法来拼接下一个
                .withUser("hnl").password("123").roles("vip1")
                .and()
                .withUser("zhangsan").password("1234").roles("vip2")
                .and()
                .withUser("lisi").password("1234").roles("vip3");
    }
````

启动测试：

在主页请求其他页面，然后登录验证：

![](.\image\安全框架\Security-密码没有加密错误.png)



**错误分析：**

```
 springboot 2.1.xx 版本可以直接使用，但是springboot2.2.xx及以上密码需要加密
*      否则会报一个There is no PasswordEncoder mapped for the id "null" 错误
*  解决：Spring Security 5.0+ 版本中增加了很多的加密方式，使用提供的加密方式对密码进行加密
```

**解决：调用passwordEncoder()方法，参数传递加密规则**

- 这些加密规则都是以XXXXPasswordEncoder()格式的类，可以在IDEA中直接搜索，然后选择自己想要的

```java
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
```

### 注销

源码给出的logout方案：

````java
/* 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http.authorizeRequests().antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;).and().formLogin()
	 * 				.and()
	 * 				// sample logout customization
	 * 				.logout().deleteCookies(&quot;remove&quot;).invalidateHttpSession(false)
	 * 				.logoutUrl(&quot;/custom-logout&quot;).logoutSuccessUrl(&quot;/logout-success&quot;);
	 * 	}
//logout的功能：
	//删除cookie(一般不会这么做)
	//注销页面lotout
	//logoutSuccessUrl注销成功跳转的页面
````

logout页面也是Security帮我们写好的。

```html
<!--注销-->
<a class="item" th:href="@{/logout}">
    <i class="sign-out icon"></i> 注销
</a>
<!--注销按钮绑定logoutController-->
```

 注销操作的配置（这个功能也是需要在授权的配置类中进行配置，因为参数是HttpSecurity）

````java
//开启注销功能,注销后跳转首页
http.logout().logoutSuccessUrl("/");
````

### 页面权限控制

当用户登录之后，页面应该只显示该用户权限之内的操作，这个操作就需要用权限控制来来完成。

**thymeleaf与security的整合**

导入thymeleaf与security的整合包，有了这个整合包之后我们就可以在thmeleaf中做一些springsecurity的操作：

```xml
<!-- https://mvnrepository.com/artifact/org.thymeleaf.extras/thymeleaf-extras-springsecurity4 -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity4</artifactId>
    <version>3.0.4.RELEASE</version>
</dependency>
```

**注意：在页面中使用springsecurity导入的命名空间是thymeleaf的而不是w3c的**

````html
<html lang="en" 
      xmlns:th="http://www.thymeleaf.org" 
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity4">
<!---命名空间的导入只需要在thymeleaf网址的后面加上整合包依赖的包名即可-->
````

使用：

**注意：由于现在使用的环境是springboot2.2.6 版本过高，所以不能达到想要的页面效果，需要降版本，降到springboot2.1以下的版本**

````html
<!--登录注销-->
<div class="right menu">
    <!--如果未登录则显示登陆按钮 isAuthenticated()验证用户是否已经登陆-->
    <div sec:authorize="!isAuthenticated()">
        <!--未登录-->
        <a class="item" th:href="@{/toLogin}">
            <i class="address card icon"></i> 登录
        </a>
    </div>
    <!--如果已经登录显式用户名和注销按钮-->
    <div sec:authorize="isAuthenticated()">
        <!--注销-->
        <a class="item" th:href="@{/logout}">
            <i class="sign-out icon"></i> 注销
        </a>
    </div>
    <!--显示用户名-->
    <div sec:authorize="isAuthenticated()">
        <!--注销-->
        <a class="item">
            用户名：<span sec:authentication="name"></span>
        </a>
    </div>
````

**还可以根据用户权限的不同显式不同的页面，不如说下面的这个块只有有VIP1的用户才有权限看：**

```html
<!--菜单根据用户的权限动态显式-->
<div class="column" sec:authentication="hasRole('vip1')">
    <div class="ui raised segment">
        <div class="ui">
            <div class="content">
                <h5 class="content">Level 1</h5>
                <hr>
                <div><a th:href="@{/level1/1}"><i class="bullhorn icon"></i> Level-1-1</a></div>
                <div><a th:href="@{/level1/2}"><i class="bullhorn icon"></i> Level-1-2</a></div>
                <div><a th:href="@{/level1/3}"><i class="bullhorn icon"></i> Level-1-3</a></div>
            </div>
        </div>
    </div>
</div>
```





### 防止跨站请求伪造CSRF

在提交请求的时候，get请求是明文传输的，是不安全的，post请求是安全的，但是我们只有在提交表单的时候才可以选择请求的方式，至于``<a>``以及一些其他的标签是不可以选择请求方式的。所以这是不安全的

spring 中CSRF是默认开启的，这是不安全的，所以我们要在springsecurity中关闭CSRF功能：

```java
http.csrf().disable();
//也是在参数为HttpSecurity的configure方法进行设置
```

### 记住我

 开启记住我功能：

```java
//开启记住我功能非常简单，只需要在在配置类中调用相应的功能即可,记住我功能本身就是一个cookie的实现
//开启记住我功能，默认保存两周
http.rememberMe();
```

![](.\image\安全框架\Security-Rememberme.png)

当开启记住我功能之后，即使重启浏览器，再次请求首页中相关的子功能，也可以请求的到

### 首页定制

到此为止，我们一直使用的都是SpringSecurity的登陆页面，那我们想用自己的登陆页面怎么实现呢？

````java
 //这是源码中给我们的功能
/* 	&#064;Override
	 * 	protected void configure(HttpSecurity http) throws Exception {
	 * 		http
	 * 			.authorizeRequests(authorizeRequests ->
	 * 				authorizeRequests
	 * 					.antMatchers(&quot;/**&quot;).hasRole(&quot;USER&quot;)
	 * 			)
	 * 			.formLogin(formLogin ->
	 * 				formLogin
	 * 					.usernameParameter(&quot;username&quot;)
	 * 					.passwordParameter(&quot;password&quot;)
	 * 					.loginPage(&quot;/authentication/login&quot;)
	 * 					.failureUrl(&quot;/authentication/login?failed&quot;)
	 * 					.loginProcessingUrl(&quot;/authentication/login/process&quot;)
	 * 			);
	 * 	}
````

**设置**

````java
//使用loginPage()设置我们自己的登录页面, loginProcessingUrl()设置的登陆提交的url
http.formLogin().loginPage("/toLogin").loginProcessingUrl("/login");
````

但是这样还是有会报错，看官方给出的代码：

````java
.usernameParameter(&quot;username&quot;)
.passwordParameter(&quot;password&quot;)
//这来两行代码用来设置表单提交的参数名字，如果不进行设置，可能会登陆不成功，在表单中的参数名为username和password的时候才会登陆成功，如果不是就会登陆失败，为了统一就需要进行设置
````

**实现：**

```java
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
```

