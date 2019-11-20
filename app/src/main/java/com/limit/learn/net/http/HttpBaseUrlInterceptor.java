package com.limit.learn.net.http;

import com.limit.learn.base.BaseModuleConfig;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 动态修改 BaseUrl 拦截器
 */
public class HttpBaseUrlInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {

    Request request = chain.request();

    // 获取 header 标记
    List<String> hostTypes = request.headers(HttpConfg.HTTP_URL_TYPE_KEY);

    if (null != hostTypes && hostTypes.size() > 0) {
      // 移除标记 header
      Request.Builder builder = request.newBuilder();
      builder.removeHeader(HttpConfg.HTTP_URL_TYPE_KEY);
      HttpUrl newBaseUrl;
      String headerValue = hostTypes.get(0);

      // 默认 主服务器地址
      if (HttpConfg.HTTP_URL_TYPE_VALUE_MAIN.equals(headerValue)) {
        newBaseUrl = HttpUrl.parse(BaseModuleConfig.APP_HOST);
      } else {// 默认服务器地址
        newBaseUrl = HttpUrl.parse(BaseModuleConfig.APP_HOST);
      }
      // 重建新的 HttpUrl，修改需要修改的 url 部分
      HttpUrl newFullUrl = request.url()
              .newBuilder()
              .scheme(newBaseUrl.scheme())
              .host(newBaseUrl.host())
              .port(newBaseUrl.port())
              .removePathSegment(0) // 移除第一个参数
              .build();

      // 重建这个 request，通过 builder.url(newFullUrl).build()
      // 然后返回一个 response 至此结束修改
      return chain.proceed(builder.url(newFullUrl).build());
    }

    return chain.proceed(request);
  }

}
