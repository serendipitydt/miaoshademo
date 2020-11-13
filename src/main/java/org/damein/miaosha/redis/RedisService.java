package org.damein.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取对象
     */

    public <T> T get(keyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;//通过这个redis进行get和set

        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey=prefix.getPrefix()+key;
            String str = jedis.get(realKey);
            T t = stringToBean(str, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象
     */
    public <T> boolean set(keyPrefix prefix, String key, T value) {
        Jedis jedis = null;//通过这个redis进行get和set

        try {
            jedis = jedisPool.getResource();
            String str = beamToString(value);
            if (str == null || str.length() <= 0) {
                return false;
            }
            //生成真正的key
            String realKey=prefix.getPrefix()+key;
            int seconds=prefix.expireSeconds();
            if (seconds<=0){
                jedis.set(realKey, str);
            }else{
                jedis.setex(realKey,seconds,str);
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断key是否存在
     */
    public <T> boolean exists(keyPrefix prefix, String key) {
        Jedis jedis = null;//通过这个redis进行get和set

        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey=prefix.getPrefix()+key;
            return  jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     */
    public <T> Long incr(keyPrefix prefix, String key) {
        Jedis jedis = null;//通过这个redis进行get和set

        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey=prefix.getPrefix()+key;
            return  jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     */
    public <T> Long decr(keyPrefix prefix, String key) {
        Jedis jedis = null;//通过这个redis进行get和set

        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey=prefix.getPrefix()+key;
            return  jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }


    private <T> String beamToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);//其他类型我们认为这是一个bean
        }

    }

    @SuppressWarnings("unchecked")
    private <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);//其他类型我们认为这是一个bean
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();//并不close 而是返回连接池
        }
    }


}
