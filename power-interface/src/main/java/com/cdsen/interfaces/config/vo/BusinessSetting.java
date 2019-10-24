package com.cdsen.interfaces.config.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HuSen
 * create on 2019/10/24 12:22
 */
@Data
public class BusinessSetting implements Serializable {
    private Integer code;
    private String name;
}
