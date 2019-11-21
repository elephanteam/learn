package com.limit.learn.wifi;

public class WifiConstant {

    public static final int LISTEN_PORT = 8988;
    public static final int SOCKET_TIMEOUT = 5000;

    //发送系统消息
    public static final int COMMAND_ID_SEND_TYPE_SYSTEM = 100;
    //发送普通消息
    public static final int COMMAND_ID_SEND_TYPE_NORMAL_CHAT = 101;
    //发送群组消息
    public static final int COMMAND_ID_SEND_TYPE_ROOM_CHAT = 102;

    //线程池内handler
    public static final int THREAD_POOL_HANDLER_WHAT_10 = 10;


    public static final String WIFI_DIRECT_OFFLINE_MEMBER_LIST = "com.limit.learn.offline_member_list";
    public static final String WIFI_DIRECT_OFFLINE_MSG = "com.limit.learn.offline_msg";


}
