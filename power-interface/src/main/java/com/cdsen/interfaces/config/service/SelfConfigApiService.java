package com.cdsen.interfaces.config.service;

import com.cdsen.interfaces.config.vo.SelfConfig;

import java.util.List;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/10/24 14:39
 */
public interface SelfConfigApiService {

    void push(Long userId, Map<String, List<SelfConfig>> selfConfig);
}
