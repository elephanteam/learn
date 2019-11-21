package com.limit.learn.wifi.direct;

import com.limit.learn.wifi.WifiConstant;
import com.limit.learn.wifi.service.WifiDirectService;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class HandleAcceptSocket implements Runnable {
    private final Socket socket;
    private final WifiDirectService netService;

    HandleAcceptSocket(WifiDirectService service, Socket socket) {
        this.netService = service;
        this.socket = socket;
    }

    public void closeSocket() {
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        try {
            InputStream ins = socket.getInputStream();
            int iCommand = ins.read();
            InetSocketAddress sockAddr = (InetSocketAddress) socket.getRemoteSocketAddress();
            if (iCommand == WifiConstant.COMMAND_ID_SEND_TYPE_SYSTEM) {
                netService.handleRecvSystemMsg(ins,sockAddr);
            }else if (iCommand == WifiConstant.COMMAND_ID_SEND_TYPE_NORMAL_CHAT) {
                netService.handleRecvChatMsg(ins, 0,sockAddr);
            }else if (iCommand == WifiConstant.COMMAND_ID_SEND_TYPE_ROOM_CHAT) {
                netService.handleRecvChatMsg(ins, 1,sockAddr);
            }
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

