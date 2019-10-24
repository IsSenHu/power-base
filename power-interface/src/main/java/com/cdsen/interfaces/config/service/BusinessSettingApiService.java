package com.cdsen.interfaces.config.service;

import com.cdsen.interfaces.config.vo.BusinessSetting;

import java.util.List;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/10/24 14:18
 */
public interface BusinessSettingApiService {

    void push(Map<String, List<BusinessSetting>> changedMap);
}
