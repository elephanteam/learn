package com.limit.learn.wifi.direct.contract;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;

import com.limit.learn.base.BasePresenter;
import com.limit.learn.base.BaseView;
import com.limit.learn.wifi.entity.WifiP2pEntity;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public interface WifiDirectServiceContract {

    interface Presenter extends BasePresenter {

        //registered p2p server
        void registerP2pService(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pDnsSdServiceInfo serviceInfo);

        //To monitor search results
        void setServiceResponse(WifiP2pManager manager, WifiP2pManager.Channel channel);

        //Locate near peer device
        void discoverPeers(WifiP2pManager manager, WifiP2pManager.Channel channel);

        void setServiceListeners(WifiP2pManager manager, WifiP2pManager.Channel channel);

        void discoverService(WifiP2pManager manager, WifiP2pManager.Channel channel);

        void renameDeviceName(WifiP2pManager manager, WifiP2pManager.Channel channel);

        void removeServiceListeners(WifiP2pManager manager, WifiP2pManager.Channel channel);

        void cancelDisconnect(WifiP2pManager manager, WifiP2pManager.Channel channel);

        void removeGroup(WifiP2pManager manager, WifiP2pManager.Channel channel);

        void connect(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pConfig config);

        void requestGroupInfo(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pDevice device, int type);

        void removeP2pService(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pDnsSdServiceInfo serviceInfo);

        void saveExitOrEnterChatMsg(String chatId, String uid, String username, String usergender, int type, String messageId, String content);

        boolean handleReceiveSystemMsg(InputStream ins, InetSocketAddress sockAddr, WifiP2pEntity meInfo, ArrayList<WifiP2pEntity> peerInfoList);

        boolean handleReceiveChatMsg(InputStream ins, int chatType, InetSocketAddress sockAddr, WifiP2pEntity meInfo, ArrayList<WifiP2pEntity> peerInfoList);

    }

    interface View extends BaseView {

        //Connected to the current search to the equipment
        void connectDevice(WifiP2pDevice resourceType);

        void discoverPeer();

        void onGroupInfoAvailableConnect(WifiP2pGroup group, WifiP2pDevice device);

        void onGroupInfoAvailableUpdateList(WifiP2pGroup group);

        void onGroupInfoAvailable(WifiP2pGroup group);

        void onConnectFailure(int reason);

        void hasConnected(boolean hasConnect);

        void updateRecvPeerList(ArrayList<WifiP2pEntity> peerInfoList);

        void updateNewPeopleState(WifiP2pEntity vo, boolean isCome);

        void handleMsgCommend(String msg);

        void handleBroadcastPeerList(WifiP2pEntity newuser);

        void handleFileCommend(String msgid, int commend);

        void postSendBytes(long currentLength, String msgid);

        void postSendMsgResult(boolean successed, int state, String msgid);
    }
}
