package com.utils;

import com.config.RedisConfig;
import io.lettuce.core.TimeoutOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;
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
