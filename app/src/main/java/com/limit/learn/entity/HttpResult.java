package com.limit.learn.entity;


import com.limit.learn.net.http.HttpCodeHelper;

import java.io.Serializable;

/**
 * 服务器返回数据结构
 */
public class HttpResult<T> implements Serializable {
    private static final long serialVersionUID = -249073937469117954L;

    public int code;

    public String msg;

    public T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 网络请求是否成功
     *
     * @return true：200 成功
     */
    public boolean isSuccess() {
        return HttpCodeHelper.isSuccess(code);
    }
}
