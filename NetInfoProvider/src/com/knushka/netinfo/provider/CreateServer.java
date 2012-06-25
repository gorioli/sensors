package com.knushka.netinfo.provider;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.knushka.netinfo.GlobalConstants;
import com.knushka.netinfo.NetInfoType;

public class CreateServer implements Runnable {

	Handler mainHandler;
	private static ServerSocket welcomeSocket;

	/* maps each producer by ip address to the types it can provide */
	private HashMap<InetAddress, ArrayList<NetInfoType>> hmIpToTypes;

	int providerId;
	NetinfoData netinfoData;

	List<ServerHandlesClient> listClientThreads;

	public CreateServer(NetinfoData netinfoData, int providerId, Handler mainHandler) {
		this.netinfoData = netinfoData;
		this.providerId = providerId;
		this.mainHandler = mainHandler;
		this.listClientThreads = new ArrayList<ServerHandlesClient>();

		try {
			welcomeSocket = new ServerSocket(com.knushka.netinfo.GlobalConstants.TCP_SERVER_PORT);
		} catch (IOException e) {
			Log.w(Global.LOG, "Exception15: " + e.getMessage());
		}

		Log.w(Global.LOG, "--- Server thread was created");
	}

	@Override
	public void run() {

		Socket clientSocket = null;
		Thread newClientThread;

		while (true) {
			try {
				Log.w(Global.LOG, "start to accept");

				clientSocket = welcomeSocket.accept();

				Log.w(Global.LOG, clientSocket.getRemoteSocketAddress() + " - was accepted");

				Map<String, ServerHandlesClient> hmAddressIp2ServerHandlesClient = new HashMap<String, ServerHandlesClient>();
				String key = clientSocket.getInetAddress().toString();
				ServerHandlesClient serverHandlesClient;
				if (!hmAddressIp2ServerHandlesClient.containsKey(key)) {
					serverHandlesClient = new ServerHandlesClient(this.netinfoData, this.providerId, this.mainHandler, clientSocket);
					newClientThread = new Thread(serverHandlesClient);
					newClientThread.start();
					hmAddressIp2ServerHandlesClient.put(key, serverHandlesClient);
				} else {
					serverHandlesClient = hmAddressIp2ServerHandlesClient.get(key);
//					serverHandlesClient. ???????????????????????????????????
				}
			} catch (Exception e) {
				Log.w(Global.LOG, "Exception20: " + e.getMessage());
			}
		}
	}

	private void printLine(String str) {
		Message msg = Message.obtain();
		msg.obj = str;
		mainHandler.sendMessage(msg);
	}
}