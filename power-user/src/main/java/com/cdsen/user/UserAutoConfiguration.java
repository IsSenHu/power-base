package com.cdsen.user;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author HuSen
 * create on 2019/10/16 11:34
 */
public class UserAutoConfiguration {

    private final StringRedisTemplate redisTemplate;

    public UserAutoConfiguration(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public UserManager userManager() {
        return new RedisUserManagerImpl(redisTemplate);
    }
}
