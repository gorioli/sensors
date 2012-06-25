package com.knushka.netinfo.consumer.tasks;

//A Java representation of the SQL TIMESTAMP type. It provides the capability of representing the SQL TIMESTAMP nanosecond value, in addition to the regular date/time value which has millisecond resolution. 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;

import android.os.Message;
import android.util.Log;

import com.knushka.netinfo.GlobalConstants;
import com.knushka.netinfo.consumer.Global;
import com.knushka.netinfo.consumer.InfoConsumerActivity;
import com.knushka.netinfo.consumer.R;

public class CreateClientTask implements Runnable {

	String serverInetAddressStr;
	int producerId;

	// --- TCP ---
	private final int tcpPort;
	Socket tcpSocket;

	PrintWriter outToServer;
	BufferedReader inFromServer;

	// --- UDP ---
	private int clientUdpPort_M;
	private int serverUdpPort_L;
	DatagramSocket clientUdpSocket;

	// -----------
	private InfoConsumerActivity mActivity;
	boolean[] chosenDataTypes;
	int requestId;
	private boolean isStop;

	public CreateClientTask(String inetAddress, InfoConsumerActivity activity) {
		this.serverInetAddressStr = inetAddress;
		this.mActivity = activity;
		isStop = false;

		this.tcpPort = com.knushka.netinfo.GlobalConstants.TCP_SERVER_PORT;
	}

	public void startTaskAgain(String inetAddress, InfoConsumerActivity activity) {
		this.serverInetAddressStr = inetAddress;
		this.mActivity = activity;
		isStop = false;
	}

	@Override
	public void run() {
		// BufferedReader inFromUser = new BufferedReader(new
		// InputStreamReader(System.in));
		while (!isStop) {

			if (tcpCommunicate()) {

				while (!isStop) {

					goToWait();

					for (int trials = 10; trials > 0; trials--) { // FIXME think of what count trials is
						if (udpCommunicate())
							break;
					}
				}
			}
		}
	}

	private void goToWait() {
		Log.w(Global.LOG, "Task " + this.serverInetAddressStr + " is going to wait");
		do {
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					Log.w(Global.LOG, "Error13: " + e.getMessage() + " for:" + serverInetAddressStr);
				}
			}
		} while (!mActivity.getIpAddress().equals(this.serverInetAddressStr)); // HI
		Log.w(Global.LOG, "task woke up 2");
	}

	private boolean tcpCommunicate() {
		try {
			// 0) create udp connection, retrieve M port:
			clientUdpSocket = new DatagramSocket();
			this.clientUdpPort_M = clientUdpSocket.getLocalPort();

			// 1) create tcp connection, send ConsumerId & udp port M:
			tcpSocket = new Socket(serverInetAddressStr, tcpPort);
			printLine("the Socket(" + serverInetAddressStr + ":" + tcpPort + ") was set");

			outToServer = new PrintWriter(tcpSocket.getOutputStream());

			outToServer.println(mActivity.getConsumerId());
			outToServer.println(this.clientUdpPort_M);
			outToServer.flush();
			printLine(mActivity.getConsumerId() + ", " + clientUdpPort_M + " were sent");

			// 3) read providerId, udp port L, info types:
			String keys = null;
			inFromServer = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
			this.producerId = Integer.parseInt(inFromServer.readLine());
			this.serverUdpPort_L = Integer.parseInt(inFromServer.readLine());
			printLine(this.producerId + ", " + this.serverUdpPort_L + " were read");

			keys = inFromServer.readLine();
			printLine(keys + " were read");

			Message msg = Message.obtain();
			msg.arg1 = Global.INFO_TYPES_RECEIVED; // results to call handleAvailableTypes() function
			msg.obj = this.serverInetAddressStr + "," + keys;
			mActivity.getMainHandler().sendMessage(msg);

			tcpSocket.close();
			printLine("Client socket is closed");
		} catch (Exception e) {
			Log.w(Global.LOG, "Error1: " + e.getMessage() + " for:" + serverInetAddressStr);
			sleepForShortPeriod(10000);
			return false;
		}
		return true;
	}

	private void sleepForShortPeriod(int period) {
		try {
			synchronized (this) {
				this.wait(period);
			}
		} catch (Exception e2) {
			Log.w(Global.LOG, "Error2: " + e2.getMessage() + " for:" + serverInetAddressStr);
		}
	}

	public boolean udpCommunicate() {

		StringBuilder sb = new StringBuilder();

		// 1) T miliseconds (period),
		sb.append(mActivity.getResources().getInteger(R.integer.resending_period_ms));
		sb.append(",");

		// 2) start time Q,
		sb.append(mActivity.getResources().getInteger(R.integer.start_in_ms));
		sb.append(",");

		// 3) int my_current_time
		sb.append(new Date().getTime());
		sb.append(",");

		// 4) is_relative_time
		sb.append(mActivity.getResources().getBoolean(R.bool.is_absolute_time));
		sb.append(",");

		sb.append(this.requestId);
		sb.append(",");

		sb.append(getChosenTypes(this.chosenDataTypes));

		try {

			String messageStr = sb.toString();
			DatagramPacket p = new DatagramPacket(messageStr.getBytes("UTF8"), messageStr.length(), InetAddress.getByName(this.serverInetAddressStr),
					this.serverUdpPort_L);

			this.clientUdpSocket.send(p);

			printLine("sent to " + this.serverInetAddressStr + ":" + this.serverUdpPort_L + ", data: " + messageStr);
		} catch (IOException e) {
			// FIXME to decide what to do if udp communication failed
			Log.w(Global.LOG, "Error3: " + e.getMessage() + " for:" + serverInetAddressStr);
			sleepForShortPeriod(10000);
			return false;
		}
		return true;
	}

	private String getChosenTypes(boolean[] chosenDataTypes) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < chosenDataTypes.length; i++) {
			if (chosenDataTypes[i]) {
				int dataType = this.mActivity.getDataType(i);
				sb.append(dataType);
				sb.append(";");
			}
		}
		if (sb.length() > 0)
			sb.replace(sb.length() - 1, sb.length(), "");
		return sb.toString();
	}

	Message msg;

	private void printLine(String str) {
		msg = Message.obtain();
		msg.obj = str;
		mActivity.getMainHandler().sendMessage(msg);
	}

	public void cancel() {
		this.isStop = true;
	}

	public void setChosenDataTypes(boolean[] chosenDataTypes) {
		this.chosenDataTypes = chosenDataTypes;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
}
