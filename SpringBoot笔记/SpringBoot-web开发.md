# 1.web开发探究

使用spring boot来发web项目要就解决的问题：

- 导入静态资源
- 首页
- jsp，模板引擎Thymeleaf
- 装配扩展springmvc
- 增删改查
- 拦截器
- 国际化



关于在Springoot中的web开发，重要类是WebMvcAutoConfiguration，这个类配置了web开发所需要的规则。

# 2.静态资源导入

在springboot中，我们可以使用以下处理静态资源

1. webjars：在webjars官网以maven依赖的方式导入静态资源（https://www.webjars.org/）,那么导入的静态资源就在``classpath:/META-INF/resources/webjars/``目录下
	- 映射路径：``localhost/webjars/``
2. 静态资源放置的位置：
	- ``classpath:/resources``(同名文件优先级最高)
	- ``classpath:/static``(同名文件优先级次高)
	- ``classpath:/public``(同名文件优先级最低)
	- ``/**``
	- 映射路径：``localhost/``

# 3.首页

首页名称index.html，可以放置在：

- resources
- static
- public 

**放置在templates文件夹下页面必须使用controller进行跳转，并且需要模板引擎的支持**

# 4.thymeleaf模板引擎



## 4.1模板引擎

前端交给我们的页面是html页面。如果是以前，我们需要将HTML页面转换为jsp页面。但是现在SpringBoot打包方式是jar的形式的，不是war，我们现在用的是内嵌式的tomcat，默认是不支持jsp的。



jsp就是就是一个模板引擎，还有freemarker，包括SpringBoot给我们推荐的Thymeleaf。模板引擎非常多，但是思想都是相同的

![](.\image\模板引擎.png)

**模板引擎的作用：**

将动态的值变成表达式套在模板引擎里面，模板引擎按照值将表达式解析，填充到指定的位置，然后将这个数据最终生成一个我们想要的内容给我们写出去，

## 4.2 Thymeleaf使用前提

官网：https://www.thymeleaf.org/index.html

在SpringBoot中使用Thymeleaf需要导入Thymeleaf的依赖，GitHub地址：https://github.com/spring-projects/spring-boot/blob/v2.1.9.RELEASE/spring-boot-project/spring-boot-starters/spring-boot-starter-thymeleaf/pom.xml

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
</dependency>
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-java8time</artifactId>
</dependency>
```

**注意：需要保证导入的包是3.0版本以上的，使用最新的SpringBoot默认就是3.0版本与以上的**

Thymeleaf的Properties类：

```java
@ConfigurationProperties(prefix = "spring.thymeleaf")
public class ThymeleafProperties {

	private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;//默认编码

	public static final String DEFAULT_PREFIX = "classpath:/templates/";//前缀

	public static final String DEFAULT_SUFFIX = ".html";//后缀
    ....
}
```

**页面需要放置的templates目录下**

## 4.3 使用Thymeleaf

是要在HTML页面中导入thymeleaf的约束：

```html
<html xmlns:th="http://www.thymeleaf.org" xmlns:tiles="http://www.thymeleaf.org">
```

所有的html元素都可以被Thymeleaf接管，``th:属性名``

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:tiles="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1 th:text="${msg}"></h1>
</body>
</html>
```

Controller:

```java
@RequestMapping("/test")
public String test(Model model){
    model.addAttribute("msg","Hello,Thymeleaf");
    return "test";
}
```



## 4.4 Thymeleaf语法



### 4.4.1 Thymeleaf获取数据

- Variable Expressions（普通变量）：``${...}``
- Selection Variable Expressions（）：``*{...}``
- Message Expressions（国际化消息）：``#{....}``
- Link URL Expressions：``@{...}``
- Fragment Expressions：``~{...}``





### 4.4.2转义or非转义

Controller：

```java
@RequestMapping("/test")
public String test(Model model){
    model.addAttribute("msg","<h1>Hello,Thymeleaf</h1>");
    return "test";
}
```

```html
<div th:text="${msg}"></div>
<div th:utext="${msg}" ></div>
```

![](.\image\Thymeleaf转义or非转义.png)

### 4.4.3 遍历数据

Controller：

```java
@RequestMapping("/getUser")
public String getUser(Model model){
    List<String> list = new ArrayList<>();
    list.add("法外狂狂徒张三");
    list.add("王司徒");
    list.add("诸葛孔明");

    model.addAttribute("users",list);
    return "user";
}
```

Html：

```html
<body>
<ul th:each=" user:${users}">
    <li th:text="${user}"></li>
</ul>

<div>
    <h1 th:each="user:${users}" th:text="${user}"></h1>
    <h1 th:each="user:${users}">[[ ${user} ]]</h1>
</div>
</body>
<!--遍历的基本格式与Vue相差无几-->
```

# 5.MVC配置和SpringMVC扩展

## 5.1 MVC配置原理

官方文档：https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html

SpringBoot帮我们们自动配置了这些东西：

- Inclusion of and beans.`ContentNegotiatingViewResolver``BeanNameViewResolver`
- Support for serving static resources, including support for WebJars (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-static-content))).
- Automatic registration of , , and beans.`Converter``GenericConverter``Formatter`
- Support for (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-message-converters)).`HttpMessageConverters`
- Automatic registration of (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-message-codes)).`MessageCodesResolver`
- Static support.`index.html`
- Custom support (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-favicon)).`Favicon`
- Automatic use of a bean (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-web-binding-initializer)).`ConfigurableWebBindingInitializer`

如果想要diy一些定制化的功能，只要写这个组件，并将其交给springboot，springboot就会帮我们自动装配

比如我们想要配置自己的视图解析器，只要实现编写一个配置类，编写一个自己的视图解析器并将其交给springboot：

```java
//扩展SpringMVC
//1.添加@Configuration注解
@Configuration
//2.实现WebMvcConfiguration
public class MyMVCConfig implements WebMvcConfigurer {

    //public interface ViewResolver {} 实现了视图解析器接口的类，就可以看作是视图解析器

    //将自定义的视图解析器注册到容器中
    @Bean
    public  ViewResolver myViewResolver(){
        return new MyViewResolver();
    }
    //自定义的视图解析器
    private static class MyViewResolver implements ViewResolver {

        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            return null;
        }
    }
}

```

## 5.2 修改SpringBoot默认的配置

SpringBoot这么多的配置，原理其实都是相同的，通过上面WebMVC的自动配置原理分析，我们要学会一种学习方式，**通过源码探究，得出结论**

SpringBoot如何加载配置：

SpringBoot在自动配置很多组件的时候，先看容器中有没有用户自己配置的，如果有就用用户自己配置的，如果没有就用自动配置的；如果这些组件可以存在多个，比如我们的视图解析器，就将用户配置的和自动配置的集合起来

## 5.3 扩展SpringMVC

**官方给出的方案：**If you want to keep Spring Boot MVC features and you want to add additional [MVC configuration](https://docs.spring.io/spring/docs/5.1.10.RELEASE/spring-framework-reference/web.html#mvc) (interceptors, formatters, view controllers, and other features), you can add your own `@Configuration` class of type `WebMvcConfigurer` but **without** `@EnableWebMvc`. If you wish to provide custom instances of `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter`, or `ExceptionHandlerExceptionResolver`, you can declare a `WebMvcRegistrationsAdapter` instance to provide such components.

我们要做的就是编写一个@Configuration配置类，并且继承WebMvcConfigurer，但是不能标注@EnableWebMvc注解

比如现在要写一个控制视图跳转的config，配置类放在config包下

````java
//如果想要扩展SpringMVC，官方建议我们这样做
@Configuration
public class MyMVCConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //控制视图跳转
        registry.addViewController("/index").setViewName("test");
    }
}
````

### 扩展

**为什么扩展springmvc的时候官方不让加@EnableWebMvc注解**

分析源码：

````java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc {
}
/*
这个注解导入一个类DelegatingWebMvcConfiguration
*/
````

DelegatingWebMvcConfiguration类的作用：从容器中获取所有的webmvcconfig

源码：

```java
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {}
//它继承了WebMvcConfigurationSupport这个类
```

我们再看WebMvcAutoConfiguration类的定义：

````java
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
/*发现是这个注解@ConditionalOnMissingBean使用了WebMvcConfigurationSupport，这个注解的作用是当WebMvcConfigurationSupport不存在的时候自动配置的东西才会生效*/
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@AutoConfigureAfter({ DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
		ValidationAutoConfiguration.class })
public class WebMvcAutoConfiguration {}
````

当我们使用@EnableWebMvc注解标注了我们的配置类，那么WebMvcConfigurationSupport就会存在让容器中，@ConditionalOnMissingBean注解就会检测到此类的存在，那么所有自动配置的东西都会失效，导致整个程序的崩盘

## 小结

在SpringBoot中，有非常多的XXX Configurer帮助我们进行扩展配置，只要看见了这个东西，我们就要注意了

# 6.整合jdbc

## 6.1 简介

对于是数据访问层，无论是SQL还是NoSQL，SpringBoot底层都采用SpringData的方式进行统一处理。

SpringBoot底层都采用SpringData的方式进行同意处理各种数据库，SpringData也是Spring中与SpringBoot、SpringCloud等齐名的项目。

SpringData官网：https://spring.io/projects/spring-data

## 6.2 创建项目

- 勾选 jdbc API
- 勾选数据库连接驱动



## 6.3配置数据源

###  编写datasource配置文件

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ssmbuild?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true
    password: MySQLadmin
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    #配置中的数据库的用户名不能写为name，应该是username,也不要写成data-username
```

### 测试连接

查看一下默认的数据源：

```java
@Autowired
private DataSource dataSource;

@Test
public void DataSourceTest() throws SQLException {
    //查看默认的数据源
    System.out.println(dataSource.getClass());
    Connection connection = dataSource.getConnection();
    System.out.println(connection);
    connection.close();
}
/*
class com.zaxxer.hikari.HikariDataSource
HikariProxyConnection@1122694271 wrapping com.mysql.cj.jdbc.ConnectionImpl@312e8eaf
*/
```

## 6.4 如何使用SpringBoot 完成增删改查

拿到数据库连接之后们就可以对数据库进行增删改查操作了。

在SpringBoot中有很多``XXXTemplate``的类，Template（模板），``XXXTemplate``就是SpringBoot框架配置后的Bean。开箱即用

比如现在使用jdbc来操作数据库，那么就要使用JdbcTemplate。

查看``JdbcTemplateAutoConfiguration``的源码：

````java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ DataSource.class, JdbcTemplate.class })
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(JdbcProperties.class)
@Import({ JdbcTemplateConfiguration.class, NamedParameterJdbcTemplateConfiguration.class })
public class JdbcTemplateAutoConfiguration {

}
````

``JdbcTemplateConfiguration``的源码：

````java
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(JdbcOperations.class)
class JdbcTemplateConfiguration {

	@Bean
	@Primary
	JdbcTemplate jdbcTemplate(DataSource dataSource, JdbcProperties properties) {
        /*jdbcTemplate中传递一个数据源和配置就可以了*/
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		JdbcProperties.Template template = properties.getTemplate();
		jdbcTemplate.setFetchSize(template.getFetchSize());
		jdbcTemplate.setMaxRows(template.getMaxRows());
		if (template.getQueryTimeout() != null) {
			jdbcTemplate.setQueryTimeout((int) template.getQueryTimeout().getSeconds());
		}
		return jdbcTemplate;
	}

}
/*发现JdbcTemplate已经注入到Spring中了，可以直接拿来使用*/
````

而JdbcTemplate帮我们做了大量的封装用来完成对数据库的操作。

## 6.5 CRUD

```JAVA
@RestController
public class JdbcController {
    //获取JDBCTemplate
    @Autowired
    JdbcTemplate jdbcTemplate;

    //查询数据库中所有的信息
    @RequestMapping("/userList")
    public List<Map<String, Object>> getUserList() {
        String sql = "select * from mybatis.user";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        return maps;
    }

    //增加一个用户
    @GetMapping("/addUser")
    public String addUser() {
        String sql = "insert into mybatis.user(id,name,pwd) values(9,'大老师','admin')";
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
```

# 7.整合Druid



## 7.1 介绍

 Druid可以很好的监控DB池连接和SQL的执行情况,并加入了日志监控

SpringBoot2.0以上默认使用Hikari数据源，可以说Hikari和Druid都是当前Javaweb上最优秀的数据源，

**com.alibaba.druid.pool.DruidDataSource基本配置参数：**

| 配置                          | 缺省值             | 说明                                                         |
| ----------------------------- | ------------------ | ------------------------------------------------------------ |
| name                          |                    | 配置这个属性的意义在于，如果存在多个数据源，监控的时候可以通过名字来区分开来。  如果没有配置，将会生成一个名字，格式是："DataSource-" + System.identityHashCode(this) |
| jdbcUrl                       |                    | 连接数据库的url，不同数据库不一样。例如：  mysql : jdbc:mysql://10.20.153.104:3306/druid2  oracle : jdbc:oracle:thin:@10.20.149.85:1521:ocnauto |
| username                      |                    | 连接数据库的用户名                                           |
| password                      |                    | 连接数据库的密码。如果你不希望密码直接写在配置文件中，可以使用ConfigFilter。详细看这里：https://github.com/alibaba/druid/wiki/%E4%BD%BF%E7%94%A8ConfigFilter |
| driverClassName               | 根据url自动识别    | 这一项可配可不配，如果不配置druid会根据url自动识别dbType，然后选择相应的driverClassName(建议配置下) |
| initialSize                   | 0                  | 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时 |
| maxActive                     | 8                  | 最大连接池数量                                               |
| maxIdle                       | 8                  | 已经不再使用，配置了也没效果                                 |
| minIdle                       |                    | 最小连接池数量                                               |
| maxWait                       |                    | 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。 |
| poolPreparedStatements        | false              | 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。 |
| maxOpenPreparedStatements     | -1                 | 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100 |
| validationQuery               |                    | 用来检测连接是否有效的sql，要求是一个查询语句。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用。 |
| testOnBorrow                  | true               | 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 |
| testOnReturn                  | false              | 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能 |
| testWhileIdle                 | false              | 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 |
| timeBetweenEvictionRunsMillis |                    | 有两个含义：  1) Destroy线程会检测连接的间隔时间2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明 |
| numTestsPerEvictionRun        |                    | 不再使用，一个DruidDataSource只支持一个EvictionRun           |
| minEvictableIdleTimeMillis    |                    |                                                              |
| connectionInitSqls            |                    | 物理连接初始化的时候执行的sql                                |
| exceptionSorter               | 根据dbType自动识别 | 当数据库抛出一些不可恢复的异常时，抛弃连接                   |
| filters                       |                    | 属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：  监控统计用的filter:stat日志用的filter:log4j防御sql注入的filter:wall |
| proxyFilters                  |                    | 类型是List<com.alibaba.druid.filter.Filter>，如果同时配置了filters和proxyFilters，是组合关系，并非替换关系 |

## 7.2 整合

导入依赖：

```xml
<!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.22</version>
</dependency>

```

**配置数据源**

````yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ssmbuild?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true
    password: MySQLadmin
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    type: com.alibaba.druid.pool.DruidDataSource
#通过type指定数据源,
#这是一些简单的配置，另外一些Druid的专属的配置，可以参照DruidDataSource源码进行配置
````

测试数据源

````java
@Test
public void DataSourceTest() throws SQLException {
    //查看默认的数据源
    System.out.println(dataSource.getClass());
    Connection connection = dataSource.getConnection();
    System.out.println(connection);
    connection.close();
}
/*
class com.alibaba.druid.pool.DruidDataSource
com.mysql.cj.jdbc.ConnectionImpl@3ae81b53
*/
````

## 7.3 Druid的配置以及监控

**详解：https://www.cnblogs.com/Dm920/p/12621144.html**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mybatis?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true
    password: MySQLadmin
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    type: com.alibaba.druid.pool.DruidDataSource
      #=== 连接池配置 ===#
    initialSize: 5    # 初始化连接个数
    minIdle: 5        # 最小空闲连接个数
    maxActive: 20     # 最大连接个数
    maxWait: 60000    # 获取连接时最大等待时间，单位毫秒。
    timeBetweenEvictionRunsMillis: 60000   # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
    minEvictableIdleTimeMillis: 300000     # 配置一个连接在池中最小生存的时间，单位是毫秒
    validationQuery: SELECT 1 FROM DUAL    # 用来检测连接是否有效的sql，要求是一个查询语句。
    testWhileIdle: true       # 建议配置为true，不影响性能，并且保证安全性。如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
    testOnBorrow: false       # 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
    testOnReturn: false       # 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
    poolPreparedStatements: true    #是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。
    maxPoolPreparedStatementPerConnectionSize: 20     #要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100
    filters: stat,wall,log4j        # 配置监控统计拦截的filters，stat:监控统计、log4j：日志记录、wall：防御sql注入
    useGlobalDataSourceStat: true   # 合并多个DruidDataSource的监控数据
    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500     # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
```

**编写Druid的配置类：**

```java
@Configuration
public class DruidConfig {

    //绑定配置文件
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    @Bean
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    //后台监控
    @Bean
    public ServletRegistrationBean statViewServlet() {
        //访问/druid/*路径就会进入到后台相关页面
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        //后台需要有人登录，账号密码配置
        HashMap<String, String> initParameters = new HashMap<>();//参照源码
        //增加配置
        initParameters.put("loginUsername","admin");//loginUsername和 loginPassword是固定参数，不能更改
        initParameters.put("loginPassword","admin");
        //允许谁可以访问
        initParameters.put("allow","");//value为空表示所有人都可以访问
        //禁止用户访问  initParameters.put("admins","192.168.0.101");

        bean.setInitParameters(initParameters);//设置初始化参数
        return bean;
    }
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }
}
```

当请求/druid/**时，就会进入到后台监控页面（这个页面是druid自带的）：

![](.\image\Druid后台监控.png)

![](.\image\Druid-SQL监控.png)

# 8.整合MyBatis

## 8.1 简介

在整合Spring与MyBatis的时候我们用了一个整合包mybatis-spring

那么在整合SpringBoot的时候，也需要一个整合包：``mybatis-spring-boot-starter``

```xml
<!-- https://mvnrepository.com/artifact/org.mybatis.spring.boot/mybatis-spring-boot-starter -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.2</version>
</dependency>

```

## 8.2 整合

- 创建项目的时候勾选JDBC和数据库的连接驱动
- 导入相关的依赖

### 扫描Mapper的问题

有两种方式标注一个Mapper接口：

- 在Mapper接口上标注``@Mapper``注解，表示这是一个Mapper接口
- 在SpringBoot启动类上使用注解``@MapperScan("com.mapper")``扫描mapper包下的所有的接口

### application.yaml中整合MyBaits

```yaml
#SpringBoot整合MyBatis
mybatis:
  type-aliases-package: com.pojo
  mapper-locations: classpath:mybatis/mapper/*.xml
#可以参照源码进行配置
```



## Mapper层

**UserMapper接口**

```java
@Mapper
@Repository
public interface UserMapper {

    List<User> queryUserList();

    User queryUserById(int id);

    int addUser(User user);

    int updateUser(User user);

    int deleteUser(int id);
}

```

**UserMapper.xml**

````xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mapper.UserMapper">
    <!--在application.yaml中配置了别名，这里的resultType就不用写全类名了-->
    <select id="queryUserList" resultType="com.pojo.User">
        select * from user;
    </select>
    <select id="queryUserById" resultType="com.pojo.User" parameterType="int">
        select * from user where id = #{id};
    </select>
    <update id="updateUser" parameterType="com.pojo.User">
        update user set name= #{name} ,pwd=#{pwd} where id =#{id}
    </update>
    <insert id="addUser" parameterType="com.pojo.User">
        insert into user(id,name,pwd) values (#{id},#{name},#{pwd})
    </insert>
    <delete id="deleteUser">
        delete from user where id=#{id}
    </delete>
</mapper>
````

## Controller层

为了省事，这次的demo没u偶写service层，而是直接controller层调用Mapper层

````java
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
````





