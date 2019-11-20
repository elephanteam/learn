package com.limit.learn.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView {

    protected P mPresenter;
    private Unbinder mUnBinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        mUnBinder = ButterKnife.bind(this,view);
        initView(view);
        mPresenter = createPresenter();
        initData();
        return view;
    }

    /**
     * 初始化 View
     * @param view
     */
    protected abstract void initView(View view);

    public abstract int getLayoutId();

    public abstract P createPresenter();

    protected abstract void initData();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mPresenter) {
            mPresenter.unDisposable();
            mPresenter = null;
        }
        mUnBinder.unbind();
    }
}