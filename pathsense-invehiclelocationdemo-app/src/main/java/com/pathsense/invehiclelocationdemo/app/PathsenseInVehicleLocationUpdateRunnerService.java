/*
 * Copyright (c) 2016 PathSense, Inc.
 */
package com.pathsense.invehiclelocationdemo.app;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;
public class PathsenseInVehicleLocationUpdateRunnerService extends Service
{
	static class InternalGroundTruthLocationListener implements LocationListener
	{
		PathsenseInVehicleLocationUpdateRunnerService mService;
		//
		InternalGroundTruthLocationListener(PathsenseInVehicleLocationUpdateRunnerService service)
		{
			mService = service;
		}
		@Override
		public void onLocationChanged(Location location)
		{
			final PathsenseInVehicleLocationUpdateRunnerService service = mService;
			//
			if (service != null)
			{
				if (location != null)
				{
					Log.i(TAG, "groundTruthLocation=" + location);
					// broadcast ground truth location update
					Intent groundTruthLocationUpdateIntent = new Intent("groundTruthLocationUpdate");
					groundTruthLocationUpdateIntent.putExtra("groundTruthLocation", location);
					LocalBroadcastManager.getInstance(service).sendBroadcast(groundTruthLocationUpdateIntent);
				}
			}
		}
		@Override
		public void onProviderDisabled(String provider)
		{
		}
		@Override
		public void onProviderEnabled(String provider)
		{
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
	}
	//
	static final String TAG = PathsenseInVehicleLocationUpdateRunnerService.class.getName();
	//
	InternalGroundTruthLocationListener mGroundTruthLocationListener;
	LocationManager mLocationManager;
	//
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	@Override
	public void onCreate()
	{
		super.onCreate();
		//
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		final LocationManager locationManager = mLocationManager;
		//
		if (locationManager != null)
		{
			String action = intent != null ? intent.getAction() : null;
			if ("start".equals(action))
			{
				// register for ground truth location updates
				if (mGroundTruthLocationListener == null)
				{
					mGroundTruthLocationListener = new InternalGroundTruthLocationListener(this);
				}
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mGroundTruthLocationListener);
				// request in-vehicle location updates
				PathsenseLocationProviderApi.getInstance(this).requestInVehicleLocationUpdates(PathsenseInVehicleLocationDemoInVehicleLocationUpdateReceiver.class);
			} else if ("stop".equals(action))
			{
				// unregister for ground truth location updates
				if (mGroundTruthLocationListener != null)
				{
					locationManager.removeUpdates(mGroundTruthLocationListener);
				}
				// remove in-vehicle location updates
				PathsenseLocationProviderApi.getInstance(this).removeInVehicleLocationUpdates();
				stopSelf();
			}
		}
		return START_STICKY;
	}
}
