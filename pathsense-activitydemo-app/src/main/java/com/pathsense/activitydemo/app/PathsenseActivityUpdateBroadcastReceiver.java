/*
 * Copyright (c) 2015 PathSense, Inc.
 */

package com.pathsense.activitydemo.app;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pathsense.android.sdk.location.PathsenseActivityRecognitionReceiver;
import com.pathsense.android.sdk.location.PathsenseDetectedActivities;

public class PathsenseActivityUpdateBroadcastReceiver extends PathsenseActivityRecognitionReceiver {
    static final String TAG = PathsenseActivityUpdateBroadcastReceiver.class.getName();

    //
    @Override
    protected void onDetectedActivities(Context context, PathsenseDetectedActivities detectedActivities) {
        Log.i(TAG, "detectedActivities = " + detectedActivities);
        // broadcast detected activities
        Intent detectedActivitiesIntent = new Intent("activityUpdate");
        detectedActivitiesIntent.putExtra("detectedActivities", detectedActivities);
        LocalBroadcastManager.getInstance(context).sendBroadcast(detectedActivitiesIntent);
    }
}
