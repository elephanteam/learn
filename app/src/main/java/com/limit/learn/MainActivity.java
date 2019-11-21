package com.limit.learn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;

import com.limit.learn.base.BaseActivity;
import com.limit.learn.base.BasePresenter;
import com.limit.learn.video.ExoPlayerActivity;
import com.limit.learn.video.VideoActivity;
import com.limit.learn.video.VideoViewActivity;
import com.limit.learn.wifi.direct.WifiP2PActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            final RxPermissions rxPermissions = new RxPermissions(this);
//            rxPermissions
//                    .request(Manifest.permission.READ_PHONE_STATE
//                            ,Manifest.permission.WRITE_EXTERNAL_STORAGE
//                            ,Manifest.permission.READ_EXTERNAL_STORAGE
//                            ,Manifest.permission.RECORD_AUDIO
//                            ,Manifest.permission.READ_CONTACTS)
//                    .subscribe(aBoolean -> {
//
//                    });
//        }
    }

    @OnClick(R.id.main_video)
    public void onClickVideoView(){
        startActivity(new Intent(this, VideoActivity.class));
    }


    @OnClick(R.id.main_wifi)
    public void onClickWifiView(){
        startActivity(new Intent(this, WifiP2PActivity.class));
    }
}
