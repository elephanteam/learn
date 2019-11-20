package com.limit.learn.mvp;


import com.limit.learn.entity.LoginBean;

import io.reactivex.Observable;

/**
 * 账户相关操作
 */
public interface AccountModelImp {

    /**
     * 账号 密码 登录
     * @param account   用户名  手机号  邮箱
     * @param pwd       密码
     * @param aesKey    加密规则
     */
    Observable<LoginBean> doLogin(String account, String pwd, String aesKey);

    /**
     * 账号 密码 注册
     * @param account   用户名  手机号  邮箱
     * @param pwd       密码
     * @param aesKey    加密规则
     */
    Observable<LoginBean> doRegister(String account, String pwd, String aesKey);

}
