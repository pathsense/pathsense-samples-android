/*
 * Copyright (c) 2015 PathSense, Inc.
 */

package com.pathsense.activitydemo.app;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.widget.TextView;

import com.pathsense.android.sdk.location.PathsenseDetectedActivities;
import com.pathsense.android.sdk.location.PathsenseDetectedActivity;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
public class MainActivity extends Activity
{
	static class InternalActivityChangeReceiver extends BroadcastReceiver
	{
		MainActivity mActivity;
		//
		InternalActivityChangeReceiver(MainActivity activity)
		{
			mActivity = activity;
		}
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final MainActivity activity = mActivity;
			final InternalHandler handler = activity != null ? activity.mHandler : null;
			//
			if (activity != null && handler != null)
			{
				PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) intent.getSerializableExtra("detectedActivities");
				Message msg = Message.obtain();
				msg.what = MESSAGE_ON_ACTIVITY_CHANGE;
				msg.obj = detectedActivities;
				handler.sendMessage(msg);
			}
		}
	}
	static class InternalActivityUpdateReceiver extends BroadcastReceiver
	{
		MainActivity mActivity;
		//
		InternalActivityUpdateReceiver(MainActivity activity)
		{
			mActivity = activity;
		}
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final MainActivity activity = mActivity;
			final InternalHandler handler = activity != null ? activity.mHandler : null;
			//
			if (activity != null && handler != null)
			{
				PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) intent.getSerializableExtra("detectedActivities");
				Message msg = Message.obtain();
				msg.what = MESSAGE_ON_ACTIVITY_UPDATE;
				msg.obj = detectedActivities;
				handler.sendMessage(msg);
			}
		}
	}
	static class InternalHandler extends Handler
	{
		MainActivity mActivity;
		//
		InternalHandler(MainActivity activity)
		{
			mActivity = activity;
		}
		@Override
		public void handleMessage(Message msg)
		{
			final MainActivity activity = mActivity;
			final TextView textDetectedActivity0 = activity != null ? activity.mTextDetectedActivity0 : null;
			final TextView textStationary = activity != null ? activity.mTextStationary : null;
			final TextView textDetectedActivity1 = activity != null ? activity.mTextDetectedActivity1 : null;
			//
			if (activity != null && textDetectedActivity0 != null && textStationary != null && textDetectedActivity1 != null)
			{
				switch (msg.what)
				{
					case MESSAGE_ON_ACTIVITY_CHANGE:
					{
						PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) msg.obj;
						PathsenseDetectedActivity mostProbableActivity = detectedActivities.getMostProbableActivity();
						if (mostProbableActivity != null)
						{
							StringBuilder detectedActivityString = new StringBuilder(mostProbableActivity.getDetectedActivity().name());
							textDetectedActivity1.setText(detectedActivityString.toString());
						} else
						{
							textDetectedActivity1.setText("");
						}
						break;
					}
					case MESSAGE_ON_ACTIVITY_UPDATE:
					{
						PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) msg.obj;
						PathsenseDetectedActivity mostProbableActivity = detectedActivities.getMostProbableActivity();
						if (mostProbableActivity != null)
						{
							List<PathsenseDetectedActivity> detectedActivityList = detectedActivities.getDetectedActivities();
							int numDetectedActivityList = detectedActivityList != null ? detectedActivityList.size() : 0;
							if (numDetectedActivityList > 0)
							{
								DecimalFormat decimalF = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("en", "US"));
								decimalF.applyPattern("0.00000");
								//
								StringBuilder detectedActivityString = new StringBuilder();
								for (int i = 0; i < numDetectedActivityList; i++)
								{
									PathsenseDetectedActivity detectedActivity = detectedActivityList.get(i);
									if (i > 0)
									{
										detectedActivityString.append("<br />");
									}
									detectedActivityString.append(detectedActivity.getDetectedActivity().name() + " " + decimalF.format(detectedActivity.getConfidence()));
								}
								textStationary.setText(detectedActivities.isStationary() ? "STATIONARY" : "NOT STATIONARY");
								textDetectedActivity0.setText(Html.fromHtml(detectedActivityString.toString()));
							}
						} else
						{
							textDetectedActivity0.setText("");
						}
						break;
					}
				}
			}
		}
	}
	//
	static final String TAG = MainActivity.class.getName();
	// Messages
	static final int MESSAGE_ON_ACTIVITY_CHANGE = 1;
	static final int MESSAGE_ON_ACTIVITY_UPDATE = 2;
	//
	InternalActivityChangeReceiver mActivityChangeReceiver;
	InternalActivityUpdateReceiver mActivityUpdateReceiver;
	InternalHandler mHandler;
	TextView mTextDetectedActivity0;
	TextView mTextDetectedActivity1;
	TextView mTextStationary;
	//
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//
		mTextDetectedActivity0 = (TextView) findViewById(R.id.textDetectedActivity0);
		mTextStationary = (TextView) findViewById(R.id.textStationary);
		mTextDetectedActivity1 = (TextView) findViewById(R.id.textDetectedActivity1);
		mHandler = new InternalHandler(this);
		// receivers
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
		mActivityChangeReceiver = new InternalActivityChangeReceiver(this);
		localBroadcastManager.registerReceiver(mActivityChangeReceiver, new IntentFilter("activityChange"));
		mActivityUpdateReceiver = new InternalActivityUpdateReceiver(this);
		localBroadcastManager.registerReceiver(mActivityUpdateReceiver, new IntentFilter("activityUpdate"));
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		//
		PathsenseLocationProviderApi.getInstance(this).removeActivityChanges();
		PathsenseLocationProviderApi.getInstance(this).removeActivityUpdates();
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		//
		PathsenseLocationProviderApi.getInstance(this).requestActivityChanges(PathsenseActivityChangeBroadcastReceiver.class);
		PathsenseLocationProviderApi.getInstance(this).requestActivityUpdates(PathsenseActivityUpdateBroadcastReceiver.class);
	}
}
