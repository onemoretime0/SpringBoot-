package com.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.UnknownHostException;

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
    @Bean(name = "myRedisTemplate")
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

    @Bean
    public RedisUtil redisUtil(){
        return new RedisUtil();
    }
}
