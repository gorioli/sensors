package com.knushka.netinfo.provider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.knushka.netinfo.NetInfoRequest;
import com.knushka.netinfo.NetInfoType;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ServerHandlesClient implements Runnable {

	Handler mainHandler;

	int consumerId;
	int providerId;
	private int myUdpPort_L;

	Socket clientSocket;

	private int udpPort_M; // udp port of the consumer / client
	private DatagramSocket udpSocket;

	NetinfoData netinfoData;
	NetInfoRequest request;

	public ServerHandlesClient(NetinfoData netinfoData, int providerId, Handler mainHandler, Socket clientSocket) {
		this.netinfoData = netinfoData;
		this.providerId = providerId;
		this.mainHandler = mainHandler;
		this.clientSocket = clientSocket;
		this.request = new NetInfoRequest();
		
		Log.w(Global.LOG, "-- Client thread was created");
	}

	@Override
	public void run() {

		tcpCommunicate();

		// ------------- udp ---------------
		udpCommunicate();
	}

	private void tcpCommunicate() {
		try {
			PrintWriter out;
			BufferedReader in;

			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			consumerId = Integer.parseInt(in.readLine()); // read
			udpPort_M = Integer.parseInt(in.readLine()); // read
			printLine(consumerId + ", " + udpPort_M + " was read");

			out = new PrintWriter(clientSocket.getOutputStream());

			out.println(providerId); // write
			udpSocket = new DatagramSocket();
			myUdpPort_L = udpSocket.getLocalPort();
			out.println(myUdpPort_L); // write
			printLine(providerId + ", " + myUdpPort_L + " were sent");

			String keys = netinfoData.getKeysAsString();
			out.println(keys); // write
			printLine(keys.toString() + " were sent.");
			out.flush();

		} catch (Exception e) {
			printLine("Error: " + e.getMessage());
			Log.w(Global.LOG, "Error141: " + e.getMessage());
		}
	}

	private void udpCommunicate() {
		try {
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			while (true) { // FIXME think when to terminate this loop
				udpSocket.receive(receivePacket);
				printLine("UDP received: " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());

				String data = null;
				try {
					// data = new String(receivePacket.getData(), "UTF8");
					data = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF8");
					printLine("Message - " + data);
					manageData(data);
				} catch (Exception e) {
					Log.e(Global.LOG, "UnsupportedEncodingException");
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			printLine("Error: " + e.getMessage());
			Log.w(Global.LOG, "Error86: " + e.getMessage());
		}
	}

	private void manageData(String data) {
		String[] parts = data.split(",");

		request.setResendingPeriodMs(Integer.parseInt(parts[0]));
		request.setStartInMs(Integer.parseInt(parts[1]));

		long date = Long.parseLong(parts[2]);
		request.setTimeStamp(new Timestamp(new Date(date).getYear(), new Date(date).getMonth(), new Date(date).getDate(), new Date(date).getHours(), new Date(
				date).getMinutes(), new Date(date).getSeconds(), 0));

		request.setAbsoluteTime(Boolean.parseBoolean(parts[3]));
		request.setRequestId(Integer.parseInt(parts[4]));

		request.setChosenDataTypes(parts[5].split(";"));

		// request.
		// new Timestamp(new Date(parts[2]))

		// boolean[] dataTypes = Boolean.parseBoolean(parts[6]);
		//
		// request.setStartInMs(Integer.parseInt(parts[1]));
	}

	private void printLine(String str) {
		Message msg = Message.obtain();
		msg.obj = str;
		mainHandler.sendMessage(msg);
	}
}
