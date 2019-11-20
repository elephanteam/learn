package com.limit.learn.mvp.presenter;

import com.limit.learn.base.BasePresenterImpl;
import com.limit.learn.mvp.contract.LoginContract;
import com.limit.learn.entity.LoginBean;
import com.limit.learn.mvp.AccountModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LoginPresenter extends BasePresenterImpl<LoginContract.View> implements LoginContract.Presenter {

    private AccountModel accountModel;

    private LoginContract.View mView;

    public LoginPresenter(LoginContract.View view) {
        super(view);
        mView = view;
    }

    @Override
    protected void attachModel() {
        accountModel = new AccountModel();
    }

    @Override
    protected void detachModel() {
        accountModel = null;
    }

    @Override
    public void doLogin(String account,String password,String aesKey) {
        try {
            accountModel.doLogin(account,password,aesKey).subscribe(new Observer<LoginBean>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(LoginBean loginBean) {
                    if (loginBean == null){
                        mView.onError(-1,"");
                    }else if(loginBean.getErrcode() != 0){
                        mView.onError(loginBean.getErrcode(),loginBean.getMsg());
                    }else{
                        mView.onResult(loginBean);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    mView.onError(-1,e.getMessage());
                }

                @Override
                public void onComplete() {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
