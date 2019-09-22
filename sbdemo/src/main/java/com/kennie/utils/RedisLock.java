package com.kennie.utils;

import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /*注意点：
    *       1：缓存有效期
    *       2：加锁时，每个节点产生一个随机字符串
    *       3：保证原子性，写值和设置过期时间必须同时
    * */

    public Boolean redisLock(String key, String requestId, long expire){
        return (Boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            //1.8.13.RELEASE版本
            //这里void返回类型无法成为boolen 2.0以下的connection.set是没有返回值的
            Boolean result =  connection.set(key.getBytes(),requestId.getBytes(), Expiration.from(expire, TimeUnit.SECONDS), RedisStringCommands.SetOption.SET_IF_ABSENT);
            //木有过期时间
//            Boolean result = connection.setNX(key.getBytes(),requestId.getBytes());
            //所以使用这个
//            Jedis jedis = (Jedis) connection.getNativeConnection();
//            if("OK".equals(jedis.set(key, requestId, "NX", "EX", 10000))){
//                return true;
//            }
//            return false
            return result;
        });
    };

    public boolean redisUnlock(String key, String requestId) {
        return (Boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Boolean result = connection.eval(script.getBytes(), ReturnType.BOOLEAN, 1, key.getBytes(), requestId.getBytes());
            return result;
        });
    }


    public Boolean redisLock2(String key, String requestId, long expire){
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key,requestId);
        //setIfAbsent()底层调用的就是setNX无法设置过期时间  代码设置过去时间  但是这样就不是原子操作
        if (stringRedisTemplate.opsForValue().setIfAbsent(key,requestId)) {
            //这里存在宕机风险，导致设置有效期失败
            stringRedisTemplate.expire(key,100000, TimeUnit.SECONDS);
        }
        return result;
    };

    public void redisUnlock2(String key, String requestId){
        if (requestId.equals(stringRedisTemplate.opsForValue().get(key))) {
            stringRedisTemplate.delete(key);
        }
    }

}
