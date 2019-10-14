package com.cdsen.interfaces.user.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HuSen
 * create on 2019/10/12 11:59
 */
@Data
public class UserVO implements Serializable {
    private Long userId;
    private String username;
}
