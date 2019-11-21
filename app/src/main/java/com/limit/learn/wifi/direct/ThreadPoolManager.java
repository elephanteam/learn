package com.limit.learn.wifi.direct;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

import com.limit.learn.wifi.WifiConstant;
import com.limit.learn.wifi.service.WifiDirectService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ThreadPoolManager extends HandlerThread {

	private final ServerSocket serverSocket;
	private final ExecutorService pool;
	private final WifiDirectService netService;
	private final static String TAG = "ServiceThread";
	private boolean isServiceRun = true;
	private Handler handler = null;

	public ThreadPoolManager(WifiDirectService service, int port)throws Exception {
		super(TAG, Process.THREAD_PRIORITY_FOREGROUND);
		this.netService = service;
		serverSocket = new ServerSocket(port);
		pool = Executors.newCachedThreadPool();
	}

	public Handler getHandler() {
		return handler;
	}

	final void setServiceRun(boolean isRun) {
		this.isServiceRun = isRun; 
	}

	final boolean isServiceRun() {
		return isServiceRun; 
	}

	static private class ServiceThreadHandler extends Handler {

		private ThreadPoolManager sThread;

		ServiceThreadHandler(ThreadPoolManager service) {
			super(service.getLooper());
			this.sThread = service;
		}
		
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WifiConstant.THREAD_POOL_HANDLER_WHAT_10) {
				while (sThread.isServiceRun()) {
					try {
						Socket sock = sThread.serverSocket.accept();
						sThread.pool.execute(new HandleAcceptSocket(sThread.netService, sock));
					} catch (Exception ex) {
						sThread.pool.shutdown();
						break;
					}
				}
			}
			super.handleMessage(msg);
		}
	}
	
	public void init() {
		setServiceRun(true);
		if (!this.isAlive()) {
			this.start();
			handler = new ServiceThreadHandler(this);
		}
		Message msg = new Message();
		msg.what = WifiConstant.THREAD_POOL_HANDLER_WHAT_10;
		getHandler().sendMessage(msg);
	}

	public void uninit() {
		setServiceRun(false);
	}
	
	public void execute (Runnable command) {
		pool.execute(command);
	}

	public void destory() {
		setServiceRun(false);
		shutdownAndAwaitTermination();
		this.quit();
	}
	
	private void shutdownAndAwaitTermination() {
		pool.shutdown();
		try {
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow();
				if (!pool.awaitTermination(60, TimeUnit.SECONDS)){
					System.err.println("Pool did not terminate");
				}
			}
			if (!serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (InterruptedException ie) {
			pool.shutdownNow();
			Thread.currentThread().interrupt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}