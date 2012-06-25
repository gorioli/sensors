package com.knushka.netinfo;

public enum NetInfoType {

	TYPE_ACCELEROMETER(1), 
	TYPE_MAGNETIC_FIELD(2), 
	TYPE_ORIENTATION(3), 
	TYPE_GYROSCOPE(4), 
	TYPE_LIGHT(5), 
	TYPE_PRESSURE(6),
	TYPE_TEMPERATURE(7), 
	TYPE_PROXIMITY(8), 
	TYPE_GPS(20);

	private int type;
	
	public static NetInfoType getType(int type) {
		switch (type) {
		case 1:
			return NetInfoType.TYPE_ACCELEROMETER;		
		case 2:
			return NetInfoType.TYPE_MAGNETIC_FIELD;	
		case 3:
			return NetInfoType.TYPE_ORIENTATION;	
		case 4:
			return NetInfoType.TYPE_GYROSCOPE;	
		case 5:
			return NetInfoType.TYPE_LIGHT;	
		case 6:
			return NetInfoType.TYPE_PRESSURE;	
		case 7:
			return NetInfoType.TYPE_TEMPERATURE;	
		case 8:
			return NetInfoType.TYPE_PROXIMITY;	
		case 20:
			return NetInfoType.TYPE_GPS;				
		}
		return null;
	}

	private NetInfoType(int type) {
		this.type = type;
	}

	public int getValue() {
		return type;
	}	
}