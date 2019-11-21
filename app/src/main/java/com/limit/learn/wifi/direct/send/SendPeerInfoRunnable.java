package com.limit.learn.wifi.direct.send;

import android.util.Log;

import com.limit.learn.base.BaseApplication;
import com.limit.learn.util.ToastUtils;
import com.limit.learn.wifi.WifiConstant;
import com.limit.learn.wifi.direct.util.DirectJsonUtil;
import com.limit.learn.wifi.entity.WifiP2pEntity;
import com.limit.learn.wifi.service.WifiDirectService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
* 发送一个新的发现消息
*/
public class SendPeerInfoRunnable implements Runnable {

   private WifiP2pEntity peerInfo;
   private WifiDirectService netService;
   private String host;
   private int port;

   public SendPeerInfoRunnable(String host, int port, WifiDirectService netService, WifiP2pEntity peerInfo) {
       this.host = host;
       this.port = port;
       this.netService = netService;
       this.peerInfo = peerInfo;

   }

   @Override
   public void run() {
       if (sendPeerInfo(host, port,peerInfo)) {
           ToastUtils.showToast(BaseApplication.getContext(),"成功");
       }
   }

   private boolean sendPeerInfo(String host, int port, WifiP2pEntity peerInfo) {
       Socket socket = new Socket();
       boolean result = true;
       try {
           socket.bind(null);
           socket.connect((new InetSocketAddress(host, port)), WifiConstant.SOCKET_TIMEOUT);// host
           OutputStream stream = socket.getOutputStream();
           stream.write(WifiConstant.COMMAND_ID_SEND_TYPE_SYSTEM);
           String jsonString = DirectJsonUtil.getSendPeerJson(peerInfo);
           String jsonLength = String.valueOf(jsonString.getBytes(StandardCharsets.UTF_8).length);
           stream.write(jsonLength.length());
           stream.write(jsonLength.getBytes());
           stream.write(jsonString.getBytes());
           Log.e("************","send peers");
       } catch (IOException e) {
           result = false;
       } finally {
           if (socket.isConnected()) {
               try {
                   socket.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
       return result;
   }
}
