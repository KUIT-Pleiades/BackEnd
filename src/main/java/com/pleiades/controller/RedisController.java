package com.pleiades.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedisController {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/redis-test")
    public ResponseEntity<String> testRedis() {
        redisTemplate.opsForValue().set("hello", "world");
        String value = redisTemplate.opsForValue().get("hello");
        return ResponseEntity.ok("value: " + value);
    }
}
