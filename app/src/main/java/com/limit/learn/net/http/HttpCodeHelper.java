package com.limit.learn.net.http;

/**
 * Http 返回请求码
 */
public class HttpCodeHelper {

  /**
   * 网络请求成功
   */
  public static final int HTTP_RESPONSE_CODE_SUCCESS = 200;

  public static boolean isSuccess(int code){
    return HTTP_RESPONSE_CODE_SUCCESS == code;
  }

}
