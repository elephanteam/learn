package com.limit.learn.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public abstract class BasePresenterImpl<V extends BaseView> implements BasePresenter {

    protected V view;

    private CompositeDisposable mCompositeDisposable;


    public BasePresenterImpl(V view) {
        this.view = view;
        attachModel();
    }

    /**
     * 绑定 Model 层
     */
    protected abstract void attachModel();

    /**
     * 解绑 Model 层
     */
    protected abstract void detachModel();

    @Override
    public void addDisposable(Disposable subscription) {
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }


    /**
     * 在界面退出等需要解绑观察者的情况下调用此方法统一解绑，防止Rx造成的内存泄漏
     */
    @Override
    public void unDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
        detachModel();
    }
}
