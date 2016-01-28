/*
 * Copyright (c) 2015 PathSense, Inc.
 */

package com.pathsense.visitdemo.app;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;
import com.pathsense.android.sdk.location.PathsenseVisitEvent;
import com.pathsense.android.sdk.location.PathsenseVisitEventReceiver;
public class PathsenseVisitDemoVisitEventReceiver extends PathsenseVisitEventReceiver
{
	static final String TAG = PathsenseVisitDemoVisitEventReceiver.class.getName();
	//
	@Override
	protected void onVisitEvent(Context context, PathsenseVisitEvent visitEvent)
	{
		Log.i(TAG, "visit = " + visitEvent.getLatitude() + ", " + visitEvent.getLongitude() + ", " + visitEvent.getRadius());
		//
		if (visitEvent.isArrival())
		{
			Location location = visitEvent.getLocation();
			Log.i(TAG, "arrival = " + location.getTime() + ", " + location.getProvider() + ", " + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAltitude() + ", " + location.getSpeed() + ", " + location.getBearing() + ", " + location.getAccuracy());
			SoundManager.getInstance(context).playJingle();
			// store visit arrival
			PathsenseLocationProviderApi.getInstance(context).getDataStore().addVisitEvent(visitEvent);
			// broadcast visit event
			Intent visitEventIntent = new Intent("visitEvent");
			visitEventIntent.putExtra("visitEvent", visitEvent);
			LocalBroadcastManager.getInstance(context).sendBroadcast(visitEventIntent);
		} else if (visitEvent.isDeparture())
		{
			Location location = visitEvent.getLocation();
			Log.i(TAG, "departure = " + location.getTime() + ", " + location.getProvider() + ", " + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAltitude() + ", " + location.getSpeed() + ", " + location.getBearing() + ", " + location.getAccuracy());
			SoundManager.getInstance(context).playDing();
			// store visit departure
			PathsenseLocationProviderApi.getInstance(context).getDataStore().addVisitEvent(visitEvent);
			// broadcast visit event
			Intent visitEventIntent = new Intent("visitEvent");
			visitEventIntent.putExtra("visitEvent", visitEvent);
			LocalBroadcastManager.getInstance(context).sendBroadcast(visitEventIntent);
		}
	}
}
