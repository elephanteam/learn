package com.limit.learn.wifi.direct;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

import com.limit.learn.R;
import com.limit.learn.base.BaseActivity;
import com.limit.learn.base.BasePresenter;
import com.limit.learn.wifi.WifiConstant;
import com.limit.learn.wifi.direct.util.DirectExtra;
import com.limit.learn.wifi.entity.WifiP2pEntity;
import com.limit.learn.wifi.service.WifiDirectService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class WifiP2PActivity extends BaseActivity {

    @BindView(R.id.wifi_direct_search_content)
    TextView wifiDirectSearchContent;

    private ArrayList<WifiP2pEntity> mDatas;

    private boolean isBound = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wifi_direct;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        isBound = bindService(new Intent(this, WifiDirectService.class), serviceConn, Activity.BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter(WifiConstant.WIFI_DIRECT_OFFLINE_MEMBER_LIST);
        filter = new IntentFilter(WifiConstant.WIFI_DIRECT_OFFLINE_MSG);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private ServiceConnection serviceConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            WifiDirectService.NetServiceBinder binder = (WifiDirectService.NetServiceBinder) service;
            mDatas = (ArrayList<WifiP2pEntity>) binder.getService().getwifiPeopleList();
        }

        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceConn != null){
            try {
                if (isBound){
                    unbindService(serviceConn);
                    isBound = false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.wifi_direct_search)
    public void onClickWifiSearch(){
        Intent intent = new Intent(this, WifiDirectService.class);
        stopService(intent);
        startService(intent);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && (WifiConstant.WIFI_DIRECT_OFFLINE_MEMBER_LIST.equals(intent.getAction()))){
                mDatas = (ArrayList<WifiP2pEntity>) intent.getSerializableExtra("onLineMember");
            }else if (intent != null && (WifiConstant.WIFI_DIRECT_OFFLINE_MSG.equals(intent.getAction()))){
                wifiDirectSearchContent.setText(intent.getStringExtra(DirectExtra.wifiMessage));
            }
        }
    };
}
