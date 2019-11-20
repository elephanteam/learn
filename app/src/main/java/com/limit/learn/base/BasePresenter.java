package com.limit.learn.base;

import io.reactivex.disposables.Disposable;

public interface BasePresenter<T> {

    // 将网络请求的disposable添加入compositeDisposable,退出时销毁
    void addDisposable(Disposable subscription);

    // 注销所有请求
    void unDisposable();
}