/*
 * Copyright (c) 2015 PathSense, Inc.
 */

package com.pathsense.geofencedemo.app;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pathsense.android.sdk.location.PathsenseGeofenceEvent;
import com.pathsense.android.sdk.location.PathsenseGeofenceEventReceiver;

public class PathsenseGeofenceDemoGeofenceEventReceiver extends PathsenseGeofenceEventReceiver {
    static final String TAG = PathsenseGeofenceDemoGeofenceEventReceiver.class.getName();

    //
    @Override
    protected void onGeofenceEvent(Context context, PathsenseGeofenceEvent geofenceEvent) {
        Log.i(TAG, "geofence = " + geofenceEvent.getGeofenceId() + ", " + geofenceEvent.getLatitude() + ", " + geofenceEvent.getLongitude() + ", " + geofenceEvent.getRadius());
        //
        if (geofenceEvent.isEgress()) {
            Location location = geofenceEvent.getLocation();
            Log.i(TAG, "geofenceEgress = " + location.getTime() + ", " + location.getProvider() + ", " + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAltitude() + ", " + location.getSpeed() + ", " + location.getBearing() + ", " + location.getAccuracy());
            SoundManager.getInstance(context).playDing();
            // broadcast event
            Intent geofenceEventIntent = new Intent("geofenceEvent");
            geofenceEventIntent.putExtra("geofenceEvent", geofenceEvent);
            LocalBroadcastManager.getInstance(context).sendBroadcast(geofenceEventIntent);
        } else if (geofenceEvent.isIngress()) {
            Location location = geofenceEvent.getLocation();
            Log.i(TAG, "geofenceIngress = " + location.getTime() + ", " + location.getProvider() + ", " + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAltitude() + ", " + location.getSpeed() + ", " + location.getBearing() + ", " + location.getAccuracy());
            SoundManager.getInstance(context).playJingle();
            // broadcast event
            Intent geofenceEventIntent = new Intent("geofenceEvent");
            geofenceEventIntent.putExtra("geofenceEvent", geofenceEvent);
            LocalBroadcastManager.getInstance(context).sendBroadcast(geofenceEventIntent);
        }
    }
}
