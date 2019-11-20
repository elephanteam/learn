package com.limit.learn.mvp.presenter;

import android.content.Context;

import com.limit.learn.base.BasePresenterImpl;
import com.limit.learn.mvp.contract.RegisterContract;
import com.limit.learn.entity.LoginBean;
import com.limit.learn.mvp.AccountModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RegisterPresenter extends BasePresenterImpl<RegisterContract.View> implements RegisterContract.Presenter {

    private AccountModel accountModel;

    private RegisterContract.View mView;

    public RegisterPresenter(RegisterContract.View view) {
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


    /**
     * 检测注册内容是都匹配
     * @param account         用户名
     * @param password        密码
     * @param passwordAgain   二次验证密码
     * */
    @Override
    public String isMatchAccount(Context context, String account, String password, String passwordAgain) {

//        if (TextUtils.isEmpty(account)){
//            return context.getString(R.string.account_username_can_not_empty);
//        }
//
//        if (TextUtils.isEmpty(password)){
//            return context.getString(R.string.account_password_can_not_empty);
//        }
//
//        if (!TextUtils.equals(password,passwordAgain)){
//            return context.getString(R.string.account_password_not_match);
//        }

        return null;
    }

    @Override
    public void doRegister(String account,String password,String aesKey) {
        try {
            accountModel.doRegister(account,password,aesKey).subscribe(new Observer<LoginBean>() {
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
