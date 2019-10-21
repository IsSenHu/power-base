package com.cdsen.rabbit.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author HuSen
 * create on 2019/10/21 14:39
 */
@Data
public class ConsumptionItemCreateDTO implements Serializable {
    private BigDecimal money;
    private String description;
}
