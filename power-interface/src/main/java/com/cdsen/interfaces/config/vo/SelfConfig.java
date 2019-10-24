package com.cdsen.interfaces.config.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/10/24 14:45
 */
@Data
public class SelfConfig implements Serializable {

    private Long id;

    private String name;

    private String type;

    private String configType;

    private Map customInfo;
}
