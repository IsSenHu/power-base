package com.cdsen.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;

/**
 * @author HuSen
 * create on 2019/10/12 15:56
 */
public class ConfigUtils {

    public static String getProperty(String key, String defaultValue) {
        Config config = ConfigService.getAppConfig();
        return config.getProperty(key, defaultValue);
    }

    public static String getProperty(String namespace, String key, String defaultValue) {
        Config config = ConfigService.getConfig(namespace);
        return config.getProperty(key, defaultValue);
    }

    public static void addChangeListener(ConfigChangeListener listener) {
        ConfigService.getAppConfig().addChangeListener(listener);
    }
}
