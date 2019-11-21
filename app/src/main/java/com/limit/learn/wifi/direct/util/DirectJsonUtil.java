package com.limit.learn.wifi.direct.util;

import android.os.Build;

import com.limit.learn.wifi.entity.WifiP2pEntity;

import org.json.JSONException;
import org.json.JSONObject;

public class DirectJsonUtil {


    /**
     * 封装发送发现节点消息
     * @param p2pEntity  发现的wifi用户信息
     * */
    public static String getSendPeerJson(WifiP2pEntity p2pEntity){

        JSONObject object=new JSONObject();
        try {
            object.put(DirectExtra.chatType, "0");
            object.put(DirectExtra.wifiUid, p2pEntity.getWifiUid());
            object.put(DirectExtra.wifiHost, p2pEntity.getHost());
            object.put(DirectExtra.wifiPort, String.valueOf(p2pEntity.getPort()));
            object.put(DirectExtra.deviceAddress, p2pEntity.getDeviceAddress());
            object.put(DirectExtra.phoneName,android.os.Build.BRAND);
            object.put(DirectExtra.phoneModel, android.os.Build.MODEL);
            object.put(DirectExtra.phoneVersionRelease,android.os.Build.VERSION.RELEASE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

}
