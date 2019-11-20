package com.limit.learn.net.http;

import com.limit.learn.base.BaseModuleConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit OkHttp 初始化
 */
public class HttpService {

    private Retrofit mRetrofit;

    private static class SingletonHolder {
        private static final HttpService instance = new HttpService();
    }

    public static HttpService instance() {
        return SingletonHolder.instance;
    }

    private HttpService() {
        Retrofit.Builder builder = new Retrofit.Builder();

        // 域名 utl
        builder.baseUrl(BaseModuleConfig.APP_HOST);

        // Gson ：Http 返回数据格式解析器
        builder.addConverterFactory(GsonConverterFactory.create());

        // RxJava 网络返回回调
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        // OkHttpClient 配置
        builder.client(configHttpClient());

        mRetrofit = builder.build();
    }

    /**
     * OkHttpClient 配置
     *
     * @return OkHttpClient
     */
    private OkHttpClient configHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // 默认重试一次，若需要重试 N 次，则要实现拦截器
        builder.retryOnConnectionFailure(true);

        // 超时 时间
        builder.connectTimeout(HttpConfg.HTTP_CONNECT_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(HttpConfg.HTTP_WRITE_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(HttpConfg.HTTP_READ_TIME_OUT, TimeUnit.SECONDS);

        // BaseUrl 动态改变
//        builder.addInterceptor(new HttpBaseUrlInterceptor());

        // 日志打印
        builder.addInterceptor(new HttpLoggingInterceptor(new HttpLogInterceptor()).setLevel(HttpLoggingInterceptor.Level.BODY));

        // 请求加密
        // builder.addInterceptor(new HttpEncryptInterceptor());

        return builder.build();
    }

    public <T> T getService(Class<T> cls) {
        return mRetrofit.create(cls);
    }

}
