/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.limit.learn.wifi.direct;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import com.limit.learn.wifi.listener.WifiP2pNetServiceListener;
import com.limit.learn.wifi.service.WifiDirectService;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "WiFiDirectBroadcastReceiver";
    private WifiDirectService netService;
    WifiP2pNetServiceListener serviceListener;

    /**
     * @ manager WifiP2pManager system service
     * @ channel Wifi p2p channel
     * @ activity activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiDirectService service, WifiP2pNetServiceListener listener) {
        super();
        this.netService = service;
        this.serviceListener = listener;
    }

    /**
     * (non-Javadoc)
     * @see BroadcastReceiver#onReceive(Context, Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // 判断是否支持 wifi点对点传输
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // 支持则开始搜索
            	netService.setIsWifiP2pEnabled(true);
            	netService.discoverPeer();
            } else {
            	netService.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // 查找到设备列表
            //从wifi p2p管理器请求可用的对等体。 这是个异步调用和调用活动通过通知PeerListListener.onPeersAvailable（）上的回调
            if (netService.isWifiP2pAviliable()) {
            	//建议用下方的方法  requestPeers是以前遗留 不知道去除会有什么情况 暂时保留看看效果
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    WifiP2pDeviceList mPeers  = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                    if (mPeers != null && mPeers.getDeviceList() != null && mPeers.getDeviceList().size() > 0){
                        netService.onPeersAvailable(mPeers);
                    }else{
                        netService.requestPeers(serviceListener);
                    }
                }else{
                    //requestPeers方法返回的数据大部分时候是空的 不建议用这个方法
                    netService.requestPeers(serviceListener);
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {//获取到连接状态改变的详细信息
            if (!netService.isWifiP2pAviliable()) {
                return;
            }
            //然后调用requestConnectionInfo获取连接信息
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP
            	netService.requestConnectionInfo(serviceListener);
            } else {
                // It's a disconnect
            	netService.discoverPeer();//If the disconnect the peer devices near try to search again
            	netService.onConnectionInfoInvalid();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {//自身设备信息改变
        	WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra( WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        	netService.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
    	}
    }
}