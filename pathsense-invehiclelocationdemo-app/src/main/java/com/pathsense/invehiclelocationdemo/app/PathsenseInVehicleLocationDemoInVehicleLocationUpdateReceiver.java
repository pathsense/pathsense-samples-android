/*
 * Copyright (c) 2016 PathSense, Inc.
 */
package com.pathsense.invehiclelocationdemo.app;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pathsense.android.sdk.location.PathsenseInVehicleLocation;
import com.pathsense.android.sdk.location.PathsenseInVehicleLocationUpdateReceiver;

public class PathsenseInVehicleLocationDemoInVehicleLocationUpdateReceiver extends PathsenseInVehicleLocationUpdateReceiver {
    static final String TAG = PathsenseInVehicleLocationDemoInVehicleLocationUpdateReceiver.class.getName();

    @Override
    protected void onInVehicleLocationUpdate(Context context, PathsenseInVehicleLocation pathsenseInVehicleLocation) {
        Log.i(TAG, "inVehicleLocation=" + pathsenseInVehicleLocation);
        // broadcast in-vehicle location update
        Intent inVehicleLocationUpdateIntent = new Intent("inVehicleLocationUpdate");
        inVehicleLocationUpdateIntent.putExtra("inVehicleLocation", pathsenseInVehicleLocation);
        LocalBroadcastManager.getInstance(context).sendBroadcast(inVehicleLocationUpdateIntent);
    }
}
