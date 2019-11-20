package com.limit.learn.base;

public interface BaseView {

    void onResult(Object result);

    void onError(int errorCode, String errorMsg);

}
