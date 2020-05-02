# Swagger介绍

前后端分离产生的问题：

- 在前后端集成联调的时候，前端人员和后端人员无法做到需求的即时协商，一旦无法解决，最终会导致问题集中爆发。

解决方案：

- 首先开发之前，制定一个schame[计划的纲要]，实时更新最新的API，降低集成风险
- 早些年：制定word计划文档
- 前后端分离
	- 前端测好后端接口：postman，可以测试接口能不能访问到
	- 后端提供接口，需要实时更新最新的消息和改动



为了解决这些问题Swagger就诞生了！

Swagger**是什么?**

- Swagger是一款RESTFUL接口的文档在线自动生成+功能测试功能软件。Swagger是一个规范和完整的框架，用于生成、描述、调用和可视化RESTfu风格的web服务。目标是使客户端和文件系统作为服务器一同样的速度来更新文件的方法，参数和模型紧密集成到服务器。这个解释简单点来讲就是说，swagger是一款可以根据restful风格生成的接口开发文档，并且支持做测试的一款中间软件。

- RestFul Api文档在线生成工具，=>Api文档与Api实施更新
- 直接运行，可以在线测试API接口
- 支持多种语言

官网：https://swagger.io/

# SpringBoot-Swagger环境搭建



- 创建SpringBoot-web项目
- 导入springfox-swagger依赖
	- swagger2
	- ui

```xml
<!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger-ui -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
<!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger2 -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>

```

# 使用Swagger

**在SpringBoot中使用Swagger需要编写Swagger的配置类，并将其注入到Spring中，并开启Swagger**

编写Swagger2的配置类，并开启Swagger2

````java
@Configuration
@EnableSwagger2     //开启Swagger2
public class SwaggerConfig {

}
//没错，现在就可以开始用了，
````

**测试运行：**

访问http://localhost/swagger-ui.html

![](.\image\swagger\Swagger-UI.png)

**最简单的配置就是，编写一个配置类并使用@EnableSwagger2注解开启Swagger2，然后注入到Spring中就可以使用了。**

# 配置Swagger信息

Swagger的Bean实例Docket

Cocket对象的创建需要传递一个DocumentationType对象：

````java
//Cocket的构造函数
public Docket(DocumentationType documentationType) {
    this.apiInfo = ApiInfo.DEFAULT;
    this.groupName = "default";
    this.enabled = true;
    this.genericsNamingStrategy = new DefaultGenericTypeNamingStrategy();
    this.applyDefaultResponseMessages = true;
    this.host = "";
    this.pathMapping = Optional.absent();
    this.apiSelector = ApiSelector.DEFAULT;
    this.enableUrlTemplating = false;
    this.vendorExtensions = Lists.newArrayList();
    this.documentationType = documentationType;
}
//DocumentationType源码
public class DocumentationType extends SimplePluginMetadata {
    public static final DocumentationType SWAGGER_12 = new DocumentationType("swagger", "1.2");
    public static final DocumentationType SWAGGER_2 = new DocumentationType("swagger", "2.0");
    public static final DocumentationType SPRING_WEB = new DocumentationType("spring-web", "1.0");
    private final MediaType mediaType;
}
//看到这里我们就知道了，要传递的的对象是哪一个了：SWAGGER_2，而根据Docket的源码我们可以知道我们可以配置那些信息
````

**开始配置：**

````java
//配置了Swagger2的Docket的bean实例
@Bean
public Docket docket(){
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        ;
}

//配置Swagger文档信息===>ApiInfo
public ApiInfo apiInfo(){
    //作者信息
    Contact contact = new Contact("hnl","","huprivatmail@gmail.com");
    return new ApiInfo(
        "hnl-Swagger-API文档",  //对应的就是Swagger-UI页面中的标题
        "Swagger学习文档",  //描述
        "1.0",  //版本
        "urn:tos",  //
        contact,
        "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0",
        new ArrayList<VendorExtension>());
}
````

![](.\image\swagger\配置Swagger信息.png)

# 配置Swagger扫描接口

如果没有配置Swagger扫描接口，那么默认是全部扫描的。

配置扫描接口的方法是：``Docket.select()``

Swagger的配置都是基于Docket的，并且可以链式编程：

```java
//配置了Swagger2的Docket的bean实例
@Bean
public Docket docket(){
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .build()
        ;
}
```

### 配置

扫描接口的配置必须是在select()方法和build()方法之间

```java
  //配置了Swagger2的Docket的bean实例
    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                /*RequestHandlerSelectors：配置要扫描接口的方式,
                    basePackage:指定要扫描的包
                    any()：扫描全部
                    none()：都不扫描
                    withClassAnnotation()：扫描某个注解标注的类，参数为注解的Class对象
                    withMethodAnnotation()：扫描某个注解注解的函数，参数为注解的Class对象
                    */
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .paths(PathSelectors.any())     //paths()：过滤***路径
                .build()
                ;
    }
```

# 配置启动Swagger

Docket对象中有一个属性：

```java
private boolean enabled = true;
//这个属性就是是否启动Swagger的开关，默认为true，false为关闭
```

**注意：**配置enable属性的时候，要将enable属性配置在select()和build()之外，因为这两个方法是一个组合

````java
//配置了Swagger2的Docket的bean实例
    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(false) //配置Swagger是否启动
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.controller"))
                .build()
                ;
    }
````

我们项目实际开发中，需要开发环境中使用Swagger，而在生产环境中我们希望关闭Swagger，该怎么做？

- 判断是不是生产环境 flag=false
- 注入enable(flag)

SpringBoot项目的多环境配置过呢据配置文件进行

- 生产环境的配置文件，``application-pro.properties``

	- ```properties
		#生产环境
		server.port=8081
		```

- 开发环境的的配置文件：``application-dev.properties``

	- ```properties
		#开发环境
		server.port=8082
		```

- 在``application.properties文件中选择开启那个配置文件``

	- ````properties
		#激活开发环境
		spring.profiles.active=dev
		````

**配置：**

````java
//配置了Swagger2的Docket的bean实例
@Bean
public Docket docket(Environment environment){
    //设置要显示的Swagger环境，dev或test则Swagger开启，其他则不开启
    Profiles profiles=Profiles.of("dev","test");
    //通过environment.acceptsProfiles(profiles)判断是否处在自己设置的环境当中
    boolean flag = environment.acceptsProfiles(profiles);


    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .enable(flag) //配置Swagger是否启动
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.controller"))
        .build()
        ;
}
//将根据项目环境选择是否启动Swagger
````

**测试：**

将环境设置为生产环境``spring.profiles.active=pro``

然后访问``localhost:8081/swagger-ui.html``

![](.\image\swagger\动态根据环境是否开启Swagger.png)

# 配置API文档的分组



配置API文档分组的属性为groupName,Docket源码：

```java
private String groupName = DEFAULT_GROUP_NAME;
//我们只要设置groupName的值，就可以实现API文档分组
```

配置代码非常简单，在docket方法的链式调用中添加一个``.groupName()``即可

````java
.groupName("Hello")//Api文档分组
````

![](.\image\swagger\Swagger-groupName.png)



**思考：如何配置多个分组？**

- 配置Docket实例即可（注入到Bean）
- 分组是根据Docket产生的

```java
@Bean
public Docket docketHnl(){
    return  new Docket(DocumentationType.SWAGGER_2).groupName("hnl");
}
@Bean
public Docket docketZhangSan(){
    return new Docket(DocumentationType.SWAGGER_2).groupName("Zhangsan");
}
```



![](.\image\swagger\每一个Docket实例代表了一个分组.png)



# 实体类配置



如果接口的返回中含有实体类，那么就会被扫描到Swagger中。

**注意：**如果实体类中的属性是private的，那么在Swagger的Model的只会显示实体类的类名，而不会显示具体的属性，如果想要显示具体的属性，则将属性定义为public或者增加setter和getter方法

```java
//只要我们的接口中返回值中存在实体类，就会被扫描到Swagger文档中
@PostMapping("/user")
public User user(){
    return new User();
}
```

还可以给实体类添加注释，使用``@Api``系列注解

- ``@Api``：注释模块（能用的地方有很多，一般使用在实体类上，可以根据源码查看用法）
- ``@ApiModel``：注释实体类的类级注释
- ``@ApiModelProperty``：注释实体类的属性
- ``@ApiOperation``：注释Controller中的方法
- ``@ApiParam``：接口参数
- 

````java
//private的属性需要在setter和getter才可以在Swagger中显示
@ApiModel("用户实体类")
public class User {

    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("密码")
    private String password;
}

//Controller
@RestController
public class HelloController {

    @GetMapping("/hellp")
    public String hello() {
        return "Hello";
    }

    //只要我们的接口中返回值中存在实体类，就会被扫描到Swagger文档中
    @PostMapping("/user")
    public User user() {
        return new User();
    }

    //@ApiOperation:接口，可以注释接口的业务功能等
    @ApiOperation("SayHi,username")
    @PostMapping("/hello/{username}")
    public String hello(@PathVariable("username") @ApiParam("参数是用户名") String username) {
        return "Hello," + username;
    }
}
````

![](.\image\swagger\@Api系列注释.png)

# Swagger的测试功能



Swagger可以测试接口:

![](.\image\swagger\Swagger测试功能.png)

![](.\image\swagger\Swagger测试接口.png)

# 小结



**在项目正式上线之后，一定要关闭Swagger**