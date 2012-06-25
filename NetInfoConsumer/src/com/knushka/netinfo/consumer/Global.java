package com.knushka.netinfo.consumer;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;

import com.knushka.netinfo.GlobalConstants;

import android.util.Log;

public class Global {
	

	public static final int INFO_TYPES_RECEIVED = 1;
	public static final String LOG = "__LOG__";

	private static String SERVERIP = null;
	private static String oldServerIp = null;
	public static long debugSleepTime = 2000;

	public static String getLocalIp() {
		oldServerIp = SERVERIP;
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface networkInterface = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr
								.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						Log.d(Global.LOG, "consumer: IP - " + inetAddress.getHostAddress().toString());
						SERVERIP = inetAddress.getHostAddress().toString();
					}
				}
			}			
		} catch (SocketException ex) {
			Log.d("ServerActivity", ex.toString());
			SERVERIP = null; // "192.168.1.111"
		}		
		return SERVERIP;
	}

	private static HashMap<Integer, Integer> usedRandomNumbers = new HashMap<Integer, Integer>();

	public static int getRandomNumber() {

		Random rnd_gen = new Random();

		while (true) {
			int rnd_num = rnd_gen.nextInt(Integer.MAX_VALUE);

			if (!usedRandomNumbers.containsKey(rnd_num)) {
				usedRandomNumbers.put(rnd_num, null);

				return rnd_num;
			}
		}
	}
}
