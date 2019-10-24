package com.cdsen.user;

import com.cdsen.apollo.ConfigUtils;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * @author HuSen
 * create on 2019/10/16 11:34
 */
@EnableApolloConfig
@EnableConfigurationProperties(SecurityConfig.class)
public class UserAutoConfiguration {

    private static final String NAMESPACE = "POWER.USER";

    public UserAutoConfiguration(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    private final SecurityConfig securityConfig;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisPoolConfig poolConfig  = new JedisPoolConfig();

        // 连接耗尽时是否阻塞，false报异常，true阻塞直到超时，默认true
        String blockWhenExhausted = ConfigUtils.getProperty(NAMESPACE, "blockWhenExhausted", "true");
        poolConfig.setBlockWhenExhausted(Boolean.parseBoolean(blockWhenExhausted));

        // 设置的逐出策略类名，默认DefaultEvictionPolicy(当连接超过最大空闲时间, 或连接数超过最大空闲连接数)
        String evictionPolicyClassName = ConfigUtils.getProperty(NAMESPACE, "evictionPolicyClassName", "org.apache.commons.pool2.impl.DefaultEvictionPolicy");
        poolConfig.setEvictionPolicyClassName(evictionPolicyClassName);

        // 是否启用pool的jmx管理功能, 默认true
        String jmxEnabled = ConfigUtils.getProperty(NAMESPACE, "jmxEnabled", "true");
        poolConfig.setJmxEnabled(Boolean.parseBoolean(jmxEnabled));

        // 默认为pool
        String jmxNamePrefix = ConfigUtils.getProperty(NAMESPACE, "JmxNamePrefix", "pool");
        poolConfig.setJmxNamePrefix(jmxNamePrefix);

        // 是否启用后进先出，默认true
        String lifo = ConfigUtils.getProperty(NAMESPACE, "lifo", "true");
        poolConfig.setLifo(Boolean.parseBoolean(lifo));

        // 最大空闲连接数，默认8个
        String maxIdle = ConfigUtils.getProperty(NAMESPACE, "maxIdle", "8");
        poolConfig.setMaxIdle(Integer.parseInt(maxIdle));

        // 最大连接数，默认8个
        String maxTotal = ConfigUtils.getProperty(NAMESPACE, "maxTotal", "8");
        poolConfig.setMaxTotal(Integer.parseInt(maxTotal));

        // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted)，如果超时就抛异常，小于零:阻塞不确定的时间，默认-1
        String maxWaitMillis = ConfigUtils.getProperty(NAMESPACE, "maxWaitMillis", "-1");
        poolConfig.setMaxWaitMillis(Long.parseLong(maxWaitMillis));

        // 资源池中资源最小空闲时间(单位为毫秒，达到此值后空闲资源将被移除，默认1800000毫秒(30分钟)
        String minEvictableIdleTimeMillis = ConfigUtils.getProperty(NAMESPACE, "minEvictableIdleTimeMillis", "1800000");
        poolConfig.setMinEvictableIdleTimeMillis(Long.parseLong(minEvictableIdleTimeMillis));

        // 最小空闲连接数，默认0
        String minIdle = ConfigUtils.getProperty(NAMESPACE, "minIdle", "0");
        poolConfig.setMinIdle(Integer.parseInt(minIdle));

        // 每次逐出检查时，检查的最大数目，如果为负数就是 : 1/abs(n)，默认3
        String numTestsPerEvictionRun = ConfigUtils.getProperty(NAMESPACE, "numTestsPerEvictionRun", "3");
        poolConfig.setNumTestsPerEvictionRun(Integer.parseInt(numTestsPerEvictionRun));

        // 对象空闲多久后逐出，当空闲时间>该值且空闲连接>最大空闲数时直接逐出，不再根据MinEvictableIdleTimeMillis判断(默认逐出策略)
        String softMinEvictableIdleTimeMillis = ConfigUtils.getProperty(NAMESPACE, "softMinEvictableIdleTimeMillis", "1800000");
        poolConfig.setSoftMinEvictableIdleTimeMillis(Long.parseLong(softMinEvictableIdleTimeMillis));

        // 在获取连接的时候检查有效性，默认false
        String testOnBorrow = ConfigUtils.getProperty(NAMESPACE, "testOnBorrow", "false");
        poolConfig.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));

        // 在空闲时检查连接有效性，默认false
        String testWhileIdle = ConfigUtils.getProperty(NAMESPACE, "testWhileIdle", "false");
        poolConfig.setTestWhileIdle(Boolean.parseBoolean(testWhileIdle));

        // 空闲资源的检测周期(单位为毫秒) 如果为负数，则不运行逐出线程，默认-1
        String timeBetweenEvictionRunsMillis = ConfigUtils.getProperty(NAMESPACE, "timeBetweenEvictionRunsMillis", "-1");
        poolConfig.setTimeBetweenEvictionRunsMillis(Long.parseLong(timeBetweenEvictionRunsMillis));

        // 在返回连接之前检查有效性，默认false
        String testOnReturn = ConfigUtils.getProperty(NAMESPACE, "testOnReturn", "false");
        poolConfig.setTestOnReturn(Boolean.parseBoolean(testOnReturn));

        // 在创建之前检查pool的有效性，默认false
        String testOnCreate = ConfigUtils.getProperty(NAMESPACE, "testOnCreate", "false");
        poolConfig.setTestOnCreate(Boolean.parseBoolean(testOnCreate));

        // 连接池Client Builder
        String readTimeOut = ConfigUtils.getProperty(NAMESPACE, "readTimeOut", "2000");
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder poolingClientConfigurationBuilder
                = JedisClientConfiguration.builder().readTimeout(Duration.ofMillis(Long.parseLong(readTimeOut))).usePooling().poolConfig(poolConfig);

        // ****************创建单节点配置****************
        String standaloneHostName = ConfigUtils.getProperty(NAMESPACE, "standaloneHostName", "127.0.0.1");
        String standalonePort = ConfigUtils.getProperty(NAMESPACE, "standalonePort", "6379");
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(standaloneHostName, Integer.parseInt(standalonePort));

        // 使用数据库1
        String standaloneDatabase = ConfigUtils.getProperty(NAMESPACE, "standaloneDatabase", "0");
        standaloneConfiguration.setDatabase(Integer.parseInt(standaloneDatabase));

        // 密码
        String standalonePassword = ConfigUtils.getProperty(NAMESPACE, "standalonePassword", "");
        standaloneConfiguration.setPassword(standalonePassword);

        // 创建连接工厂
        return new JedisConnectionFactory(standaloneConfiguration, poolingClientConfigurationBuilder.build());
    }

    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(@Qualifier("jedisConnectionFactory") JedisConnectionFactory connectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(connectionFactory);
        // 开启事务支持
        stringRedisTemplate.setEnableTransactionSupport(true);
        return stringRedisTemplate;
    }

    @Bean
    public UserManager userManager(StringRedisTemplate redisTemplate) {
        return new RedisUserManagerImpl(redisTemplate, securityConfig);
    }
}
