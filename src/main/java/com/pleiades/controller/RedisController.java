package com.pleiades.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisController {
    private final StringRedisTemplate redisTemplate;
    @GetMapping("/write")
    public String write() {
        redisTemplate.opsForValue().set("hello", "world");
        return "written";
    }
    @GetMapping("/read")
    public String read() {
        return redisTemplate.opsForValue().get("hello");
    }
}
