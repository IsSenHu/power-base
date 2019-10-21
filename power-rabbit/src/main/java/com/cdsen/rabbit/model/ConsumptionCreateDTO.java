package com.cdsen.rabbit.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/10/21 14:37
 */
@Data
public class ConsumptionCreateDTO implements Serializable {

    private String time;
    private String currency;
    private List<ConsumptionItemCreateDTO> items;
}
