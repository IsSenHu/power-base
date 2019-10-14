package com.cdsen.interfaces;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HuSen
 * create on 2019/10/12 14:51
 */
@Data
public class DubboResult<T> implements Serializable {
    private int code;
    private String error;
    private T data;

    public static <T> DubboResult<T> of(T d) {
        DubboResult<T> result = new DubboResult<>();
        result.setCode(0);
        result.setData(d);
        return result;
    }

    public static <T> DubboResult<T> of(int code, String error) {
        DubboResult<T> result = new DubboResult<>();
        result.setCode(code);
        result.setError(error);
        return result;
    }
}
