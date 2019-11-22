package com.cdsen.apollo;

/**
 * @author HuSen
 * create on 2019/11/22 15:25
 */
public class AppProperties {

    public static class Rabbitmq {
        public static final String RABBITMQ_NAMESPACE = "rabbitmq";
        public static final String DIRECT_EXCHANGE_POWER = "directExchangePower";

        public static final String DIRECT_KEY_CREATE_CONSUMPTION = "directKeyCreateConsumption";
        public static final String DIRECT_QUEUE_CREATE_CONSUMPTION = "directQueueCreateConsumption";

        public static final String DIRECT_KEY_CREATE_INCOME = "directKeyCreateIncome";
        public static final String DIRECT_QUEUE_CREATE_INCOME = "directQueueCreateIncome";
    }
}
