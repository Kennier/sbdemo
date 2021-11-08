package com.kennie.config;

/**
 * @author xuxinjian
 * @since 2020-04-30
 */

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

/**
 * 基于注解添加缓存
 * @author 徐新建
 */
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    private final RedisConnectionFactory redisConnectionFactory;

    CacheConfig(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Bean("methodCacheKeyGenerator")
    @Override
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {


            StringBuilder sb = new StringBuilder(32);
            sb.append(o.getClass().getSimpleName());
            sb.append("_");
            sb.append(method.getName());
            if (objects.length > 0) {
                sb.append("#");
            }
            String sp = "";
            for (Object object : objects) {
                sb.append(sp);
                if (object == null) {
                    sb.append("NULL");
                } else {
//                    if (object instanceof Page) {//batis的page
//                        Page p = (Page) object;
//                        sb.append(p.getCurrent() + "_" + p.getSize());
//                    } else {
//                        sb.append(object.toString());
//                    }
                }
                sp = "_";
            }
            return sb.toString();

        };
    }

    /**
     * 配置 RedisCacheManager，使用 cache 注解管理 redis 缓存
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        // 初始化一个RedisCacheWriter
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);

        // 设置默认过期时间：30 分钟
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                // .disableCachingNullValues()
                // 使用注解时的序列化、反序列化
                .serializeKeysWith(CustomRedisCacheManager.STRING_PAIR)
                .serializeValuesWith(CustomRedisCacheManager.FASTJSON_PAIR);

        return new CustomRedisCacheManager(cacheWriter, defaultCacheConfig);
    }
}