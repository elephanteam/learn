package com.limit.learn.wifi.entity;


import android.net.wifi.p2p.WifiP2pInfo;

import com.limit.learn.base.BaseApplication;

import org.json.JSONObject;

import java.util.UUID;

public class WifiP2pEntity {

	/**
	 * 传输的内容
	 * */
	private JSONObject infoJson;

	/**
	 * 默认的 uid  仅仅是为了测试
	 * */
	private String wifiUid;

	/**
	 * wifi ip地址
	 * */
	private String host;

	/**
	 * wifi 端口号
	 * */
	private int port;

	/**
	 * 设备地址
	 * */
	private String deviceAddress;

	private WifiP2pInfo info;

	/**
	 * 是否是wifi组的主人
	 * */
	private boolean isOwner;

	/**
	 * wifi的连接状态
	 * */
	private int status;

	/**
	 * 是否是无网传输
	 * */
    private boolean isOnLine;//If no network online

	public JSONObject getInfoJson() {
		return infoJson;
	}

	public void setInfoJson(JSONObject infoJson) {
		this.infoJson = infoJson;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	public WifiP2pInfo getInfo() {
		return info;
	}

	public void setInfo(WifiP2pInfo info) {
		this.info = info;
	}

	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean owner) {
		isOwner = owner;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isOnLine() {
		return isOnLine;
	}

	public void setOnLine(boolean onLine) {
		isOnLine = onLine;
	}

	public String getWifiUid() {
		return BaseApplication.wifiUid;
	}
}
