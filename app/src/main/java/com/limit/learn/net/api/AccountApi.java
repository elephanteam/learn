package com.limit.learn.net.api;

import com.limit.learn.entity.HttpResult;
import com.limit.learn.entity.LoginBean;
import com.limit.learn.entity.UserBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 用户相关 API
 */
public interface AccountApi{

    /**
     * 获取用户个人信息
     */
    @POST("/api/user/info")
    Observable<HttpResult<UserBean>> getUserInfo(@Body RequestBody body);

    /**
     * 获取用户个人信息
     */
    @POST("index.php")
    Observable<ResponseBody> login(@Body RequestBody body);

    @FormUrlEncoded
    @POST("index.php")
    Observable<LoginBean> getData(@Field("body") String jsonRequest);

}
