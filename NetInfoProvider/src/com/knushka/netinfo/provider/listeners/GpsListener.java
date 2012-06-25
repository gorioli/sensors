package com.knushka.netinfo.provider.listeners;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.knushka.netinfo.NetInfoType;
import com.knushka.netinfo.provider.Global;
import com.knushka.netinfo.provider.InfoProviderActivity;
import com.knushka.netinfo.provider.NetinfoData;
import com.knushka.netinfo.provider.R;

public class GpsListener implements LocationListener {

	private LocationManager locationManager;

	private NetinfoData netinfoData;

	private long locationUpdatesMinTime; // in milliseconds
	private float locationUpdatesMinDistance; // in meters

	private Handler mainHandler;

	private InfoProviderActivity activity;

	private Location location = null;

	public GpsListener(InfoProviderActivity activity, Handler mainHandler, LocationManager locationManager,
			NetinfoData netinfoData) {
		this.activity = activity;
		this.mainHandler = mainHandler;
		this.locationManager = locationManager;
		this.netinfoData = netinfoData;

		locationUpdatesMinDistance = R.integer.locationUpdatesMinDistance;
		locationUpdatesMinTime = R.integer.locationUpdatesMinTime;
		initLocation();

		if (location != null) {
			Log.w(Global.LOG, "-----------------\nlocation provider -" + location.getProvider() + "- has been selected.");

			onLocationChanged(location);

		} else {
			activity.latituteField.setText("Provider not available");
			activity.longitudeField.setText("");
		}
	}

	public void onLocationChanged(Location location) {
		netinfoData.addValues(NetInfoType.TYPE_GPS, location.getLatitude(), location.getLongitude());

		activity.latituteField.setText(String.valueOf(location.getLatitude()));
		activity.longitudeField.setText(String.valueOf(location.getLongitude()));

		Log.v(Global.LOG, "onLocationChanged");
	}

	public void onProviderDisabled(String provider) {
		Log.v(Global.LOG, "onProviderDisabled");
		location.reset();
	}

	public void onProviderEnabled(String provider) {
		initLocation();
		onLocationChanged(location);
		Log.v(Global.LOG, "onProviderEnabled");
	}

	/*
	 * Called when the provider status changes. This method is called when a provider is unable to fetch a location or
	 * if the provider has recently become available after a period of unavailability.
	 */
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.v(Global.LOG, "onStatusChanged");
	}

	private String provider;

	private void initLocation() {
		// Define the criteria how to select the locatioin provider -> use default
		provider = locationManager.getBestProvider(new Criteria(), false);
		location = locationManager.getLastKnownLocation(provider);
	}

	public void onResume() {
		locationManager.requestLocationUpdates("gps", locationUpdatesMinTime, locationUpdatesMinDistance, this);
	}

	public void onPause() {
		locationManager.removeUpdates(this);
	}

	private void printLine(String str) {
		Message msg = Message.obtain();
		msg.obj = str;
		mainHandler.sendMessage(msg);
	}
}
