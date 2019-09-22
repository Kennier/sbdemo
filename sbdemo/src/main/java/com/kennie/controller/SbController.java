package com.kennie.controller;

import com.kennie.utils.RedisLock;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
public class SbController {

    @Resource
    private RedisLock redisLock;

    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    };

    @RequestMapping("/redis/lock")
    public Boolean redisLock(HttpSession httpSession){
        System.out.println(httpSession.getId());
        return redisLock.redisLock("lock",httpSession.getId(),100000);
    };

    @RequestMapping("/redis/unlock")
    public Boolean redisUnlock(HttpSession httpSession){
        return redisLock.redisUnlock("lock",httpSession.getId());
    };

}
