package com.cdsen.apollo;

/**
 * @author HuSen
 * create on 2019/10/15 10:15
 */
public class AppProperties {

    public static class Security {

        public static final String SECRET = "com.cdsen.security.secret";

        public static final String EXPIRATION = "com.cdsen.security.expiration";

        public static final String MAX_SESSION_IN_CACHE = "com.cdsen.security.max-session-in-cache";

        public static final String HEADER = "com.cdsen.security.header";
    }

    public static class Rabbitmq {
        public static final String RABBITMQ_NAMESPACE = "rabbitmq";
        public static final String DIRECT_QUEUE_POWER = "directQueuePower";
        public static final String DIRECT_EXCHANGE_POWER = "directExchangePower";
        public static final String DIRECT_KEY_POWER = "directKeyPower";

        public static final String DIRECT_QUEUE_CREATE_CONSUMPTION = "directQueueCreateConsumption";
        public static final String DIRECT_KEY_CREATE_CONSUMPTION = "directKeyCreateConsumption";
    }
}
