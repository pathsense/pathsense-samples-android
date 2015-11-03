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
import com.pathsense.android.sdk.location.PathsenseDeviceHolding;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;

import java.util.List;
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
	static class InternalDeviceHoldingReceiver extends BroadcastReceiver
	{
		MainActivity mActivity;
		//
		InternalDeviceHoldingReceiver(MainActivity activity)
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
				PathsenseDeviceHolding deviceHolding = (PathsenseDeviceHolding) intent.getSerializableExtra("deviceHolding");
				Message msg = Message.obtain();
				msg.what = MESSAGE_ON_DEVICE_HOLDING;
				msg.obj = deviceHolding;
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
			final TextView textDetectedActivity1 = activity != null ? activity.mTextDetectedActivity1 : null;
			final TextView textDeviceHolding = activity != null ? activity.mTextDeviceHolding : null;
			final PathsenseLocationProviderApi api = activity != null ? activity.mApi : null;
			//
			if (activity != null && textDetectedActivity0 != null && textDetectedActivity1 != null && textDeviceHolding != null && api != null)
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
//							if (mostProbableActivity.isStationary())
//							{
//								detectedActivityString.append(" STATIONARY");
//							}
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
								StringBuilder detectedActivityString = new StringBuilder();
								for (int i = 0; i < numDetectedActivityList; i++)
								{
									PathsenseDetectedActivity detectedActivity = detectedActivityList.get(i);
									if (i > 0)
									{
										detectedActivityString.append("<br />");
									}
									detectedActivityString.append(detectedActivity.getDetectedActivity().name() + " " + detectedActivity.getConfidence());
								}
								textDetectedActivity0.setText(Html.fromHtml(detectedActivityString.toString()));
							}
						} else
						{
							textDetectedActivity0.setText("");
						}
						break;
					}
					case MESSAGE_ON_DEVICE_HOLDING:
					{
						PathsenseDeviceHolding deviceHolding = (PathsenseDeviceHolding) msg.obj;
						if (deviceHolding != null)
						{
							textDeviceHolding.setText(deviceHolding.isHolding() ? "Holding" : "Not Holding");
						} else
						{
							textDeviceHolding.setText("");
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
	static final int MESSAGE_ON_DEVICE_HOLDING = 3;
	//
	InternalActivityChangeReceiver mActivityChangeReceiver;
	InternalActivityUpdateReceiver mActivityUpdateReceiver;
	InternalDeviceHoldingReceiver mDeviceHoldingReceiver;
	InternalHandler mHandler;
	PathsenseLocationProviderApi mApi;
	TextView mTextDetectedActivity0;
	TextView mTextDetectedActivity1;
	TextView mTextDeviceHolding;
	//
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//
		mTextDetectedActivity0 = (TextView) findViewById(R.id.textDetectedActivity0);
		mTextDetectedActivity1 = (TextView) findViewById(R.id.textDetectedActivity1);
		mTextDeviceHolding = (TextView) findViewById(R.id.textDeviceHolding);
		mHandler = new InternalHandler(this);
		// receivers
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
		mActivityChangeReceiver = new InternalActivityChangeReceiver(this);
		localBroadcastManager.registerReceiver(mActivityChangeReceiver, new IntentFilter("activityChange"));
		mActivityUpdateReceiver = new InternalActivityUpdateReceiver(this);
		localBroadcastManager.registerReceiver(mActivityUpdateReceiver, new IntentFilter("activityUpdate"));
		mDeviceHoldingReceiver = new InternalDeviceHoldingReceiver(this);
		localBroadcastManager.registerReceiver(mDeviceHoldingReceiver, new IntentFilter("deviceHolding"));
		// location api
		mApi = PathsenseLocationProviderApi.getInstance(this);
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		//
		final PathsenseLocationProviderApi api = mApi;
		//
		if (api != null)
		{
			api.removeActivityChanges();
			api.removeActivityUpdates();
			api.removeDeviceHolding();
		}
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		//
		final PathsenseLocationProviderApi api = mApi;
		//
		if (api != null)
		{
			api.requestActivityChanges(PathsenseActivityChangeBroadcastReceiver.class);
			api.requestActivityUpdates(PathsenseActivityUpdateBroadcastReceiver.class);
			api.requestDeviceHolding(PathsenseDeviceHoldingBroadcastReceiver.class);
		}
	}
}
