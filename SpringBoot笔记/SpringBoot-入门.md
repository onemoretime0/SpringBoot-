# 1.微服务

## 1.1 什么是微服务

微服务是一种架构风格，他要求我们在开发一个应用程序的时候，这个应用程序必须构建陈给一系列小服务的组合。

可以通过HTTP的方式进行交互。

## 1.2 单体应用构架

所谓的单体应用构架（all in one）是指，我们将一个应用程序中的所有应用服务都封装在一个应用中。

无论是ERP、CRM或是其他什么系统，都把数据访问、web访问等等功能放在一个war包内

- 这样做的好处是已于开发和测试，也十分方便部署；当需要扩展时，只需要将war复制多份，然后放在多个服务器上，在做负载均衡就可以了
- 单体应用构架的缺点是：哪怕只需要修改一个非常小的地方，都需要停止整个服务，重新打包，然后重新部署这个war包。特别是对一个大型应用，我们不可能将所有的内容都放在一个应用里面，如何维护如何分工都成问题



## 1.3 微服务架构

所谓微服务架构就是打破之前的all in one 的构架方式，把每个功能单元独立出来。把独立出来功能元素动态的组合，需要的功能元素才拿来组合。所以微服务架构是对功能元素进行复制，而没有对整个应用进行赋值

这样做的好处是：

- 节省了调用资源
- 每个功能元素的服务都是一个可替换的、可独立升级的软件代码

## 1.4 如何构建微服务

一个大型系统微服务构架，就像一个复杂交织的神经网络，每一个神经元就是一个功能元素，它们各自完成自己的功能，然后通过HTTP互相请求调用。

比如一个电商系统，查缓存、连数据库、浏览页面、结账等等都是一个独立的功能服务，都被微化了，它们作为一个微服务共同构建了一个庞大的系统。如果修改其中的一个功能，只需要更新升级其中的一个功能单元即可

spring为我们提供了构建大型分布式微服务的全套、全程产品：

- 构建一个个功能独立的微服务应用单元，可以使用spring boot，可以帮助我们快速构建一个应用
- 大型分布式网络服务的调用，这部分由spring cloud来完成，实现分布式
- 在分布式中间，进行流式数据计算、批处理，spring cloud data flow

# 2.SpringBoot——HelloWorld

## 2.1 SpringBoot快速生成

网站：https://start.spring.io/

![](.\image\官网创建SpringBoot.png)

==**不推荐**==，一般开发直接在IDEA中直接创建

## 2.2IDEA创建SpringBoot项目

在创建的时候勾选web选项，不勾选则springboot项目只是一个基本的项目

![](.\image\IDEA创建SpringBoot.png)

## 2.3 一个基础的SpringBoot项目的结构

![](.\image\SpringBoot的项目结构.png)

**所有的包都需要在SpringBoot启动入口的同级目录下创建**

## 2.4 彩蛋

SpringBoot启动项目时的banner可以自定义

![](.\image\banner.png)

这部分可以换成自己的

- http://www.network-science.de/ascii/ （将文字转成文本文件）
	http://www.degraeve.com/img2txt.php （将图片转成文本文件）
- http://www.network-science.de/ascii/

制作步骤：

- 将制作好的字符复制到banner.txt文件中（这个文件要在resource文件夹下创建）
- 重启服务

# 3.SpringBoot自动装配原理



## 3.1 pom.xml

- spirng-boot-dependencies：核心依赖在父工程中
- 我们在写入或者引入一些spirngboot依赖的时候，不需要指定版本，因为在父工程中有这些依赖的版本仓库



## 3.2 启动器



启动类列表：https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/using-boot-build-systems.html#using-boot-starter

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
```

**启动器说白了就是springboot的启动场景**

比如，这个依赖会自动帮我们导入web环境所需要的依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**springBoot会将所有的功能场景变成一个个的启动器**

我们要使用哪些功能，只需要找到对应的启动即可starter



## 3.3 主程序

```java
@SpringBootApplication //标注这个类是一个SpringBoot应用，并且启动类下所有的资源都被导入
public class Springboot01HelloworldApplication {
    public static void main(String[] args) {
        //将SpringBoot应用启动
        SpringApplication.run(Springboot01HelloworldApplication.class, args);
    }
}
```

- **@SpringBootApplication：**标注这个类是一个SpringBoot应用
- ``SpringApplication.run(Springboot01HelloworldApplication.class, args);``将SpringBoot应用启动

### 3.3.1 @SpringBootApplication注解

**@SpringBootApplication：**标注这个类是一个SpringBoot应用

这个注解是一个组合注解，源码：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {}
```

其中两个重要的注解：

- **@SpringBootConfiguration：SpringBoot的配置**
- **@EnableAutoConfiguration：自动配置**

#### @SpringBootConfiguration：SpringBoot的配置

![](.\image\@SpringBootConfiguration.png)

源码：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration  //Spring配置类，说明这个启动类也是一个配置类
public @interface SpringBootConfiguration {}
```

@Configuration  //Spring配置类，说明这个启动类也是一个配置类

再深究@Configuration  的源码：

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {}
```

看到熟悉的@Component，说明这也是一个Spring的组件

#### @EnableAutoConfiguration：自动配置



源码：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage //自动配置包
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {}
```

看到

- **@AutoConfigurationPackage 注解，这个注解的作用是自动配置包**
- **@Import(AutoConfigurationImportSelector.class)：自动配置导入选择**



再深究源码**@AutoConfigurationPackage** 以及**AutoConfigurationImportSelector**的源码的源码：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(AutoConfigurationPackages.Registrar.class)//自动配置’包注册‘
public @interface AutoConfigurationPackage {
}
```

**AutoConfigurationImportSelector**源码中getAutoConfigurationEntry()方法中有行代码：

```java
List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
//获取所有配置
//再看getCandidateConfigurations的源码：
	protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
		List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
				getBeanClassLoader());
		Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
				+ "are using a custom packaging, make sure that file is correct.");
		return configurations;
	}
//再看getSpringFactoriesLoaderFactoryClass的源码：
protected Class<?> getSpringFactoriesLoaderFactoryClass() {
    return EnableAutoConfiguration.class;
}
/*
 return EnableAutoConfiguration.class;
 EnableAutoConfiguration这个注解标注的谁呢：SpringBootApplication
 所以，至此：@SpringBootApplication的作用已经清楚了：
 	@SpringBootApplication的作用就是，标注的类是一个SpringBoot的应用，并且启动类下所有的资源都被导入
*/
```

getCandidateConfigurations的方法源码中还有一句话："No auto configuration classes found in META-INF/spring.factories. If you are using a custom packaging, make sure that file is correct."

#### spring.factories文件

**META-INF/spring.factories这个文件是自动配置的核心文件**。这个文件的位置在autoconfigure依赖在的META-INF文件夹下：

![](.\image\spring.factoties.png)

所有的配置类都在此文件中。

![](.\image\spring.factoties文件.png)

思考：既然所有的配置类都在此文件中，为什么有的配置没有生效，需要导入对应的starter才能有用？

- 核心注解@ConditionalOnXXX，如果这里面的条件都满足才会生效

####  结论

- SpringBoot所有的自动配置都是在启动的时候扫描并加载，
- 所有的自动配置类都在spring.factories文件中
	- 但是不一定生效，要判断条件是否成立
	- 只要导入对应的starter，就有了对应的启动器，有了启动器，自动装配就会生效
	- 然后就配置成功了



1. SpingBoot在启动的时候，在类路径下``META-INF/spring.factories``获取指定的值
2. 将这些自动配置的类导入容器，自动配置类就会生效，帮我们自动配置
3. 以前我们需要配置的东西，现在SpringBoot帮我们做了
4. 整个JavaEE，解决方案和自动配置的东西都在``spring-boot-autoconfigure-2.2.6.RELEASE.jar``这个包下
	- 它会把所有的需要导入的组件以类名的方式返回，这些组件就会被添加到容器中
5. 容器中也会存在非常多的XXXAutoConfiguration（@Bean）的类，就是这些类给容器中导入了这个场景所需要的所有组件
6. 有了自动配置类，免去了我们手动编写配置文件的工作

![](.\image\@SpringBootApplication注解.png)

### 3.3.2主启动类怎么运行

```java
@SpringBootApplication //标注这个类是一个SpringBoot应用，并且启动类下所有的资源都被导入
public class Springboot01HelloworldApplication {
    public static void main(String[] args) {
        //将SpringBoot应用启动
        SpringApplication.run(Springboot01HelloworldApplication.class, args);
        //参数一：Springboot01HelloworldApplication.class：应用入口的类
        //参数二：args：命令行参数
    }
}
```

@SpringBootApplicaiton注解上面已经讲完了，现在来看启动代码：

```java
//将SpringBoot应用启动
SpringApplication.run(Springboot01HelloworldApplication.class, args);
```

**运行main方法，开启了一个服务。**

**SpringApplication.run的分析：**

- 一部分是SpringApplication的实例化
- 二是run方法的执行

#### SpringApplication

这个类主要做了四件事情

- 推断应用类型是普通项目还是web项目
- 查找并加载所有可用初始化器，设置到initializers属性中
- 找出所有的应用程序监听器，设置到listeners属性中
- 推断并设置main方法的定义类，找出运行的主类

Springboot01HelloworldApplication.class：其中Springboot01HelloworldApplication是SpringBoot帮我们自动生成的，这个类是可以自己写的。



## 3.4面试：关于SpringBoot，谈谈你的理解

- 自动装配
- run方法：
	- 推断应用类型是普通项目还是web项目
	- 查找并加载所有可用初始化器，设置到initializers属性中
	- 找出所有的应用程序监听器，设置到listeners属性中
	- 推断并设置main方法的定义类，找出运行的主类



# 4.配置文件



## 4.1 SpringBoot配置文件

官方不推荐使用application.properties文件作为SpringBoot的配置文件，所以新建SpringBoot项目之后可以将aplication.properties文件删除，另外新建application.yaml文件作为SpringBoot项目的配置文件。

SpringBoot使用一个全局的配置文件，配置文件的名称是固定的：

- application.properties
	- 语法结构：key=value
- application.yaml
	- 语法结构：key:空格value

**配置文件的作用：**修改SpringBoot自动配置的默认值，因为SpringBoot在底层都给我们自动配置好了



## 4.2 YAML

*YAML*是"YAML Ain't a Markup Language"（YAML不是一种[标记语言](https://baike.baidu.com/item/标记语言)）的[递归缩写](https://baike.baidu.com/item/递归缩写)。在开发的这种语言时，*YAML* 的意思其实是："Yet Another Markup Language"（仍是一种[标记语言](https://baike.baidu.com/item/标记语言)），但为了强调这种语言以数据做为中心，而不是以标记语言为重点，而用反向缩略语重命名。

**标记语言：**

以前的配置文件，大多数都是以xml配配置的，比如一个简单的端口配置：

yaml:

```yaml
server:
  port: 80
```

xml:

```xml
<server>
	<port>80</port>
</server>
```

## 4.3 yaml的基本语法

基本语法：

```yaml
key: value #冒号后面要有空格
```

properties只能保存键值对，而yaml可以保存数组和对象：

```yaml
# 普通的key-value
name: zhangsan

#对象
student:
  name: zhangsan
  age: 12

#对象的行内写法：
person: {name: zhangsan,age: 12}

#数组
array:
  - 11
  - 12
  - 13
array_other: [11,12,13]
array_string: [dog,cat,pig]

```

## 4.4 yaml给类属性赋值

**通过这种方式可以给SpringBoot配置类赋值**

yaml可以直接给实体类赋值。

实体类：

```java
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private String name;
    private Integer age;
    private Boolean happy;
    private Date birthday;
    private Map<String,Object> maps;
    private List<Object> lists;
    private Dog dog;
}

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dog {
    private String name;
    private Integer age;
}
```

在application.yaml中配置实体类：

```yaml
person:
  name: 法外狂徒张三
  age: 24
  happy: false
  birthday: 1995/09/12
  maps: {k1: v1,k2: v2}
  lists:
    - code
    - girl
  dog:
    name: 旺财
    age: 3

```

通过在实体类上使用@ConfigurationProperties注解将实体类与配置文件关联起来：

```java
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
/*
@ConfigurationProperties注解的作用：
	将配置文件中的每一个属性的值，映射到这个组件中；
	告知SpringBoot将本类所有的属性和配置文件中相关的配置进行绑定
	参数prefix = "person":将配置文件中的person下面所有的属性一一对应
	
	只有这个组件是容器的组件，才能使容器提供@ConfigurationProperties功能

*/
@ConfigurationProperties(prefix = "person")//通过这个注解将实体类与配置文件绑定起来
public class Person {
    private String name;
    private Integer age;
    private Boolean happy;
    private Date birthday;
    private Map<String,Object> maps;
    private List<Object> lists;
    private Dog dog;
}
```

测试:

```java
@SpringBootTest
class Springboot02ConfigApplicationTests {

    @Autowired
    private Person person;
    @Test
    void contextLoads() {
        System.out.println(person.toString());
    }
}
/*
结果：
Person(name=法外狂徒张三, age=24, happy=false, birthday=Tue Sep 12 00:00:00 CST 1995, maps={k1=v1, k2=v2}, lists=[code, girl], dog=Dog(name=旺财, age=3))
*/
```

**注意：**在使用@ConfigurationProperties注解的时候会爆红，但是不影响使用，官网推荐添加一下以来解决爆红问题：

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-configuration-processor</artifactId>
	<optional>true</optional>
</dependency>
```

## 4.5 使用properties给类属性赋值

javaConfig绑定我们配置文件的值，可以使用这种方式 

在使用properties给属性赋值之前，请修改properties文件的编码：

![](.\image\properties-指定Encoding.png)

那么类就不需要使用@ConfigurationProperties注解标注

```java

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
//加载指定的配置文件
@PropertySource(value="classpath:application.properties")
public class Person {
    //使用这种方式就需要使用@Value注解一个一个的进行赋值
    @Value("${name}")
    private String name;
    private Integer age;
    private Boolean happy;
    private Date birthday;
    private Map<String,Object> maps;
    private List<Object> lists;
    private Dog dog;
}
```

properties配置文件：

```properties
name=法外狂徒张三
```

**测试：**

```java
@SpringBootTest
class Springboot02ConfigApplicationTests {

    @Autowired
    private Person person;
    @Test
    void contextLoads() {
        System.out.println("Person.Name="+person.getName());
    }
}
/*
结果：Person.Name=法外狂徒张三
*/
```

## 4.6 properties配置与yaml配置的对比

|                      | @ConifgurationProperties      | @Value                        |
| -------------------- | ----------------------------- | ----------------------------- |
| 功能                 | 批量注入配置文件中的属性      | 一个个指定                    |
| 松散绑定（松散语法） | :heavy_check_mark:            | :negative_squared_cross_mark: |
| SpEL表达式           | :negative_squared_cross_mark: | :heavy_check_mark:            |
| JSR303数据校验       | :heavy_check_mark:            | :negative_squared_cross_mark: |
| 复杂类型封装         | :heavy_check_mark:            | :negative_squared_cross_mark: |

**松散绑定:**比如我们在yaml中写的是last-name，而在类中的属性是lastName。last-name和lastName是一样的，"-"后面跟着的字母蓦然是大写的。这就是松散绑定

**JSR303数据校验：**这个就是我们可以在字段增加一层过滤器验证，可以保证数据的合法性

**l结论：**

- 配置yaml和配置properties都可以获取到值，推荐使用yaml
- 如果我们在某个业务中，只需要获取配置文件中的某个值，可以使用@Value
- 如果说，我们专门编写了一个JavaBean来和配置文件进行映射，就直接使用@ConfigurationProperties



# 5. JSR303数据校验

在SpringBoot中可以使用@Validated来校验数据，如果数据异常则会同意抛出异常，方便异常中心统一处理。

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "user")
@Validated//数据校验
public class User {
    private String userName;
    private String password;
    @Email
    private String email;
}

```

```yaml
user:
  user-name: 水濑祈
  password: admin
  email: admins
```

在User类中开启了邮箱验证，但是在配置文件中没有配置标准的邮箱格式，那么启动就会抛出异常：

```tex
  Property: user.email
    Value: admins
    Origin: class path resource [application.yaml]:17:10
    Reason: 不是一个合法的电子邮件地址

Caused by: org.springframework.boot.context.properties.ConfigurationPropertiesBindException: Error creating bean with name 'user': Could not bind properties to 'User' : prefix=user, ignoreInvalidFields=false, ignoreUnknownFields=true; nested exception is org.springframework.boot.context.properties.bind.BindException: Failed to bind properties under 'user' to com.hnl.pojo.User
```

![](.\image\JSR303注解.png)

  **Hibernate Validator 附加的 constraint**

![](.\image\JSR303校验.png)

Validated的位置(包下定义了很多的注解)：

![](.\image\Validation包的位置.png)

# 6.多环境配置以及配置文件的位置



## 6.1配置文件的位置以及优先级

SpringBoot配置文件可以在那些地方创建：

- ``file:./config/``：项目根目录:/config/
	- 优先级最高
- ``file:./``：项目根目录/
	- 次优先级
- ``classpath:/config/``：类目录下（resource或者java）/config/
	- 第三优先级
- ``classpath:/``：类目录下（resource或者java）/
	- 优先级最低（此文件是springboot自动帮我们创建的）

![](.\image\可编写配置文件的位置.png)

## 6.2多环境配置

在具体的项目开发中，有生产环境和测试环境，两种环境用的数据库肯定是不同的，所以多环境的切换是很有必要的

###   6.2.1properties文件实现多环境切换

有三个配置文件：

SpringBoot自动创建好的：

```properties
#springboot的多环境配置：可以选择激活哪一个配置文件
spring.profiles.active=dev
#值 可以只写后缀，因为前缀都是application
```

其他环境：

```properties
#开发环境
server.port=8081
################################################
#测试环境
server.port=8082
```

通过在默认的配置文件手动的指定不同环境的配置文件来实现

### 6.2.2 yaml文件实现多环境切换

使用ymal文件进行配置，一个文件即可搞定，多个环境之间用"---"三条横线隔开

```yaml
server:
  port: 8081

#指定使用哪个配置
spring:
  profiles:
    active: dev
---
#测试环境
server:
  port: 8081
spring:
  profiles: test
---
#开发环境
server:
  port: 8082
spring:
  profiles: dev

```



# 7.编写SpringBoot的配置文件

之前看了SpringBoot是如何完成自动装配的 ，了解到spring.factories文件时SpringBoot的核心配置文件

我们自己编写的application.properties或者application.yaml配置文件与spring.factories有很强的联系。

比如，spring.factories中配置的HttpEncodingAutoConfiguration类：

```java
//表示这是一个配置类，spring.factories文件中的所有的XXXAutoConfiguration类都有此注解
@Configuration(proxyBeanMethods = false)
//自动配置属性，HttpProperties
@EnableConfigurationProperties(HttpProperties.class)
//ConditionalOnXXX是spring的底层注解：根据不同的条件来判断当前配置或类是否生效
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(CharacterEncodingFilter.class)
@ConditionalOnProperty(prefix = "spring.http.encoding", value = "enabled", matchIfMissing = true)
public class HttpEncodingAutoConfiguration {

	private final HttpProperties.Encoding properties;
	public HttpEncodingAutoConfiguration(HttpProperties properties) {
		this.properties = properties.getEncoding();
	}
```

查看其私有属性HttpProperties的源码：

```java
@ConfigurationProperties(prefix = "spring.http")
//说明我们可以通过配置文件去配置它的值
```

![](F:\OneDrive\Typora文档\SpringBoot\image\自动装配在理解-实例理解.png)

发现，在 配置文件中根据spring.http能配置的东西在HttpProperties类中都有定义

**结论:**

- 在配置文件中能配置的东西，都存在一个固有的规律
- 能配置的东西都定义在一个XXXProperties类中，XXXProperties绑定配置文件
- XXXAutoConfiguration中装配的是默认值，而配置文件可以通过XXXProperties将默认值修改为我们想要的值

spring.factories文件中每一个XXXAutoConfiguration类都是容器中的一个组件，最后加入到容器中，用他们来做自动配置。

一旦这个类生效，这配置类就会给容器中添加各种组件；这些组件的属性是从对应的properties类中获取的，这些类里面的每一个属性又是和配置文件绑定的。

所有的在配置文件中能配置的属性都是在XXXProperties类中封装着，配置文件能配置什么就可以参照某个功能对应的这个属性类

### SpringBoot自动装配的原理：

==**精髓**==

- SpringBoot启动会加载大量的自动配置类
- 我们看我们需要功能有没有在SpringBoot默认写好的自动配置类中
- 再看这个自动配置类中到底配置了那些组件（只要我们需要的组件存在其中，就不需要再手动配置了）
- 给容器中自动配置类添加组件的时候，会从properties类中获取某些属性，我们只需要再配置文件中指定这些属性的值即可

XXXAutoConfiguration：自动配置类

XXXProperties：封装配置文件中相关的属性

### debug=true配置

可以通过此配置来查看，那些自动配置类生效了，那些没有生效

