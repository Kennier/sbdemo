package com.kennie.config;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 缓存过期注解，解决使用spring-data-redis注解缓存时无法指定缓存时间问题
 * @author 徐新建
 * @since 2020-09-11
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheExpire {

    /**
     * expire time, default 60s
     */
    @AliasFor("expire")
    long value() default 60L;

    /**
     * expire time, default 60s
     */
    @AliasFor("value")
    long expire() default 60L;

    /**
     * 是否开始随机，开启后将在指定范围内随机生成缓存时间
     * @return
     */
    boolean enableRandom() default false;

    /**
     * 最小缓存时间，开启随机缓存时间时，如果随机时间比最小缓存时间小，则采用最小缓存时间
     * @return
     */
    long min() default 0L;

}
