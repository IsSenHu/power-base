package com.cdsen.user;

/**
 * @author HuSen
 * create on 2019/10/16 10:14
 */
public interface UserManager {

    /**
     * 保存用户登录信息
     *
     * @param token         Token
     * @param userLoginInfo 用户登录信息
     * @return 是否保存成功
     */
    boolean saveUser(String token, UserLoginInfo userLoginInfo);

    /**
     * 获取用户登录信息
     *
     * @param token Token
     * @return 用户登录信息
     */
    UserLoginInfo getLoginInfo(String token);

    /**
     * 销毁用户登录信息
     *
     * @param token Token
     * @return 是否销毁成功
     */
    boolean invalidate(String token);

    /**
     * 修改用户的锁定状态
     *
     * @param token              Token
     * @param isAccountNonLocked 是否没有被锁定
     * @return 是否修改成功
     */
    boolean changeLockState(String token, boolean isAccountNonLocked);
}
