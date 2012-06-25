package com.knushka.netinfo.provider.listeners;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import com.knushka.netinfo.NetInfoType;
import com.knushka.netinfo.provider.InfoProviderActivity;
import com.knushka.netinfo.provider.NetinfoData;

public class SensorsListener implements SensorEventListener {

	SensorManager mngr;
	TextView debugText;
	InfoProviderActivity activity;
	NetinfoData netinfoData;

	List<Sensor> listSensors;

	public SensorsListener(SensorManager mngr, InfoProviderActivity activity, TextView debugText,
			NetinfoData netinfoData) {
		super();

		this.netinfoData = netinfoData;
		this.mngr = mngr;
		this.debugText = debugText;
		this.activity = activity;

		this.listSensors = this.mngr.getSensorList(Sensor.TYPE_ALL);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {

		switch (event.sensor.getType()) {

		case Sensor.TYPE_ACCELEROMETER:
			activity.tvX.setText(Float.toString(event.values[0]));
			activity.tvY.setText(Float.toString(event.values[1]));
			activity.tvZ.setText(Float.toString(event.values[2]));

			this.netinfoData.addValues(NetInfoType.TYPE_ACCELEROMETER, event.values[0], event.values[1],
					event.values[2]);
			break;

		case Sensor.TYPE_GYROSCOPE:
			activity.tvG1.setText(Float.toString(event.values[0]));
			activity.tvG2.setText(Float.toString(event.values[1]));
			activity.tvG3.setText(Float.toString(event.values[2]));

			this.netinfoData.addValues(NetInfoType.TYPE_ACCELEROMETER, event.values[0], event.values[1],
					event.values[2]);
			break;

		case Sensor.TYPE_MAGNETIC_FIELD:
			activity.tvM1.setText(Float.toString(event.values[0]));
			activity.tvM2.setText(Float.toString(event.values[1]));
			activity.tvM3.setText(Float.toString(event.values[2]));

			this.netinfoData.addValues(NetInfoType.TYPE_MAGNETIC_FIELD, event.values[0], event.values[1],
					event.values[2]);
			break;

		case Sensor.TYPE_ORIENTATION:
			activity.tvOr1.setText(Float.toString(event.values[0]));
			activity.tvOr2.setText(Float.toString(event.values[1]));
			activity.tvOr3.setText(Float.toString(event.values[2]));

			this.netinfoData.addValues(NetInfoType.TYPE_ORIENTATION, event.values[0], event.values[1], event.values[2]);
			break;

		case Sensor.TYPE_PROXIMITY:
			activity.tvPr1.setText(Float.toString(event.values[0]));
			activity.tvPr2.setText(Float.toString(event.values[1]));
			activity.tvPr3.setText(Float.toString(event.values[2]));

			this.netinfoData.addValues(NetInfoType.TYPE_PROXIMITY, event.values[0], event.values[1], event.values[2]);
			break;

		case Sensor.TYPE_TEMPERATURE:
			activity.tvT.setText(Float.toString(event.values[0]));

			this.netinfoData.addValues(NetInfoType.TYPE_TEMPERATURE, event.values[0], event.values[1], event.values[2]);
			break;

		case Sensor.TYPE_LIGHT:
			activity.tvG1.setText("(Light) " + Float.toString(event.values[0]));
			activity.tvG2.setText(Float.toString(event.values[1]));
			activity.tvG3.setText(Float.toString(event.values[2]));

			this.netinfoData.addValues(NetInfoType.TYPE_LIGHT, event.values[0], event.values[1], event.values[2]);
			break;

		case Sensor.TYPE_PRESSURE:
			activity.tvT.setText(Float.toString(event.values[0]));

			this.netinfoData.addValues(NetInfoType.TYPE_PRESSURE, event.values[0], event.values[1], event.values[2]);
			break;
		}
	}

	public void onResume() {
		for (Sensor sensor : listSensors) {
			this.mngr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	public void onPause() {
		this.mngr.unregisterListener(this);
	}
}
