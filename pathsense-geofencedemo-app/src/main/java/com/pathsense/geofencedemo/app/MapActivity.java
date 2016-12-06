/*
 * Copyright (c) 2015 PathSense, Inc.
 */

package com.pathsense.geofencedemo.app;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.pathsense.android.sdk.location.PathsenseGeofenceEvent;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;
public class MapActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback
{
	static class InternalHandler extends Handler
	{
		MapActivity mActivity;
		//
		int mZIndex;
		//
		InternalHandler(MapActivity activity)
		{
			mActivity = activity;
		}
		@Override
		public void handleMessage(Message msg)
		{
			final MapActivity activity = mActivity;
			final GoogleMap map = activity != null ? activity.mMap : null;
			//
			if (activity != null && map != null)
			{
				switch (msg.what)
				{
					case MESSAGE_ON_GEOFENCE_EVENT:
					{
						PathsenseGeofenceEvent geofenceEvent = (PathsenseGeofenceEvent) msg.obj;
						Location location = geofenceEvent.getLocation();
						LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
						// geofence for event
						Circle geofence = activity.mGeofence;
						if (geofence == null)
						{
							geofence = map.addCircle((new CircleOptions()).center(new LatLng(geofenceEvent.getLatitude(), geofenceEvent.getLongitude())).radius(geofenceEvent.getRadius()).fillColor(Color.BLUE).strokeColor(Color.BLUE).zIndex(mZIndex++));
							activity.mGeofence = geofence;
						} else
						{
							geofence.setZIndex(mZIndex++);
							geofence.setRadius(geofenceEvent.getRadius());
							geofence.setCenter(new LatLng(geofenceEvent.getLatitude(), geofenceEvent.getLongitude()));
						}
						if (geofenceEvent.isEgress())
						{
							// egress event
							Circle geofenceEgress = activity.mGeofenceEgress;
							if (geofenceEgress == null)
							{
								geofenceEgress = map.addCircle((new CircleOptions()).center(point).radius(5.0d).fillColor(0x99FFFF00).strokeColor(Color.YELLOW).zIndex(mZIndex++));
								activity.mGeofenceEgress = geofenceEgress;
							} else
							{
								geofenceEgress.setZIndex(mZIndex++);
								geofenceEgress.setCenter(point);
							}
						} else if (geofenceEvent.isIngress())
						{
							// ingress event
							Circle geofenceIngress = activity.mGeofenceIngress;
							if (geofenceIngress == null)
							{
								geofenceIngress = map.addCircle((new CircleOptions()).center(point).radius(5.0d).fillColor(0x99008000).strokeColor(Color.GREEN).zIndex(mZIndex++));
								activity.mGeofenceIngress = geofenceIngress;
							} else
							{
								geofenceIngress.setZIndex(mZIndex++);
								geofenceIngress.setCenter(point);
							}
						}
						map.moveCamera(CameraUpdateFactory.newLatLng(point));
						break;
					}
				}
			}
		}
	}
	static class InternalLocalGeofenceEventReceiver extends BroadcastReceiver
	{
		MapActivity mActivity;
		//
		InternalLocalGeofenceEventReceiver(MapActivity activity)
		{
			mActivity = activity;
		}
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final MapActivity activity = mActivity;
			final InternalHandler handler = activity != null ? activity.mHandler : null;
			//
			if (activity != null && handler != null)
			{
				// local broadcast from PathsenseGeofenceEventBroadcastReceiver
				PathsenseGeofenceEvent geofenceEvent = (PathsenseGeofenceEvent) intent.getSerializableExtra("geofenceEvent");
				Message msg = Message.obtain();
				msg.what = MESSAGE_ON_GEOFENCE_EVENT;
				msg.obj = geofenceEvent;
				handler.sendMessage(msg);
			}
		}
	}
	//
	static final String TAG = MapActivity.class.getName();
	// Messages
	static final int MESSAGE_ON_GEOFENCE_EVENT = 1;
	//
	int mFindLocation;
	Button mButtonStart;
	Circle mGeofence;
	Circle mGeofenceEgress;
	Circle mGeofenceIngress;
	GoogleMap mMap;
	InternalHandler mHandler;
	InternalLocalGeofenceEventReceiver mLocalGeofenceEventReceiver;
	PathsenseLocationProviderApi mApi;
	SharedPreferences mPreferences;
	//
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		//
		mPreferences = getSharedPreferences("PathsenseGeofenceDemoPreferences", MODE_PRIVATE);
		mHandler = new InternalHandler(this);
		// local broadcast receiver for geofence events from PathsenseGeofenceEventBroadcastReceiver
		mLocalGeofenceEventReceiver = new InternalLocalGeofenceEventReceiver(this);
		LocalBroadcastManager.getInstance(this).registerReceiver(mLocalGeofenceEventReceiver, new IntentFilter("geofenceEvent"));
		// location api
		mApi = PathsenseLocationProviderApi.getInstance(this);
		// initialize UI
		mButtonStart = (Button) findViewById(R.id.buttonStart);
		mButtonStart.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				final SharedPreferences preferences = mPreferences;
				final Button buttonStart = mButtonStart;
				final PathsenseLocationProviderApi api = mApi;
				//
				if (preferences != null && buttonStart != null && api != null)
				{
					int startedFlag = preferences.getInt("startedFlag", 0);
					if (startedFlag == 1)
					{
						buttonStart.setText("Start");
						// remove geofences
						api.removeGeofences();
						// turn-off switch
						SharedPreferences.Editor editor = preferences.edit();
						editor.putInt("startedFlag", 0);
						editor.commit();
					} else
					{
						buttonStart.setText("Stop");
						// cleanup
						if (mGeofence != null)
						{
							mGeofence.remove();
							mGeofence = null;
						}
						if (mGeofenceEgress != null)
						{
							mGeofenceEgress.remove();
							mGeofenceEgress = null;
						}
						if (mGeofenceIngress != null)
						{
							mGeofenceIngress.remove();
							mGeofenceIngress = null;
						}
						// find current location for geofence center
						mFindLocation = 0;
						FusedLocationManager.getInstance(MapActivity.this).requestLocationUpdate(MapActivity.this);
						// turn-on switch
						SharedPreferences.Editor editor = preferences.edit();
						editor.putInt("startedFlag", 1);
						editor.commit();
					}
				}
			}
		});
		mButtonStart.setEnabled(false);
		// Obtain the MapFragment and set the async listener to be notified when the map is ready.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}
	@Override
	public void onLocationChanged(Location location)
	{
		final GoogleMap map = mMap;
		final SharedPreferences preferences = mPreferences;
		final Button buttonStart = mButtonStart;
		final PathsenseLocationProviderApi api = mApi;
		//
		if (map != null && preferences != null && buttonStart != null && api != null)
		{
			if (mFindLocation == 1)
			{
				map.moveCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder()).target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(18).build()));
				//
				int startedFlag = preferences.getInt("startedFlag", 0);
				if (startedFlag == 1)
				{
					buttonStart.setText("Stop");
				} else
				{
					buttonStart.setText("Start");
				}
				buttonStart.setEnabled(true);
				//
				mFindLocation = 0;
			} else
			{
				// add 100m geofence around me
				api.addGeofence("MYGEOFENCE", location.getLatitude(), location.getLongitude(), 100, PathsenseGeofenceDemoGeofenceEventReceiver.class);
			}
		}
	}
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;
		mFindLocation = 1;
		FusedLocationManager.getInstance(this).requestLocationUpdate(this);
	}
	@Override
	public void onProviderDisabled(String s)
	{
	}
	@Override
	public void onProviderEnabled(String s)
	{
	}
	@Override
	public void onStatusChanged(String s, int i, Bundle bundle)
	{
	}
}
