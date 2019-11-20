package com.limit.learn.net.http;

/**
 * HTTP 配置常量
 */
public class HttpConfg {

    /**
     * 连接超时时长x秒
     */
    public static final int HTTP_CONNECT_TIME_OUT = 15;
    /**
     * 写数据接超时时长x秒
     */
    public static final int HTTP_WRITE_TIME_OUT = 30;
    /**
     * 读数据超时时长x秒
     */
    public static final int HTTP_READ_TIME_OUT = 30;

    /**
     * 切换 服务器地址 header 标记 Key
     */
    public static final String HTTP_URL_TYPE_KEY = "base_url_type";
    /**
     * 主服务器地址（默认）
     */
    public static final String HTTP_URL_TYPE_VALUE_MAIN = "base_url_main";

}
