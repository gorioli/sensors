package com.knushka.netinfo;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class NetInfoRequest {
	private int resendingPeriodMs;
	private int startInMs;
	private Timestamp timeStamp;
	private boolean isAbsoluteTime;

	List<NetInfoType> listNetInfoType;

	public int getResendingPeriodMs() {
		return resendingPeriodMs;
	}

	public void setResendingPeriodMs(int resendingPeriodMs) {
		this.resendingPeriodMs = resendingPeriodMs;
	}

	public int getStartInMs() {
		return startInMs;
	}

	public void setStartInMs(int startInMs) {
		this.startInMs = startInMs;
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean isAbsoluteTime() {
		return isAbsoluteTime;
	}

	public void setAbsoluteTime(boolean isAbsoluteTime) {
		this.isAbsoluteTime = isAbsoluteTime;
	}

	public boolean[] getChosenDataTypes() {
		return chosenDataTypes;
	}

	public void setChosenDataTypes(String[] parts) {
		listNetInfoType = new LinkedList<NetInfoType>();
		for (int i = 0; i < parts.length; i++) {
			listNetInfoType.add(NetInfoType.getType(Integer.parseInt(parts[i])));
		}
		
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	private boolean[] chosenDataTypes;
	private int requestId;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
