/*
 * Copyright (c) 2015 PathSense, Inc.
 */

package com.pathsense.activitydemo.app;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pathsense.android.sdk.location.PathsenseDeviceHolding;
import com.pathsense.android.sdk.location.PathsenseDeviceHoldingReceiver;
public class PathsenseDeviceHoldingBroadcastReceiver extends PathsenseDeviceHoldingReceiver
{
	static final String TAG = PathsenseActivityChangeBroadcastReceiver.class.getName();
	//
	@Override
	protected void onDeviceHolding(Context context, PathsenseDeviceHolding deviceHolding)
	{
		Log.i(TAG, "deviceHolding = " + deviceHolding);
		// broadcast device holding
		Intent deviceHoldingIntent = new Intent("deviceHolding");
		deviceHoldingIntent.putExtra("deviceHolding", deviceHolding);
		LocalBroadcastManager.getInstance(context).sendBroadcast(deviceHoldingIntent);
	}
}
