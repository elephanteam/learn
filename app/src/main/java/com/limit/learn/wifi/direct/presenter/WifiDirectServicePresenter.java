package com.limit.learn.wifi.direct.presenter;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

import com.limit.learn.base.BasePresenterImpl;
import com.limit.learn.wifi.direct.contract.WifiDirectServiceContract;
import com.limit.learn.wifi.entity.WifiP2pEntity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.UUID;

public class WifiDirectServicePresenter extends BasePresenterImpl<WifiDirectServiceContract.View> implements WifiDirectServiceContract.Presenter{

    private WifiDirectServiceContract.View mView;

    public WifiDirectServicePresenter(WifiDirectServiceContract.View view) {
        super(view);
        this.mView = view;
    }

    /**
     * 2.加入本地服务
     * 添加本地服务，发送服务信息，网络渠道和用来表示成功或失败的监听器请求。
     * @param manager            WifiPepManager
     * @param channel            WifiP2pManager channel
     * @param serviceInfo        WifiP2pDnsSdServiceInfo
     * */
    @Override
    public void registerP2pService(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pDnsSdServiceInfo serviceInfo) {
        manager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e("***********","registerP2pService");
            }

            @Override
            public void onFailure(int arg0) {
                Log.e("***********","registerP2pService onFailure" + arg0);
            }
        });
    }

    /*
     * 3.注册监听服务
     * 可发现其他设备的信息
     * @param manager             WifiPepManager
     * @param channel             WifiP2pManager channel
     * */
    @Override
    public void setServiceResponse(WifiP2pManager manager, WifiP2pManager.Channel channel){
        WifiP2pManager.DnsSdServiceResponseListener servListener = (instanceName, registrationType, resourceType) -> {
            if("learn".equals(instanceName)){//连接服务
                if (resourceType.isGroupOwner()){
                    mView.connectDevice(resourceType);
                }else{
                    mView.discoverPeer();
                }
            }
        };
        manager.setDnsSdResponseListeners(channel, servListener, null);
    }


    /**
     * 必须先进行addServiceRequest才能进行查找服务
     * @param manager             WifiPepManager
     * @param channel             WifiP2pManager channel
     * */
    @Override
    public void setServiceListeners(final WifiP2pManager manager, final WifiP2pManager.Channel channel) {
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("***********","addServiceRequest");
                        discoverService(manager,channel);
                    }

                    @Override
                    public void onFailure(int code) {
                        Log.e("***********","addServiceRequest onFailure" + code);
                    }
                });
    }

    /**
     * 4.查找服务
     * @param manager             WifiPepManager
     * @param channel             WifiP2pManager channel
     * */
    @Override
    public void discoverService(WifiP2pManager manager,WifiP2pManager.Channel channel) {
        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e("***********","discoverService");
            }
            @Override
            public void onFailure(int code) {
                Log.e("***********","discoverService onFailure" + code);
            }
        });
    }

    /**
     * 5.连接服务
     * 在注册监听的时候当发现服务可用时会回调onDnsSdServiceAvailable方法中获取到WifiP2pDevice对象
     * @param manager             WifiPepManager
     * @param channel             WifiP2pManager channel
     * @param config              WifiP2pConfig
     * */
    @Override
    public void connect(WifiP2pManager manager, WifiP2pManager.Channel channel,WifiP2pConfig config) {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e("***********","connect");
            }

            @Override
            public void onFailure(int reason) {
                mView.onConnectFailure(reason);
                Log.e("***********","connect onFailure" + reason);
            }
        });
    }


    /**
     * 删除本地服务
     * @param manager             WifiPepManager
     * @param channel             WifiP2pManager channel
     * @param serviceInfo         WifiPepDnsSdServiceInfo
     * */
    @Override
    public void removeP2pService(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pDnsSdServiceInfo serviceInfo) {
        manager.removeLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int arg0) {

            }
        });
    }

    /**
     * 一个应用可以初始化被发现的热点是通过discoverPeers(Wifip2pManager.channel,Wifip2pManager.ActionListener).
     * 一个已经被初始化的Peer来自从设备开始连接热点后应用在活动期间的请求，会组成一个Peer组或者这里有一个明确的stopPeerDiscovery(Wifip2pManager.channel,Wifip2pManager.ActionListen),
     * App可以监听wifi_p2p_discovery_changed_Action去知道一个p2p是否在运行或者停止工作，通常地，wifi_p2p_peers_changed_action可以说明peer列表的改变
     * @param manager             WifiPepManager
     * @param channel             WifiP2pManager channel
     * */
    @Override
    public void discoverPeers(WifiP2pManager manager,WifiP2pManager.Channel channel) {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e("***********","discoverPeers");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.e("***********","discoverPeers onFailure" + reasonCode);
            }
        });
    }


    /**
     * 更改设备名称
     * @param manager       WifiPepManager
     * @param channel       WifiP2pManager channel
     * */
    @Override
    public void renameDeviceName(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        Method method;
        try {
            method = manager.getClass().getMethod("setDeviceName", WifiP2pManager.Channel.class, String.class, WifiP2pManager.ActionListener.class);
            method.invoke(manager, channel, android.os.Build.BRAND + UUID.randomUUID(),new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    Log.e("***********","renameDeviceName");
                }

                public void onFailure(int reason) {
                    Log.e("***********","renameDeviceName onFailure" + reason);
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove ServiceListeners
     * @param manager        WifiPepManager
     * @param channel        WifiP2pManager channel
     * */
    @Override
    public void removeServiceListeners(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.removeServiceRequest(channel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int code) {

                    }
                });
    }

    /**
     * 取消连接
     * @param manager         WifiPepManager
     * @param channel         WifiP2pManager channel
     * */
    @Override
    public void cancelDisconnect(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reasonCode) {

            }
        });
    }

    /**
     * 移除群组
     * @param manager             WifiPepManager
     * @param channel             WifiP2pManager channel
     * */
    @Override
    public void removeGroup(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {

            }

            @Override
            public void onSuccess() {

            }
        });

    }

    /**
     * 请求组信息
     * @param manager             WifiPepManager
     * @param channel             WifiP2pManager channel
     * @param device              WifiP2pDevice
     * @param type                0  connect device   1  normal   2 update list
     * */
    @Override
    public void requestGroupInfo(WifiP2pManager manager, WifiP2pManager.Channel channel, final WifiP2pDevice device, final int type) {
        try {
            manager.requestGroupInfo(channel, group -> {
                if (device != null){
                    mView.onGroupInfoAvailableConnect(group,device);
                }else if (type == 1){
                    mView.onGroupInfoAvailable(group);
                }else{
                    mView.onGroupInfoAvailableUpdateList(group);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * The receiving system messages at present mainly is the new user information and user exit
     * @param ins               inputStream
     * @param sockAddr         InetSocketAddress
     * @param meInfo           WifiP2pEntity
     * @param peerInfoList     user info list
     */
    @Override
    public boolean handleReceiveSystemMsg(InputStream ins, InetSocketAddress sockAddr,WifiP2pEntity meInfo,ArrayList<WifiP2pEntity> peerInfoList) {
        try {
            mView.hasConnected(true);
            int iSize = ins.read();
            byte[] buffer = new byte[iSize];
            int len = ins.read(buffer, 0, iSize);
            String strBuffer = new String(buffer, 0, len);
            int jsonLength = Integer.parseInt(strBuffer);
            byte[] jsonBuf = new byte[jsonLength];
            len = ins.read(jsonBuf, 0, jsonLength);
            String json = new String(jsonBuf, "utf-8");
            Log.e("********************",json);
            mView.updateRecvPeerList(peerInfoList);
            mView.handleMsgCommend(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Receive chat messages group and single chat messages
     * @param ins               inputStream
     * @param sockAddr         InetSocketAddress
     * @param meInfo           WifiP2pEntity
     * @param peerInfoList     user info list
     * @param chatType
     * 	聊天类型
     * 	0.      单聊
     * */
    @Override
    public boolean handleReceiveChatMsg(InputStream ins, int chatType, InetSocketAddress sockAddr,WifiP2pEntity meInfo,ArrayList<WifiP2pEntity> peerInfoList) {
        try {
            int jsonLengthLength = ins.read();// jsonLengthLength
            byte[] buffer = new byte[jsonLengthLength];
            int len = ins.read(buffer, 0, jsonLengthLength);
            String strBuffer = new String(buffer, 0, len);
            int jsonLength = Integer.parseInt(strBuffer);// json length
            byte[] jsonBuf = new byte[jsonLength];// The length of the json must be less than 255
            len = ins.read(jsonBuf, 0, jsonLength);
            String json = new String(jsonBuf, 0, len);
            Log.e("********************",json);
            mView.updateRecvPeerList(peerInfoList);
            mView.handleMsgCommend(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void saveExitOrEnterChatMsg(String chatId, String uid, String username, String usergender, int type, String messageId, String content) {

    }


    @Override
    protected void attachModel() {

    }

    @Override
    protected void detachModel() {

    }

}
