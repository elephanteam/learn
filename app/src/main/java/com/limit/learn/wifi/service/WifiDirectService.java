package com.limit.learn.wifi.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.limit.learn.util.Utils;
import com.limit.learn.wifi.WifiConstant;
import com.limit.learn.wifi.direct.ThreadPoolManager;
import com.limit.learn.wifi.direct.WiFiDirectBroadcastReceiver;
import com.limit.learn.wifi.direct.contract.WifiDirectServiceContract;
import com.limit.learn.wifi.direct.presenter.WifiDirectServicePresenter;
import com.limit.learn.wifi.direct.send.SendPeerInfoRunnable;
import com.limit.learn.wifi.direct.util.DirectExtra;
import com.limit.learn.wifi.entity.WifiP2pEntity;
import com.limit.learn.wifi.listener.WifiP2pNetServiceListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class WifiDirectService extends Service implements ChannelListener, WifiP2pNetServiceListener, WifiDirectServiceContract.View {
    private boolean retryChannel = false;
    private NetServiceBinder mBinder = new NetServiceBinder();
    private ThreadPoolManager serviceThread = null;
    private WifiP2pManager manager = null;
    private Channel channel = null;
    private BroadcastReceiver receiver = null;
    private IntentFilter intentFilter = new IntentFilter();
    private ArrayList<WifiP2pEntity> peerInfoList = new ArrayList<>();// Connect the user's information
    private boolean isConnecting = false;// Are connected, only to ensure the same time try to connect a device
    private boolean hasConnected = false;  //Connected devices connectors he try
    private WifiP2pEntity meInfo;// All of the information your current equipment
    private boolean isWifiP2pEnabled = false;
    private WifiP2pDnsSdServiceInfo serviceInfo;
    private boolean hasInit = false;//The connection is successful a

    private WifiDirectServicePresenter mPresenter;

    private Timer mConnectTimer;
    private TimerTask mConnectTimerTask;

    private Timer mDiscoverTimer;
    private TimerTask mDiscoverTimerTask;

    /**
     * 聊天类型
     * */
    private int chatType;

    public boolean isWifiP2pAviliable() {
        return manager != null;
    }

    public boolean isWifiP2pManager() {
        return manager != null;
    }

    public boolean isWifiP2pChannel() {
        return channel != null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInfo();
        meInfo.setOwner(true);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mPresenter = new WifiDirectServicePresenter(this);
        receiver = new WiFiDirectBroadcastReceiver(this, this);
        registerReceiver(receiver, intentFilter);
        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            if (mWifiManager != null && !mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //创建WifiP2pManager
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        //获取通道
        channel = manager.initialize(this, getMainLooper(), this);
        //更改wifi名称 方便区别
        mPresenter.renameDeviceName(manager,channel);
        //创建一个Bonjour服务信息对象。
        serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("learn", "_learn.ipp._tcp", null);
        new Handler().postDelayed(() -> {
            if (manager != null && channel != null){
                if (serviceInfo != null){
                    //加入本地服务
                    mPresenter.registerP2pService(manager,channel,serviceInfo);
                }
                //注册监听服务
                mPresenter.setServiceResponse(manager,channel);
                //查找服务
                mPresenter.setServiceListeners(manager,channel);
            }
        },1000);
        startDiscoverTimer();
        try {
            serviceThread = new ThreadPoolManager(this, WifiConstant.LISTEN_PORT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initServiceThread() {
        if (serviceThread != null) {
            serviceThread.init();
        }
    }

    private void uninitServiceThread() {
        if (serviceThread != null) {
            serviceThread.uninit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceThread != null) {
            serviceThread.destory();
        }
        peerInfoList.clear();
        stopConnectTimer();
        stopDiscoverTimer();
        try {
            if (manager != null && channel != null){
                manager.stopPeerDiscovery(channel,null);
                if (serviceInfo != null){
                    mPresenter.removeP2pService(manager,channel,serviceInfo);
                }
                mPresenter.removeServiceListeners(manager,channel);
                mPresenter.cancelDisconnect(manager,channel);
                mPresenter.removeGroup(manager,channel);

                manager = null;
                channel = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
        isConnecting = false;
        hasConnected = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onChannelDisconnected() {
        if (isWifiP2pManager() && !retryChannel) {
            onConnectionInfoInvalid();
            retryChannel = true;
            if (manager != null){
                channel = manager.initialize(this, getMainLooper(), this);
            }
        } else {
            try {
                stopSelf();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResult(Object result) {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {

    }

    @Override
    public void updateThisDevice(WifiP2pDevice device) {

    }

    public class NetServiceBinder extends Binder {
        public WifiDirectService getService() {
            return WifiDirectService.this;
        }
    }

    final public void setIsWifiP2pEnabled(boolean isEnabled) {
        this.isWifiP2pEnabled = isEnabled;
        if (isWifiP2pEnabled) {
            initServiceThread();
        } else {
            uninitServiceThread();
        }
    }


    /*Connected to the current search to the equipment*/
    @Override
    public void connectDevice(final WifiP2pDevice device){
        if(!isConnecting && !hasConnected){
            startConnectTimer();
            isConnecting = true;
            if (manager != null && channel != null){
                mPresenter.requestGroupInfo(manager,channel,device,0);
            }
        }
    }


    // Locate near peer device
    @Override
    public void discoverPeer() {
        if (isWifiP2pEnabled && mPresenter != null && manager != null && channel != null) {
            mPresenter.discoverPeers(manager,channel);
        }
    }


    /**
     * 连接成功
     * */
    @Override
    public void onGroupInfoAvailableConnect(WifiP2pGroup group,WifiP2pDevice device) {
        try {
            hasConnected = false;
            if(group == null || group.getClientList()==null || group.getClientList().size()<=0){
                meInfo.setStatus(WifiP2pDevice.AVAILABLE);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (manager != null && channel != null){
                    mPresenter.connect(manager,channel,config);
                }
            }else{
                meInfo.setOwner(group.isGroupOwner());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        /**
         * 连接成功
         * */
        @Override
        public void onGroupInfoAvailableUpdateList(WifiP2pGroup group) {
        if (group != null){
            Log.e("***********","onGroupInfoAvailableUpdateList group.getNetworkName() = " + group.getNetworkName() + "-----group.getClientList().size() =" + group.getClientList().size());
        }
        try {
            hasConnected = false;
            for (int i = 0; i < peerInfoList.size(); i++) {
                WifiP2pEntity vo = peerInfoList.get(i);
                boolean hasFindPeer = false;
                if(group.getClientList()!=null){
                    for (WifiP2pDevice device : group.getClientList()) {
                        if (device.deviceAddress.equals(vo.getDeviceAddress())) {
                            hasFindPeer = true;
                            break;
                        }
                    }
                }
                if (!hasFindPeer) {
                    updateNewPeopleState(vo,false);
                    peerInfoList.remove(i);
                    i--;
                    postRecvPeerList(peerInfoList);
                }
            }
            postRecvPeerList(peerInfoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {
        try {
            hasConnected = false;
            if(group==null){//The group does not exist
                if (meInfo == null) {
                    meInfo = new WifiP2pEntity();
                }
                meInfo.setStatus(WifiP2pDevice.AVAILABLE);// Disconnection or connection failure reset connection status
                if(hasInit){
                    new Handler().postDelayed(() -> {
                        //execute the task
                        if (manager != null && channel != null){
                            if (serviceInfo != null){
                                mPresenter.registerP2pService(manager,channel,serviceInfo);//Disconnect to re-register
                            }
                            mPresenter.discoverService(manager,channel);//To search for other equipment services
                        }
                    }, (long) (Math.random() * 5000)); //Random search time to start, there is a sequence, avoid period began to search at the same time
                }
            }else{
                Log.e("***********","onGroupInfoAvailable  group.getNetworkName() = " + group.getNetworkName() + "-----group.getClientList().size() =" + group.getClientList().size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectFailure(int reason) {
        if (reason != 2){
            onConnectionInfoInvalid();
        }
    }

    @Override
    public void hasConnected(boolean hasConnect) {
        hasConnected = hasConnect;
    }

    @Override
    public void updateRecvPeerList(ArrayList<WifiP2pEntity> peerInfoList) {
        this.peerInfoList = peerInfoList;
        postRecvPeerList(peerInfoList);
    }

    @Override
    public void updateNewPeopleState(WifiP2pEntity vo, boolean isCome) {

    }

    @Override
    public void handleMsgCommend(String msg) {
        Intent intent = new Intent(WifiConstant.WIFI_DIRECT_OFFLINE_MSG);
        intent.putExtra(DirectExtra.wifiMessage,msg);
        Utils.sendBroadcastReceiver(WifiDirectService.this,intent);
    }

    @Override
    public void handleBroadcastPeerList(WifiP2pEntity wifiP2pEntity) {
        if(meInfo.getHost()==null){
            setLocalData(null);
        }
        for (WifiP2pEntity vo : peerInfoList) {
            if(wifiP2pEntity.getWifiUid().equals(vo.getWifiUid())) {
                serviceThread.execute(new SendPeerInfoRunnable(vo.getHost(), vo.getPort(),this, meInfo));
                for (WifiP2pEntity object : peerInfoList) {
                    if(!object.getHost().equals(vo.getHost())) {
                        serviceThread.execute(new SendPeerInfoRunnable(vo.getHost(), vo.getPort(), this, object));
                    }
                }
            }else{
                serviceThread.execute(new SendPeerInfoRunnable(vo.getHost(), vo.getPort(), this, wifiP2pEntity));
            }
        }
    }

    @Override
    public void handleFileCommend(String msgid, int commend) {

    }

    @Override
    public void postSendBytes(long currentLength, String msgid) {

    }

    @Override
    public void postSendMsgResult(boolean successed, int state, String msgid) {

    }

    // The connection fails
    public void onConnectionInfoInvalid() {
        if (meInfo == null) {
            meInfo = new WifiP2pEntity();
        }
        isConnecting = false;
        try {
            if (manager == null){
                manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
                channel = manager.initialize(this, getMainLooper(), this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (mPresenter != null && manager != null && channel != null ){
            mPresenter.requestGroupInfo(manager,channel,null,1);
        }
    }


    public void requestPeers(WifiP2pManager.PeerListListener listener) {
        if (manager != null && channel != null ){
            manager.requestPeers(channel, listener);
        }
    }

    public void requestConnectionInfo(WifiP2pManager.ConnectionInfoListener listener) {
        if (manager != null && channel != null ){
            manager.requestConnectionInfo(channel, listener);
        }
    }


    public List<WifiP2pEntity> getwifiPeopleList() {
        if (peerInfoList != null && peerInfoList.size() <= 0){
            hasConnected = false;
        }
        return peerInfoList;
    }

    public WifiP2pEntity getwifiMeInfo(){
        return meInfo;
    }

    // System method to update the current peer device list
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        if (peers != null && peers.getDeviceList() != null){
            List<WifiP2pDevice> p2pDeviceList = new ArrayList<>(peers.getDeviceList());
            boolean hasGo = false;
            for (int i = 0 ; i < p2pDeviceList.size(); i++){
                if (p2pDeviceList.get(i).isGroupOwner() && p2pDeviceList.get(i).deviceName.length() <= 20){
                    hasGo = true;
                    connectDevice(p2pDeviceList.get(i));
                    break;
                }
            }
            if(!hasGo){
                for (int i = 0 ; i < p2pDeviceList.size(); i++){
                    if (p2pDeviceList.get(i).deviceName.length() <= 20 && p2pDeviceList.get(i).status != 0 ){
                        connectDevice(p2pDeviceList.get(i));
                        break;
                    }
                }
            }
            updateOnLineList(p2pDeviceList);
        }
    }

    // refresh the current online users delete only for groupowner, refresh the online users because if the client is probably less than online users
    //, so it is not accurate, online users will need to list update server notification
    private void updateOnLineList(List<WifiP2pDevice> p2pDeviceList) {
        if (meInfo != null && meInfo.isOwner() && mPresenter != null &&  manager != null && channel != null ){// groupowner
            mPresenter.requestGroupInfo(manager,channel,null,2);
        }

    }

    // 获取到WifiP2pInfo
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if(info==null){
            return;
        }
        hasInit=true;
        isConnecting = false;
        hasConnected = peerInfoList == null || peerInfoList.size() > 0;
        if(!info.isGroupOwner && mPresenter != null && manager != null && channel != null && serviceInfo != null){
            mPresenter.removeP2pService(manager,channel,serviceInfo);
        }
        setInfo();
        setLocalData(info);
        if (!info.isGroupOwner && serviceThread != null) {
            try {
                if (info.groupOwnerAddress != null && info.groupOwnerAddress.getHostAddress() != null){
                    serviceThread.execute(new SendPeerInfoRunnable(info.groupOwnerAddress.getHostAddress(),WifiConstant.LISTEN_PORT, this, meInfo));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化自己信息
     * */
    private void setInfo(){
        if (meInfo == null) {
            meInfo = new WifiP2pEntity();
        }
    }

    /**
     * 设置IP port和其他信息
     * */
    private void setLocalData(WifiP2pInfo info){
        if(info!=null && info.isGroupOwner){
            meInfo.setHost(info.groupOwnerAddress.getHostAddress());
        }else{
            String ip = getLocalIpAddress();
            meInfo.setHost(ip);
        }
        meInfo.setPort(WifiConstant.LISTEN_PORT);
        if(info!=null){
            meInfo.setStatus(WifiP2pDevice.CONNECTED);// The connected
            meInfo.setInfo(info);
            meInfo.setOwner(info.isGroupOwner);
        }else{
            meInfo.setOwner(false);
            meInfo.setStatus(WifiP2pDevice.AVAILABLE);
        }
    }


    // Open the picture
    public InputStream getInputStream(Uri uri) throws FileNotFoundException {
        ContentResolver cr = getContentResolver();
        return cr.openInputStream(uri);
    }


    // Get the current IP address Note: if there is no connection to the peer-to-peer networks inside, there is no IP address
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address){
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void startConnectTimer(){
        stopConnectTimer();
        mConnectTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(isConnecting){
                    onConnectionInfoInvalid();
                }
            }
        };
        mConnectTimer = new Timer();
        mConnectTimer.schedule(mConnectTimerTask, 1000 * 20, 5000);
    }

    private void stopConnectTimer(){
        if(mConnectTimer != null){
            mConnectTimer.cancel();
            mConnectTimer = null;
        }
        if(mConnectTimerTask != null){
            mConnectTimerTask.cancel();
            mConnectTimerTask = null;
        }
    }

    /**
     * 开始搜索
     * */
    private void startDiscoverTimer(){
        stopDiscoverTimer();
        mDiscoverTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(!hasConnected){
                    discoverPeer();
                }
            }
        };
        mDiscoverTimer = new Timer();
        mDiscoverTimer.schedule(mDiscoverTimerTask, 10000 * 6, 5000 * 6);
    }

    private void stopDiscoverTimer(){
        if(mDiscoverTimer != null){
            mDiscoverTimer.cancel();
            mDiscoverTimer = null;
        }
        if(mDiscoverTimerTask != null){
            mDiscoverTimerTask.cancel();
            mDiscoverTimerTask = null;
        }
    }


    private SocketAddress remoteSockAddr;

    public void setRemoteSockAddress(SocketAddress sockAddr) {
        remoteSockAddr = sockAddr;
    }

    public SocketAddress getRemoteSockAddress() {
        return remoteSockAddr;
    }

    /**
     * The receiving system messages at present mainly is the new user information and user exit
     * 目前接收系统消息主要是新用户信息和用户出口
     * */
    public boolean handleRecvSystemMsg(InputStream ins,InetSocketAddress sockAddr) {
        return mPresenter.handleReceiveSystemMsg(ins,sockAddr,meInfo,peerInfoList);
    }

    /**
     * Receive chat messages group and single chat messages
     * 接收聊天消息组和单个聊天消息
     * @param chatType
     * 	 * 聊天类型
     * 	 * 0.      单聊
     * 	 * 1.      everyone            everyone聊天室里使用
     * 	 * 2.      tango_offline       纯无网聊天室使用 everyone下级页面
     *
     * */
    public boolean handleRecvChatMsg(InputStream ins, int chatType, InetSocketAddress sockAddr) {
        return mPresenter.handleReceiveChatMsg(ins,chatType,sockAddr,meInfo,peerInfoList);
    }

    // For the activity on latest online users list
    public void postRecvPeerList(ArrayList<WifiP2pEntity> peerlist) {
        Intent intent = new Intent();
        intent.putExtra("onLineMember",peerlist);
        intent.setAction(WifiConstant.WIFI_DIRECT_OFFLINE_MEMBER_LIST);
        Utils.sendBroadcastReceiver(this,intent);
    }
}
