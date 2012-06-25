package com.knushka.netinfo.provider;

// A Java representation of the SQL TIMESTAMP type. It provides the capability of representing the SQL TIMESTAMP nanosecond value, in addition to the regular date/time value which has millisecond resolution. 
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import com.knushka.netinfo.NetInfoType;

public class NetinfoData {
	// HI implements Serializable

	private long seqNum = 0;
	private Timestamp timestamp;

	private int streamID;

	private TreeMap<NetInfoType, List<Object>> hmTypeToValues = null;
	LinkedList<Object> tempList;

	public NetinfoData() {
		super();

		hmTypeToValues = new TreeMap<NetInfoType, List<Object>>(new Comparator<NetInfoType>() {
			@Override
			public int compare(NetInfoType o1, NetInfoType o2) {
				return o1.getValue() - o2.getValue();
			};
		});
	}

	public void addValues(NetInfoType type, Object... values) {

		hmTypeToValues.put(type, new LinkedList<Object>());

		for (Object value : values) {
			hmTypeToValues.get(type).add(value);
		}
	}

	public void addValue(NetInfoType type, Object value) {

		if (!hmTypeToValues.containsKey(type))
			tempList = new LinkedList<Object>();
		else
			tempList = (LinkedList<Object>) hmTypeToValues.get(type);
		tempList.add(value);
		hmTypeToValues.put(type, tempList);
	}

//	public int[] getKeysAsInteger() {
//		int[] array = new int[hmTypeToValues.size()];
//
//		int i = 0;
//		for (NetInfoType key : hmTypeToValues.keySet()) {
//			array[i] = key.getValue();
//			i++;
//		}
//
//		return array;
//	}

	// public char[] getKeysAsChar() {
	// char[] array = new char[hmTypeToValues.size()];
	//
	// int i = 0;
	// for (NetInfoType key : hmTypeToValues.keySet()) {
	// array[i] = (char) (key.getValue()+ 48);
	// i++;
	// }
	//
	// return array;
	// }

	public String getKeysAsString() {
		StringBuilder strBuilder = new StringBuilder();

		for (NetInfoType key : hmTypeToValues.keySet()) {
			strBuilder.append(key.getValue());
			strBuilder.append(",");
		}

		return strBuilder.toString();
	}

	public void prepareToSend() {
		if (this.seqNum == 0)
			this.seqNum = getNextSequanceNum();

		this.timestamp = new Timestamp(new Date().getTime());

	}

	public long getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(long seqNum) {
		if (this.seqNum == 0)
			this.seqNum = seqNum;
	}

	private static long counter;

	private static long getNextSequanceNum() {
		counter++;
		return counter;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public int size() {
		return this.hmTypeToValues.size();
	}

	// public void setTimestamp() {
	// this.timestamp = new Timestamp(new Date().getTime());
	// }

	@Override
	public String toString() {
		StringBuffer messageBuff = new StringBuffer();

		// // set type
		// messageBuff.append(this.type);
		// messageBuff.append(",");
		//
		// // set sequence number
		// messageBuff.append(this.seqNum);
		// messageBuff.append(",");
		//
		// // set timestamp
		// messageBuff.append(this.timestamp);

		return messageBuff.toString();
	}
}
