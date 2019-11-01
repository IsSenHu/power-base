package com.cdsen.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author HuSen
 * create on 2019/11/1 17:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailAuthToken implements Serializable {
    private String host;
    private String username;
    private String password;
    private String protocol;
    private String folder;
}
