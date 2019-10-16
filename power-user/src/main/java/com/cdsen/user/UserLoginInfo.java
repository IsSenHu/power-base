package com.cdsen.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author HuSen
 * create on 2019/10/16 10:22
 */
@Getter
@Setter
@AllArgsConstructor
public class UserLoginInfo implements Serializable {

    private Long userId;
    private String username;
    private boolean isAccountNonLocked;
}
