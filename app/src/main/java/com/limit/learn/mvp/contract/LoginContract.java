package com.limit.learn.mvp.contract;

import com.limit.learn.base.BasePresenter;
import com.limit.learn.base.BaseView;

public class LoginContract {

    public interface View extends BaseView {

    }

    public interface Presenter extends BasePresenter {
        void doLogin(String account, String password, String aesKey);
    }
}
