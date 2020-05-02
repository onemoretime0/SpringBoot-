# 1.整合SpringBoot

## 创建springboot项目

### 项目说明

SpringBoot与数据相关的都是在SpringData中，包括jdbc的操作整合mybatis，redis页不例外，同样是在springdata中。

创建SpringBoot项目的时候可以勾选Redis也可以创建成之后导入jar包。

![](.\image\整合SpringBoot-创建项目.png)

**pom.xml说明：**

```xml
<!--在SpringBoot中redis的启动器-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!--查看启动启动父依赖-->
<dependency>
    <!--redis在springboot中的继承确实是springdata来操作的-->
      <groupId>org.springframework.data</groupId>
      <artifactId>spring-data-redis</artifactId>
      <version>2.2.6.RELEASE</version>
      <scope>compile</scope>
      <exclusions>
        <exclusion>
          <artifactId>jcl-over-slf4j</artifactId>
          <groupId>org.slf4j</groupId>
        </exclusion>
      </exclusions>
    </dependency>
<!--在此pom文件中没有发现jedis，但是发现了lettuce依赖，在SpringBoot2.0版本之后，SpringBoot中就没有了jedis却而代之的是lettuce用来操作Redis-->
<dependency>
      <groupId>io.lettuce</groupId>
      <artifactId>lettuce-core</artifactId>
      <version>5.2.2.RELEASE</version>
      <scope>compile</scope>
    </dependency>
```

### Jedis 与 lettuce

> 在SpringBoot2.0版本之后，在springboot项目总已经不采用Jedis来操作Redis数据库了，而是采用lettuce来操作Redis数据库

- **Jedis：**采用的是直连，多线个线程操作的话，是不安全的。如果想要避免不安全就要使用Redis Pool连接池（类似于BIO是阻塞的）
- **Lettuce：**底层采用的是netty，实例可以在多个线程中进行共享，不存在线程不安全的情况，可以减少线程数量（更像NIO模式）



## SpringBoot中Redis的配置类源码分析

- `RedisAutoConfiguration`：是Redis的自动配置类
- `RedisProperties`：properties配置类

`RedisAutoConfiguration`类的源码：

```java
/*
	在RedisAutoConfiguration类中配置了一个RedisTemplate模板和stringRedisTemplate模板。
*/
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@Import({ LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class })
public class RedisAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(name = "redisTemplate")//我们可以自己定义一个redisTemplate来替换这个默认的redisTemplate
    /*
    	在RedisTemplate中只配置了默认的Redis,并没有做过多的设置，Redis对象都是需要序列化的
    */
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
        //两个泛型都是Object类型的，使用的时候需要强制类型转换,我们期待的是<String,Object>
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

    //由于string是Redis中最常用的类型，所以单独配置了一个bean
	@Bean
	@ConditionalOnMissingBean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}
}

```

## 开始整合

- 导入依赖
- 配置连接
- 测试



#### 在application中配置Redis

在application中可以配置的选项有很多，可以根据需要进行配置：

```yaml
spring:
    redis:
    # redis数据库索引（默认为0），我们使用索引为3的数据库，避免和其他数据库冲突
    database: 3
    # redis服务器地址（默认为localhost）
    host: localhost
    # redis端口（默认为6379）
    port: 6379
    # redis访问密码（默认为空）
    password:
    # redis连接超时时间（单位为毫秒）
    timeout: 0
    # redis连接池配置
    pool:
    # 最大可用连接数（默认为8，负数表示无限）
    max-active: 8
    # 最大空闲连接数（默认为8，负数表示无限）
    max-idle: 8
    # 最小空闲连接数（默认为0，该值只有为正数才有作用）
    min-idle: 0
    # 从连接池中获取连接最大等待时间（默认为-1，单位为毫秒，负数表示无限）
    max-wait: -1
```

配置连接池的时候要注意，在springboot2.0版本以上需要配置lettuce的连接池而不是jedis的连接池。

#### 通过RedisTemplate操作Redis

在SpringBoot中需要通过RedisTemplate对象操作数据库。

- 对于Redis数据结构的操作可以通过opsForXXX()方法来进行操作
- 一些常用的操作，比如事务可以直接通过RedisTemplate对下来操作

```java
//使用RedisTemplate对象需要先从Spring容器中获取
@Autowired
private RedisTemplate redisTemplate;

//比如通过RedisTemplate对象来操作String类型
redisTemplate.opsForValue().set("name","zhangsan");
redisTemplate.opsForValue().get("name");

//常用操作
redisTemplate.multi();		//开启事务
redisTemplate.watch();		//开启监控

//还可以通过redisTemplate获取连接对象
RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
connection.flushAll();
connection.flushDb();
```

```java
@Test
void contextLoads() {
    redisTemplate.opsForValue().set("name","zhangsan");
    System.out.println(redisTemplate.opsForValue().get("name"));
}
//结果：zhangsan
/*
	但是在redis-cli客户端中查询的结果却是这样的：
		127.0.0.1:6379> keys *
        1) "\xac\xed\x00\x05t\x00\x04name"
        127.0.0.1:6379>
    这是由于对象没有序列化的结果，这需要我们自己来配置RedisTemplate
*/
```

# 2.自定义RedisTemplate

## 存入Redis的对象需要序列化

SpringBoot官方定义的配置都在RedisTemplate中。我们可以定义我们自己的RedisTemplate。

来看RedisTemplate中的某些配置：

```java
//这是关于Redis对象序列化的配置
@SuppressWarnings("rawtypes") private @Nullable RedisSerializer keySerializer = null;
@SuppressWarnings("rawtypes") private @Nullable RedisSerializer valueSerializer = null;
@SuppressWarnings("rawtypes") private @Nullable RedisSerializer hashKeySerializer = null;
@SuppressWarnings("rawtypes") private @Nullable RedisSerializer hashValueSerializer = null;

//再看这些值的赋值方式:
// 发现序列化方式默认是jdk的序列化方式，jdk的序列化方式会使字符串转义，所以就会出上述的结果
defaultSerializer = new JdkSerializationRedisSerializer(
					classLoader != null ? classLoader : this.getClass().getClassLoader());
```

首先来看在对象没有序列化的时候存入Redis会发生什么问题：

```java
//User类
public class User {
    private String name;
    private int age;
}
 @Test
    void test1() throws JsonProcessingException {
        //真实的开发中一般使用json来传递对象
        User user = new User("张三", 14);
        String userJson = new ObjectMapper().writeValueAsString(user);//将user转换为一个json字符串
        redisTemplate.opsForValue().set("user", userJson);
        System.out.println(redisTemplate.opsForValue().get("user"));
    }
/*
	结果：{"name":"张三","age":14}，结果是正确的输出了
*/

//那如果是直接向set方法传入一个对象呢：
//Test
    @Test
    void test1() throws JsonProcessingException {
        User user = new User("张三", 14);
        redisTemplate.opsForValue().set("user",user);
        System.out.println(redisTemplate.opsForValue().get("user"));
    }
/*
	报错：报了一个SerializationException异常，没有对对象序列化引起的异常
	下面将User类序列化之后再次操作,User实现Serializable接口实现序列化
*/

//在企业级开发中，所有的pojo类都需要序列化
public class User implements Serializable {
    private String name;
    private int age;
}
//再次执行上面的代码，结果：User(name=张三, age=14)，成功输出

/*
	虽然是在IDEA的控制台中成功的输出了，但是在Redis的客户端中我们查看的是依然出现了字符串转义的问题127.0.0.1:6379> keys *
1) "\xac\xed\x00\x05t\x00\x04user"
127.0.0.1:6379>

这还是因为SpringBoot配置的默认序列化方式是JDK的序列化方式，我们需要配置自己的序列化方式，下面开始吧
		|	|
		|	|
	   -	 -
		\	/
		  V
*/
```





## 自定义RedisTemplate

**自定义对象序列化方式**

自定义序列化方式非常简单

```java
//这是关于Redis对象序列化的配置
@SuppressWarnings("rawtypes") private @Nullable RedisSerializer keySerializer = null;
@SuppressWarnings("rawtypes") private @Nullable RedisSerializer valueSerializer = null;
@SuppressWarnings("rawtypes") private @Nullable RedisSerializer hashKeySerializer = null;
@SuppressWarnings("rawtypes") private @Nullable RedisSerializer hashValueSerializer = null;
```

使用RedisTemplate对象set对象的方法即可，例如：

```java
//使用Json的序列化方式 
Jackson2JsonRedisSerializer<Object> JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>();
template.setKeySerializer(JsonRedisSerializer);
//set方法需要传递一个RedisSerializer接口的实现类对象，每一个实现类都是一助攻序列化方式
```

RedisSerializer的实现类：

![](.\image\RedisSerializer接口的实现类.png)

对于序列化的配置原理都是这样的，可以根据需要来进行配置

**具体的配置编写(可以作为一个模板)**

````java
@Configuration
public class RedisConfig {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 配置自己的RedisTemplate
     *
     * @param redisConnectionFactory
     * @return
     * @throws UnknownHostException
     */
    @Bean(name="myRedisTemplate") //为了不会引起歧义
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {

        //为了开发方便，一般直接使用泛型直接使用<String,Object>
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        //序列化配置
        //使用JSON解析任意的Object对象
        Jackson2JsonRedisSerializer<Object> JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        //使用ObjectMapper进行转义
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        JsonRedisSerializer.setObjectMapper(objectMapper);

        //key采用String序列化方式
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        //Hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        //Value的序列化方式采用Json
        template.setValueSerializer(JsonRedisSerializer);
        //Hash的Value也采用Json的序列化方式
        template.setHashValueSerializer(JsonRedisSerializer);

        template.afterPropertiesSet();

        return template;
    }
}

````

**测试：**

````java
@Autowired
@Qualifier("myRedisTemplate")
private RedisTemplate redisTemplate;

@Test
void test1() throws JsonProcessingException {
    User user = new User("张三", 14);
    redisTemplate.opsForValue().set("user", user);
    System.out.println(redisTemplate.opsForValue().get("user"));
}

/*
	127.0.0.1:6379> keys *
    1) "user"
    127.0.0.1:6379> get user
    key经过我们的配置类的序列化，已经没有了转义字符

*/
````

# 3.编写Redis的工具类

在企业中我们并不会用这么原生的方式去编写我们的代码操作Redis，我们需要一个工具类来进行配置Redis-RedisUtils，就像是JDBCUtils那样。

使用RedisUtils是我们的开发更加的简单，代码量更少！（如果有脑残公司以代码量衡量KPI，请马上辞职）

在这里只列举了一部分工具类的代码，具体的可以根据这部分代码编写完成的工具类。

这个工具类其实就是对操作Redis的方法进行了一次封装，是代码量变少而已！

编写完这个工具类，需要在RedisConfig配置类中将其注入的Spring容器。

````java
@Bean
public RedisUtil redisUtil(){
    return new RedisUtil();
}
````



````java
//在我们真实的开发中，或者企业开发中，都可以看到这样的工具类，用于对Redis操作的封装
@Component
public final class RedisUtil {
    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 指定缓存过期时间
     *
     * @param key  键
     * @param time 时间（秒）
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据Key获取过期时间
     *
     * @param key 键
     * @return 返回过期时间，返回0代表永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除一个或多个key的缓存
     *
     * @param key 可以删除一个key的缓存，也可以删除多个key的缓存
     */
    public void delCache(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 普通缓存获取，就相当于redis中的get命令
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存存入
     *
     * @param key
     * @param value
     * @return 缓存失败返回false, 成功返回true
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存存入设置时间
     *
     * @param key
     * @param value
     * @param time
     * @return
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增，相当于redis中的inceby命令
     *
     * @param key
     * @param delta 递增因子
     * @return 返回递增后的数据
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减，相当于redis中的decrby命令
     *
     * @param key
     * @param delta 递减因子
     * @return 然会递减之后的数据
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /*
     ********************************************************************************************
     * 对于map的操作
     ********************************************************************************************
     */

    /**
     * Redis中的 HGET 命令
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hget(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * HMGET命令
     *
     * @param key
     * @return
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HMSET命令
     *
     * @param key
     * @param map
     * @return
     */
    public boolean hmset(String key, Map<Object, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HMSET 并设置时间
     * @param key
     * @param map
     * @param time
     * @return
     */
    public boolean hmset(String key, Map<Object, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hset(String key,Object hashKey,Object value){
        try{
            redisTemplate.opsForHash().put(key,hashKey,value);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
````