package com.knushka.netinfo.provider;

import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.knushka.netinfo.GlobalConstants;
import com.knushka.netinfo.provider.R;
import com.knushka.netinfo.provider.listeners.GpsListener;
import com.knushka.netinfo.provider.listeners.SensorsListener;

public class InfoProviderActivity extends Activity {

	private GpsListener gpsLocationListener;
	private SensorsListener sensorsListener;

	EditText eTimerUpdatePeriod;
	Timer timer;

	int providerId;

	NetinfoData netinfoData;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.providerId = Global.getRandomNumber();
		setSensorFields();

		this.netinfoData = new NetinfoData();
		setGpsListener();
		setSensorsListener();

		startNetworking();
		
		Log.w(Global.LOG, "=====================");

		// scheduleUpdateLocatorTimer();
		// setButtonStopUdpSendingListener();
		// setButtonStartUdpSendingListener();

	}

	private void setSensorsListener() {
		final TextView tvDebug = (TextView) findViewById(R.id.tvDebug);

		SensorManager mngr = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		sensorsListener = new SensorsListener(mngr, this, tvDebug, this.netinfoData);
	}

	private void setGpsListener() {
		final TextView text = (TextView) findViewById(R.id.tvDebug);
		Handler mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				text.setText(text.getText() + "\n" + (String) msg.obj);
			}
		};
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		gpsLocationListener = new GpsListener(this, mainHandler, locationManager, this.netinfoData);
	}

	TextView text;

	private void startNetworking() {
		text = (TextView) findViewById(R.id.tvDebug);
		Handler mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				text.setText(text.getText() + "\n" + (String) msg.obj);
			}
		};
		// HI: should not create server if it already exists
		Thread tcpConnectionListener = new Thread(new CreateServer(this.netinfoData, providerId, mainHandler));
		tcpConnectionListener.start();
	}

	// private void setButtonStopUdpSendingListener() {
	// Button btnStopUdpSending = (Button) findViewById(R.id.btnStopUDPSend);
	//
	// btnStopUdpSending.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View view) {
	// if (timer != null) {
	// timer.cancel();
	// timer = null;
	// }
	// }
	// });
	// }

	// private void setButtonStartUdpSendingListener() {
	// Button btnStartUdpSending = (Button) findViewById(R.id.btnStartUDPSend);
	//
	// btnStartUdpSending.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View view) {
	// if (timer == null) {
	// scheduleSendingUdpPocketTimer();
	// }
	// }
	// });
	// }
	//
	// public void scheduleSendingUdpPocketTimer() {
	// final Handler handler = new Handler();
	//
	// // timer.cancel();
	// if (timer == null)
	// timer = new Timer();
	//
	// TimerTask sendUDPPocketTask = new TimerTask() {
	// @Override
	// public void run() {
	// /*
	// * The timer task is made with handler due to efficiency issues, see
	// * developer.android.com/resources/articles/timed-ui-updates.html
	// */
	// handler.post(new Runnable() {
	// @Override
	// public void run() {
	// gpsLocationListener.sendUDPPocket();
	// }
	// });
	// }
	// };
	// // get timer update period from user's input
	// eTimerUpdatePeriod = (EditText) findViewById(R.id.EditUpdateTime);
	// String str = eTimerUpdatePeriod.getText().toString();
	// int timerUpdatePeriod = Integer.parseInt(str);
	//
	// timer.scheduleAtFixedRate(sendUDPPocketTask, 0, timerUpdatePeriod);
	// }

	@Override
	protected void onResume() {
		
		super.onResume();
		if (this.gpsLocationListener != null)
			this.gpsLocationListener.onResume();
		if (this.sensorsListener != null)
			this.sensorsListener.onResume();
		((TextView) findViewById(R.id.tvMyIp)).setText("My ip:" + Global.getLocalIp());
		Log.v(Global.LOG, "onResume");
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		this.gpsLocationListener.onPause();
		this.sensorsListener.onPause();
		Log.v(Global.LOG, "onPause");
	}

	public TextView tvX = null;
	public TextView tvY = null;
	public TextView tvZ = null;

	public TextView tvM1 = null;
	public TextView tvM2 = null;
	public TextView tvM3 = null;

	public TextView tvOr1 = null;
	public TextView tvOr2 = null;
	public TextView tvOr3 = null;

	public TextView tvPr1 = null;
	public TextView tvPr2 = null;
	public TextView tvPr3 = null;

	public TextView tvG1 = null;
	public TextView tvG2 = null;
	public TextView tvG3 = null;

	public TextView tvT = null;

	public TextView latituteField;
	public TextView longitudeField;

	private void setSensorFields() {
		tvX = (TextView) findViewById(R.id.tvX);
		tvY = (TextView) findViewById(R.id.tvY);
		tvZ = (TextView) findViewById(R.id.tvZ);

		tvM1 = (TextView) findViewById(R.id.tvM1);
		tvM2 = (TextView) findViewById(R.id.tvM2);
		tvM3 = (TextView) findViewById(R.id.tvM3);

		tvOr1 = (TextView) findViewById(R.id.tvOr1);
		tvOr2 = (TextView) findViewById(R.id.tvOr2);
		tvOr3 = (TextView) findViewById(R.id.tvOr3);

		tvPr1 = (TextView) findViewById(R.id.tvPr1);
		tvPr2 = (TextView) findViewById(R.id.tvPr2);
		tvPr3 = (TextView) findViewById(R.id.tvPr3);

		tvG1 = (TextView) findViewById(R.id.tvG1);
		tvG2 = (TextView) findViewById(R.id.tvG2);
		tvG3 = (TextView) findViewById(R.id.tvG3);

		tvT = (TextView) findViewById(R.id.tvT);

		latituteField = (TextView) findViewById(R.id.latitude);
		longitudeField = (TextView) findViewById(R.id.longitude);

	}

}
