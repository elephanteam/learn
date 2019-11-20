package com.limit.learn.mvp.contract;

import android.content.Context;

import com.limit.learn.base.BasePresenter;
import com.limit.learn.base.BaseView;

public class RegisterContract {

    public interface View extends BaseView {

    }

    public interface Presenter extends BasePresenter {

        /**
         * 是否匹配注册规则
         * */
        String isMatchAccount(Context context, String account, String password, String passwordAgain);

        /**
         * 注册
         * @param account    用户名
         * @param password   密码
         * @param aesKey     加密字符串
         * */
        void doRegister(String account, String password, String aesKey);
    }
}
