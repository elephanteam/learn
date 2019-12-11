package com.limit.learn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;

import com.limit.learn.base.BaseActivity;
import com.limit.learn.base.BasePresenter;
import com.limit.learn.bluetooth.BlueToothActivity;
import com.limit.learn.sms.SmsActivity;
import com.limit.learn.video.VideoActivity;
import com.limit.learn.wifi.direct.WifiP2PActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.OnClick;

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

    }

    @OnClick(R.id.main_video)
    public void onClickVideoView(){
        startActivity(new Intent(this, VideoActivity.class));
    }


    @OnClick(R.id.main_wifi)
    public void onClickWifiView(){
        startActivity(new Intent(this, WifiP2PActivity.class));
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.main_sms)
    public void onClickSmsView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new RxPermissions(this).request(Manifest.permission.SEND_SMS
                            ,Manifest.permission.READ_SMS
                            ,Manifest.permission.RECEIVE_SMS
                            ,Manifest.permission.READ_PHONE_STATE)
                    .subscribe(aBoolean -> {
                        startActivity(new Intent(this, SmsActivity.class));
                    });
        }else{
            startActivity(new Intent(this, SmsActivity.class));
        }
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.main_bluetooth)
    public void onClickBluetoothView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new RxPermissions(this).request(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .subscribe(aBoolean -> {
                        startActivity(new Intent(this, BlueToothActivity.class));
                    });
        }else{
            startActivity(new Intent(this, BlueToothActivity.class));
        }
    }
}
