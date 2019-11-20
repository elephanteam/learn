package com.limit.learn.base;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseActivity<P extends BasePresenter> extends FragmentActivity {

    protected P mPresenter;
    private Unbinder mUnBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mUnBinder = ButterKnife.bind(this);
        initView();
        mPresenter = createPresenter();
        initData();
    }

    private void initView() {

    }

    public abstract int getLayoutId();

    public abstract P createPresenter();

    protected abstract void initData();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.unDisposable();
            mPresenter = null;
        }
        mUnBinder.unbind();
    }
}