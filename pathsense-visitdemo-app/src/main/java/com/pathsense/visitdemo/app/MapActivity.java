/*
 * Copyright (c) 2015 PathSense, Inc.
 */

package com.pathsense.visitdemo.app;
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
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApiDataStore;
import com.pathsense.android.sdk.location.PathsenseVisitEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
public class MapActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback
{
	static class InternalHandler extends Handler
	{
		MapActivity mActivity;
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
					case MESSAGE_ON_VISIT_EVENT:
					{
						PathsenseVisitEvent visitEvent = (PathsenseVisitEvent) msg.obj;
						Location location = visitEvent.getLocation();
						activity.drawMarker(visitEvent);
						LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
						map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
						break;
					}
				}
			}
		}
	}
	static class InternalVisitEventReceiver extends BroadcastReceiver
	{
		MapActivity mActivity;
		//
		InternalVisitEventReceiver(MapActivity activity)
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
				PathsenseVisitEvent visitEvent = (PathsenseVisitEvent) intent.getSerializableExtra("visitEvent");
				Message msg = Message.obtain();
				msg.what = MESSAGE_ON_VISIT_EVENT;
				msg.obj = visitEvent;
				handler.sendMessage(msg);
			}
		}
	}
	//
	static final String TAG = MapActivity.class.getName();
	// Messages
	static final int MESSAGE_ON_VISIT_EVENT = 0;
	//
	int mZIndex;
	ArrayList<Circle> mMarkers = new ArrayList<Circle>();
	Button mButtonStart;
	GoogleMap mMap;
	HashSet<String> mVisits = new HashSet<String>();
	InternalHandler mHandler;
	InternalVisitEventReceiver mVisitEventReceiver;
	PathsenseLocationProviderApi mApi;
	SharedPreferences mPreferences;
	//
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		//
		mPreferences = getSharedPreferences("PathsensePreferences", MODE_PRIVATE);
		mHandler = new InternalHandler(this);
		// receivers
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
		mVisitEventReceiver = new InternalVisitEventReceiver(this);
		localBroadcastManager.registerReceiver(mVisitEventReceiver, new IntentFilter("visitEvent"));
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
				final PathsenseLocationProviderApiDataStore dataStore = api != null ? api.getDataStore() : null;
				//
				if (preferences != null && buttonStart != null && api != null && dataStore != null)
				{
					int startedFlag = preferences.getInt("startedFlag", 0);
					if (startedFlag == 1)
					{
						buttonStart.setText("Start");
						// turn-off visits
						api.removeVisits();
						// turn-off switch
						SharedPreferences.Editor editor = preferences.edit();
						editor.putInt("startedFlag", 0);
						editor.commit();
					} else
					{
						buttonStart.setText("Stop");
						// cleanup
						removeMarkers();
						dataStore.removeVisitEvents();
						// turn-on visits
						api.requestVisits(PathsenseVisitDemoVisitEventReceiver.class);
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
	void drawMarker(PathsenseVisitEvent visitEvent)
	{
		final HashSet<String> visits = mVisits;
		final GoogleMap map = mMap;
		final ArrayList<Circle> markers = mMarkers;
		//
		if (visits != null && map != null && markers != null)
		{
			Location location = visitEvent.getLocation();
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			// draw visit
			String visitId = visitEvent.getLatitude() + "," + visitEvent.getLongitude() + "," + visitEvent.getRadius();
			if (visits.add(visitId))
			{
				Circle marker0 = map.addCircle((new CircleOptions()).center(new LatLng(visitEvent.getLatitude(), visitEvent.getLongitude())).radius(visitEvent.getRadius()).fillColor(Color.BLUE).strokeColor(Color.BLUE).zIndex(mZIndex++));
				markers.add(marker0);
			}
			// draw point
			if (visitEvent.isArrival())
			{
				Circle marker1 = map.addCircle((new CircleOptions()).center(latLng).radius(5.0d).fillColor(0x99008000).strokeColor(Color.GREEN).zIndex(mZIndex++));
				markers.add(marker1);
			} else if (visitEvent.isDeparture())
			{
				Circle marker1 = map.addCircle((new CircleOptions()).center(latLng).radius(5.0d).fillColor(0x99FFFF00).strokeColor(Color.YELLOW).zIndex(mZIndex++));
				markers.add(marker1);
			}
		}
	}
	void drawMarkers()
	{
		final PathsenseLocationProviderApi api = mApi;
		final PathsenseLocationProviderApiDataStore dataStore = api != null ? api.getDataStore() : null;
		final GoogleMap map = mMap;
		//
		if (api != null && dataStore != null && map != null)
		{
			removeMarkers();
			// draw markers
			Location mostRecentLocation = null;
			//
			List<PathsenseVisitEvent> visitEvents = dataStore.getVisitEvents();
			int numVisitEvents = visitEvents != null ? visitEvents.size() : 0;
			if (numVisitEvents > 0)
			{
				for (int i = 0; i < numVisitEvents; i++)
				{
					PathsenseVisitEvent visitEvent = visitEvents.get(i);
					Location location = visitEvent.getLocation();
					drawMarker(visitEvent);
					//
					if (mostRecentLocation == null || location.getTime() > mostRecentLocation.getTime())
					{
						mostRecentLocation = location;
					}
				}
			}
			//
			if (mostRecentLocation != null)
			{
				LatLng mostRecentLatLng = new LatLng(mostRecentLocation.getLatitude(), mostRecentLocation.getLongitude());
				map.moveCamera(CameraUpdateFactory.newLatLng(mostRecentLatLng));
			}
		}
	}
	@Override
	public void onLocationChanged(Location location)
	{
		final GoogleMap map = mMap;
		final SharedPreferences preferences = mPreferences;
		final Button buttonStart = mButtonStart;
		//
		if (map != null && preferences != null && buttonStart != null)
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
			drawMarkers();
		}
	}
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;
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
	protected void onResume()
	{
		super.onResume();
		//
		drawMarkers();
	}
	@Override
	public void onStatusChanged(String s, int i, Bundle bundle)
	{
	}
	void removeMarkers()
	{
		final HashSet<String> visits = mVisits;
		final ArrayList<Circle> markers = mMarkers;
		//
		if (visits != null && markers != null)
		{
			visits.clear();
			//
			int numMarkers = markers.size();
			for (int i = 0; i < numMarkers; i++)
			{
				Circle marker = markers.get(i);
				marker.remove();
			}
		}
	}
}
