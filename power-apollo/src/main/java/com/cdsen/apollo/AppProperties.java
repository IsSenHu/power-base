package com.cdsen.apollo;

import lombok.Getter;

/**
 * @author HuSen
 * create on 2019/10/15 10:15
 */
public class AppProperties {

    public static class Rabbitmq {
        public static final String RABBITMQ_NAMESPACE = "rabbitmq";

        public static final String DIRECT_EXCHANGE_POWER = "directExchangePower";
        public static final String DIRECT_QUEUE_CREATE_CONSUMPTION = "directQueueCreateConsumption";
        public static final String DIRECT_KEY_CREATE_CONSUMPTION = "directKeyCreateConsumption";
    }

    @Getter
    public enum Config {
        //
        CONSUMPTION_TYPE("消费类型"),
        INCOME_CHANNEL("收入渠道");
        private String name;

        Config(String name) {
            this.name = name;
        }
    }
}
