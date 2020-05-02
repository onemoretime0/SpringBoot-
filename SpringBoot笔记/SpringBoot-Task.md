# 1.异步任务

## 同步和异步

异步(async)是相对于同步(sync)来说的，简单理解，同步是串行的，异步是并行的。

好比说，A需要从B和C两个节点获取数据

- 第一种方式，A请求B，B返回给A数据，A再去请求C，在从C出获得数据。这种方式就是同步。
- 另一种方式，A去请求B，不等B返回数据，就去请求C，然后等B和C准备好数据再推送给A，A同样可以拿到B和C的数据，这就是异步。

注意，第二种方式B和C是同时处理A的请求的，是比第一种方式效率要高的，但是这种方式，有一个限制，就是从B和C之间要获取的数据不能有依赖关系，假如获取C的数据时候，C需要从B返回来的数据，那就只能采用第一种方式，先请求B，拿到B的数据，在去请求C

## SpringBoot异步任务

SpringBoot完成异步任务只需要两个注解：

- ``@EnableAsync``：在MAIN上标注，用于开启异步任务
- ``@Async``：可以用在异步任务类上，也可以用在异步任务方法上，通知SpringBoot这是一个异步的任务

```java
@Service
public class AsyncService {

    //通知Spring这是一个异步的的方法
    @Async
    public void hello(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("数据正在处理");
    }
}

//Controller调用Service
@RestController
public class AsyncController {

    @Autowired
    private AsyncService service;

    @RequestMapping("/hello")
    public String hello() {
        service.hello();
        return "OK";
    }
}
//开启注解
@EnableAsync
@SpringBootApplication
public class SpringbootTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootTaskApplication.class, args);
    }
}
```

运行项目，然后在浏览器访问``localhost:8080/hello``，浏览器马上就会刷新，而在IDEA的控制台需要等三秒才会打印"数据正在处理"

如果在Service层的hello方法上没有添加``@Async``注解，那么浏览器需要刷新三秒才会显式数据。

## 默认异步任务线程池

SpringBoot异步任务默认使用的线程池为SimpleAsyncTaskExecutor，其特点如下：

- 默认定义多少异步任务，创建多少线程（创建线程数量太多，占用内存过大，会造成OutOfMemoryError）
- SimpleAsyncTaskExecutor不提供拒绝策略机制。
- SimpleAsyncTaskExecutor可通过设置参数concurrencyLimit（值为大于或等于0的整数），指定启用的线程数目；默认concurrencyLimit取值为-1，即不启用资源节流。

## 自定义异步任务线程池

想要自定义异步任务线程池，仅需要实现AsyncConfigurer接口，并实现其中的方法：

````java
@Configuration
public class AsyncThreadPoolConfig implements AsyncConfigurer {

    /**
     * 自定义异步任务线程池
     * 可参考Executor的实现类ThreadPoolTaskExecutor进行配置
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);//初始线程池大小
        threadPoolTaskExecutor.setMaxPoolSize(10000000);//最大线程数量
        threadPoolTaskExecutor.setQueueCapacity(10);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }

    /**
     * 自定义异常处理器，也可不配置，有默认实现
     * @return
     */
   /* @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
         AsyncUncaughtExceptionHandler syncUncaughtExceptionHandler = (ex, method, params) -> ex.printStackTrace();
        return syncUncaughtExceptionHandler;
    }*/
}

````

看打印的信息，线程池已经变为我们自己定义的了

![](.\image\任务\自定义异步任务线程池.png)

# 2.邮件任务



## 配置

首先导入依赖：

````xml
<!--SpringBoot的启动器-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
````

**配置**

只需要在application.properties中进行简单的配置即可使用：

```properties
spring.mail.username=709779916@qq.com
#密码进行了加密操作，QQ邮箱开启POP3默认会加密
spring.mail.password=javgxiefzbpcbcba
spring.mail.host=smtp.qq.com
#qq邮箱可以开启加密验证
spring.mail.properties.mail.smtp.ssl.enable=true
```

## 发送邮件

````java
@SpringBootTest
class SpringbootTaskApplicationTests {

    @Autowired
    JavaMailSenderImpl mailSender;

    @Test
    void simpleMail() {

        //一个简单的邮件
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setSubject("Hello");//主题
        mailMessage.setText("这是在SpringBoot中使用JavaMailSenderImpl对象发送的邮件");//内容
        mailMessage.setTo("709779916@qq.com");
        mailMessage.setFrom("709779916@qq.com");

        mailSender.send(mailMessage);
    }

    @Test
    void complexMail() throws MessagingException {

        //一个复杂邮件,MimeMessage复杂邮件
        MimeMessage message = mailSender.createMimeMessage();
        //组装邮件
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,true,"utf-8");
        //利用MimeMessageHelper对象进行组装
        mimeMessageHelper.setSubject("Hello,complexMail");
        mimeMessageHelper.setText("<h1>复杂邮件可以写HTML</h1>+" +
                "<hr>+" +
                "<p style='color:red'>这是在SpringBoot中使用JavaMailSenderImpl对象发送的复杂邮件</p>",true);
        //附件
        mimeMessageHelper.addAttachment("加藤惠.png",new File("F:\\OneDrive\\壁纸\\路人女主\\加藤惠.png"));
        mimeMessageHelper.addAttachment("雪乃.png",new File("F:\\OneDrive\\壁纸\\春物\\雪乃.png"));

        mimeMessageHelper.setTo("709779916@qq.com");
        mimeMessageHelper.setFrom("709779916@qq.com");

        mailSender.send(message);
    }


}

````

简单邮件：

![](.\image\任务\简单邮件.png)

复杂邮件：

![](.\image\任务\复杂邮件.png)





# 3.定时执行任务



## 介绍

**定时任务的两个核心接口是：**

- TaskExecutor（函数式接口）：任务执行者
- TaskScheduler：任务调度者

````java
@FunctionalInterface
public interface TaskExecutor extends Executor {
	@Override
	void execute(Runnable task);
}
````

**两个核心注解：**

- ``@EnableScheduling``：开启定时功能的注解
- ``@Scheduled``：什么时候执行



## cron表达式

Cron表达式是一个字符串，字符串以5或6个空格隔开，分为6或7个域，每一个域代表一个含义，Cron有如下两种语法格式：

- Seconds Minutes Hours DayofMonth Month DayofWeek Year
- Seconds Minutes Hours DayofMonth Month DayofWeek

### 结构

**corn从左到右（用空格隔开）：秒 分 小时 月份中的日期 月份 星期中的日期 年份**

### 各字段的含义

| 字段                     | 允许值                                 | 允许的特殊字符             |
| ------------------------ | -------------------------------------- | -------------------------- |
| 秒（Seconds）            | 0~59的整数                             | , - * /   四个字符         |
| 分（*Minutes*）          | 0~59的整数                             | , - * /   四个字符         |
| 小时（*Hours*）          | 0~23的整数                             | , - * /   四个字符         |
| 日期（*DayofMonth*）     | 1~31的整数（但是你需要考虑你月的天数） | ,- * ? / L W C   八个字符  |
| 月份（*Month*）          | 1~12的整数或者 JAN-DEC                 | , - * /   四个字符         |
| 星期（*DayofWeek*）      | 1~7的整数或者 SUN-SAT （1=SUN）        | , - * ? / L C #   八个字符 |
| 年(可选，留空)（*Year*） | 1970~2099                              | , - * /   四个字符         |

**注意事项：**每一个域都使用数字，但还可以出现如下特殊字符，它们的含义是：

- ``*``：表示匹配该域的任意值。假如在Minutes域使用*, 即表示每分钟都会触发事件。
- ``?``：只能用在DayofMonth和DayofWeek两个域。它也匹配域的任意值，但实际不会。因为DayofMonth和DayofWeek会相互影响。例如想在每月的20日触发调度，不管20日到底是星期几，则只能使用如下写法： 13 13 15 20 * ?, 其中最后一位只能用？，而不能使用*，如果使用*表示不管星期几都会触发，实际上并不是这样。
- ``-``：表示范围。例如在Minutes域使用5-20，表示从5分到20分钟每分钟触发一次 
- ``/``：表示起始时间开始触发，然后每隔固定时间触发一次。例如在Minutes域使用5/20,则意味着5分钟触发一次，而25，45等分别触发一次. 
- ``,``：表示列出枚举值。例如：在Minutes域使用5,20，则意味着在5和20分每分钟触发一次。 
- ``L``：表示最后，只能出现在DayofWeek和DayofMonth域。如果在DayofWeek域使用5L,意味着在最后的一个星期四触发。
- ``W``：表示有效工作日(周一到周五),只能出现在DayofMonth域，系统将在离指定日期的最近的有效工作日触发事件。例如：在 DayofMonth使用5W，如果5日是星期六，则将在最近的工作日：星期五，即4日触发。如果5日是星期天，则在6日(周一)触发；如果5日在星期一到星期五中的一天，则就在5日触发。另外一点，W的最近寻找不会跨过月份 。
- ``LW``：这两个字符可以连用，表示在某个月最后一个工作日，即最后一个星期五
- ``#``：用于确定每个月第几个星期几，只能出现在DayofMonth域。例如在4#2，表示某月的第二个星期三。

### 常用表达式例子

- **0 0 2 1 \* ? \***  表示在每月的1日的凌晨2点调整任务
- **0 15 10 ? \* MON-FRI**  表示周一到周五每天上午10:15执行作业
- **0 15 10 ? 6L 2002-2006**  表示2002-2006年的每个月的最后一个星期五上午10:15执行作
- **0 0 10,14,16 \* \* ?**  每天上午10点，下午2点，4点 
- **0 0/30 9-17 \* \* ?**  朝九晚五工作时间内每半小时 
- **0 0 12 ? \* WED**   表示每个星期三中午12点 
- **0 0 12 \* \* ?**  每天中午12点触发 
- **0 15 10 ? \* \***   每天上午10:15触发 
- **0 15 10 \* \* ?**   每天上午10:15触发 
- **0 15 10 \* \* ? \***   每天上午10:15触发 
- **0 15 10 \* \* ? 2005**   2005年的每天上午10:15触发 
- **0 \* 14 \* \* ?**   在每天下午2点到下午2:59期间的每1分钟触发 

## 执行定时任务

熟悉了cron表达式之后，在SoringBoot中执行定时任务还是很容易的。

首先在SpringBoot启动类上添加一个注解：`@EnableScheduling`

然后编写需要执行的并在方法上面添加`@Scheduled`注解，并传入一个cron表达式

````java
//特定的事件执行，cron表达式
@Scheduled(cron = "0/2  * * * * ?")  //传入一个cron表达式,每两秒执行一次
public void hello(){
    System.out.println("Hello,执行了");
}
````

