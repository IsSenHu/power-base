package com.cdsen.user;

import com.cdsen.apollo.AppProperties;
import com.cdsen.apollo.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author HuSen
 * create on 2019/10/16 10:55
 */
@Slf4j
public class RedisUserManagerImpl implements UserManager {

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String IS_ACCOUNT_NON_LOCKED = "isAccountNonLocked";
    private static final String IS_ENABLED = "isEnabled";
    private static final String ROLES = "roles";

    private final StringRedisTemplate redisTemplate;

    RedisUserManagerImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean saveUser(String token, UserLoginInfo userLoginInfo) {
        try {
            String expiration = ConfigUtils.getProperty(AppProperties.Security.EXPIRATION, "60");
            redisTemplate.executePipelined((RedisCallback<Object>) redisConnection -> {
                Map<byte[], byte[]> values = new HashMap<>(4);
                values.put(stringToByteArr(USER_ID), stringToByteArr(userLoginInfo.getUserId().toString()));
                values.put(stringToByteArr(USERNAME), stringToByteArr(userLoginInfo.getUsername()));
                values.put(stringToByteArr(PASSWORD), stringToByteArr(userLoginInfo.getPassword()));
                values.put(stringToByteArr(IS_ACCOUNT_NON_LOCKED), stringToByteArr(String.valueOf(userLoginInfo.isAccountNonLocked())));
                values.put(stringToByteArr(IS_ENABLED), stringToByteArr(String.valueOf(userLoginInfo.isEnabled())));
                String rolesStr = CollectionUtils.isEmpty(userLoginInfo.getRoles()) ? "" : String.join(",", userLoginInfo.getRoles());
                values.put(stringToByteArr(ROLES), stringToByteArr(rolesStr));
                byte[] key = token.getBytes(StandardCharsets.UTF_8);
                redisConnection.hMSet(key, values);
                redisConnection.expire(key, TimeUnit.MINUTES.toSeconds(Long.parseLong(expiration)));
                return null;
            });
            return true;
        } catch (Exception e) {
            log.error("save user error:", e);
            return false;
        }
    }

    private byte[] stringToByteArr(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public UserLoginInfo getLoginInfo(String token) {
        try {
            HashOperations<String, String, String> hash = redisTemplate.opsForHash();
            Map<String, String> entries = hash.entries(token);
            return CollectionUtils.isEmpty(entries) ? null : new UserLoginInfo(
                    Long.parseLong(entries.get(USER_ID)),
                    entries.get(USERNAME),
                    entries.get(PASSWORD),
                    Boolean.parseBoolean(entries.get(IS_ACCOUNT_NON_LOCKED)),
                    Boolean.parseBoolean(entries.get(IS_ENABLED)),
                    Arrays.asList(entries.get(ROLES).split(","))
            );
        } catch (Exception e) {
            log.error("getLoginInfo error:", e);
            return null;
        }
    }

    @Override
    public boolean invalidate(String token) {
        try {
            Boolean delete = redisTemplate.delete(token);
            return Objects.isNull(delete) ? false : delete;
        } catch (Exception e) {
            log.error("invalidate user error:", e);
            return false;
        }
    }

    @Override
    public boolean changeLockState(String token, boolean isAccountNonLocked) {
        try {
            HashOperations<String, String, String> hash = redisTemplate.opsForHash();
            hash.put(token, IS_ACCOUNT_NON_LOCKED, String.valueOf(isAccountNonLocked));
            return true;
        } catch (Exception e) {
            log.error("changeLockState error:", e);
            return false;
        }
    }
}
