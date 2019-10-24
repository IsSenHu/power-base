package com.cdsen.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

/**
 * @author HuSen
 * create on 2019/10/24 11:15
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "com.cdsen.security")
public class SecurityConfig implements InitializingBean {

    private String secret;

    private Long expiration;

    private Long maxSessionInCache;

    private String header;

    @Override
    public void afterPropertiesSet() {
        Assert.hasText(secret, "");
        Assert.notNull(expiration, "");
        Assert.notNull(maxSessionInCache, "");
        Assert.hasText(header, "");
        log.info("ヾ(Ő∀Ő๑)ﾉ太好惹 加载到安全配置:{}", this);
    }
}
