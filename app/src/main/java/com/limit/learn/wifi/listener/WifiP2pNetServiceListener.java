package com.limit.learn.wifi.listener;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;


public interface WifiP2pNetServiceListener extends ConnectionInfoListener, PeerListListener {

	void updateThisDevice(WifiP2pDevice device);

}
