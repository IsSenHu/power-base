package com.cdsen.rabbit.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author HuSen
 * create on 2019/9/3 11:17
 */
@Data
public class InComeCreateDTO {

    /**
     * 收入
     */
    private BigDecimal income;

    /**
     * 收入时间
     */
    private String time;

    /**
     * 收入说明
     */
    private String description;

    /**
     * 货币类型
     */
    private String currency;

    /**
     * 收入渠道
     */
    private Integer channel;
}
