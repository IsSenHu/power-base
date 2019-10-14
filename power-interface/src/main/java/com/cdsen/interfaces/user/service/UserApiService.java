package com.cdsen.interfaces.user.service;

import com.cdsen.interfaces.DubboResult;
import com.cdsen.interfaces.user.vo.UserVO;

/**
 * @author HuSen
 * create on 2019/10/12 11:59
 */
public interface UserApiService {

    /**
     * 根据Token获取用户
     *
     * @param token Token
     * @return 用户信息
     */
    DubboResult<UserVO> find(String token);
}
