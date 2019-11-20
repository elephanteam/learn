package com.limit.learn.mvp;

import com.limit.learn.base.BaseModel;
import com.limit.learn.entity.LoginBean;
import com.limit.learn.net.ApiHelper;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 用户相关 网络实现类
 */
public class AccountModel extends BaseModel implements AccountModelImp {

    @Override
    public Observable<LoginBean> doLogin(String account, String pwd, String aesKey) {

        Map<String,String> params = new HashMap<>();
        params.put("loginid", account);
        params.put("password", pwd);
        params.put("aeskey", aesKey);
        params.put("encrypt", "1");
        return ApiHelper.getAccountApiService()
                .getData(getRequestJson(false,"user", "login",params))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<LoginBean> doRegister(String account, String pwd, String aesKey) {
        Map<String,String> params = new HashMap<>();
        params.put("loginid", account);
        params.put("password", pwd);
        params.put("aeskey", aesKey);
        params.put("encrypt", "1");
        return ApiHelper.getAccountApiService()
                .getData(getRequestJson(false,"user", "register",params))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
