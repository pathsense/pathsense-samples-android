/*
 * Copyright (c) 2016 PathSense, Inc.
 */
package com.pathsense.invehiclelocationdemo.app;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pathsense.android.sdk.location.PathsenseInVehicleLocation;

import java.util.ArrayList;
import java.util.List;
public class MapActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback
{
	static class InternalGroundTruthLocationUpdateReceiver extends BroadcastReceiver
	{
		MapActivity mActivity;
		//
		InternalGroundTruthLocationUpdateReceiver(MapActivity activity)
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
				Location groundTruthLocation = intent.getParcelableExtra("groundTruthLocation");
				Message msg = Message.obtain();
				msg.what = MESSAGE_ON_GROUND_TRUTH_LOCATION;
				msg.obj = groundTruthLocation;
				handler.sendMessage(msg);
			}
		}
	}
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
					case MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE:
					{
						PathsenseInVehicleLocation inVehicleLocation = (PathsenseInVehicleLocation) msg.obj;
						LatLng position = new LatLng(inVehicleLocation.getLatitude(), inVehicleLocation.getLongitude());
						Marker markerInVehicle = activity.mMarkerInVehicle;
						if (markerInVehicle == null)
						{
							markerInVehicle = map.addMarker((new MarkerOptions()).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_arrow)).anchor(.5f, .5f).position(position).title("PathsenseInVehicle"));
							activity.mMarkerInVehicle = markerInVehicle;
						} else
						{
							markerInVehicle.setPosition(position);
						}
						activity.mHeadingRoad = activity.unwrapHeading(inVehicleLocation.getBearing(), activity.mHeadingGroundTruth);
						markerInVehicle.setRotation((float) activity.mHeadingRoad + 90);
						map.moveCamera(CameraUpdateFactory.newLatLng(position));
						activity.drawPolylineInVehicle(inVehicleLocation);
						break;
					}
					case MESSAGE_ON_GROUND_TRUTH_LOCATION:
					{
						final Marker groundTruthMarker = activity.mMarkerGroundTruth;
						//
						if (groundTruthMarker != null)
						{
							Location groundTruthLocation = (Location) msg.obj;
							float bearing = groundTruthLocation.getBearing();
							if (bearing != 0)
							{
								activity.mHeadingGroundTruth = groundTruthLocation.getBearing();
							}
							groundTruthMarker.setRotation((float) activity.mHeadingGroundTruth - 90);
							LatLng position = new LatLng(groundTruthLocation.getLatitude(), groundTruthLocation.getLongitude());
							groundTruthMarker.setPosition(position);
							//map.moveCamera(CameraUpdateFactory.newLatLng(position));
							activity.drawPolylineGroundTruth(groundTruthLocation);
						}
						break;
					}
				}
			}
		}
	}
	static class InternalInVehicleLocationUpdateReceiver extends BroadcastReceiver
	{
		MapActivity mActivity;
		//
		InternalInVehicleLocationUpdateReceiver(MapActivity activity)
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
				PathsenseInVehicleLocation inVehicleLocationUpdate = intent.getParcelableExtra("inVehicleLocation");
				Message msg = Message.obtain();
				msg.what = MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE;
				msg.obj = inVehicleLocationUpdate;
				handler.sendMessage(msg);
			}
		}
	}
	//
	static final String TAG = MapActivity.class.getName();
	// Messages
	static final int MESSAGE_ON_IN_VEHICLE_LOCATION_UPDATE = 0;
	static final int MESSAGE_ON_GROUND_TRUTH_LOCATION = 1;
	//
	double mHeadingGroundTruth;
	double mHeadingRoad;
	int mCreateFlag;
	Button mButtonStart;
	GoogleMap mMap;
	InternalGroundTruthLocationUpdateReceiver mGroundTruthLocationUpdateReceiver;
	InternalHandler mHandler = new InternalHandler(this);
	InternalInVehicleLocationUpdateReceiver mInVehicleLocationUpdateReceiver;
	List<Circle> mInVehiclePointMarkers = new ArrayList<Circle>();
	List<Location> mGroundTruthLocations = new ArrayList<Location>();
	List<PathsenseInVehicleLocation> mInVehicleLocations = new ArrayList<PathsenseInVehicleLocation>();
	Marker mMarkerGroundTruth;
	Marker mMarkerInVehicle;
	Polyline mPolylineGroundTruth;
	Polyline mPolylineInVehicle;
	SharedPreferences mPreferences;
	//
	void drawPolylineGroundTruth(Location groundTruthLocation)
	{
		final List<Location> groundTruthLocations = mGroundTruthLocations;
		final GoogleMap map = mMap;
		//
		if (groundTruthLocations != null && map != null)
		{
			int numGroundTruthLocations = groundTruthLocations.size();
			if (numGroundTruthLocations > 0)
			{
				long timestamp = System.currentTimeMillis();
				for (int i = numGroundTruthLocations - 1; i > -1; i--)
				{
					Location q_groundTruthLocation = groundTruthLocations.get(i);
					if ((timestamp - q_groundTruthLocation.getTime()) > 60000)
					{
						groundTruthLocations.remove(i);
						numGroundTruthLocations--;
					}
				}
			}
			groundTruthLocations.add(0, groundTruthLocation);
			numGroundTruthLocations++;
			//
			if (numGroundTruthLocations > 1)
			{
				PolylineOptions polylineOptions = new PolylineOptions();
				polylineOptions.width(15);
				polylineOptions.color(Color.RED);
				polylineOptions.geodesic(true);
				for (int i = 0; i < numGroundTruthLocations; i++)
				{
					Location q_groundTruthLocation = groundTruthLocations.get(i);
					polylineOptions.add(new LatLng(q_groundTruthLocation.getLatitude(), q_groundTruthLocation.getLongitude()));
				}
				if (mPolylineGroundTruth != null)
				{
					mPolylineGroundTruth.remove();
				}
				mPolylineGroundTruth = map.addPolyline(polylineOptions);
			}
		}
	}
	void drawPolylineInVehicle(PathsenseInVehicleLocation inVehicleLocation)
	{
		final List<PathsenseInVehicleLocation> inVehicleLocations = mInVehicleLocations;
		final List<Circle> inVehiclePointMarkers = mInVehiclePointMarkers;
		final GoogleMap map = mMap;
		//
		if (inVehicleLocations != null && inVehiclePointMarkers != null && map != null)
		{
			int numInVehicleLocations = inVehicleLocations.size();
			if (numInVehicleLocations > 0)
			{
				long timestamp = System.currentTimeMillis();
				for (int i = numInVehicleLocations - 1; i > -1; i--)
				{
					PathsenseInVehicleLocation q_inVehicleLocation = inVehicleLocations.get(i);
					if ((timestamp - q_inVehicleLocation.getTime()) > 60000)
					{
						inVehicleLocations.remove(i);
					}
				}
			}
			int numInVehiclePointMarkers = inVehiclePointMarkers.size();
			if (numInVehiclePointMarkers > 0)
			{
				for (int i = numInVehiclePointMarkers - 1; i > -1; i--)
				{
					Circle inVehiclePointMarker = inVehiclePointMarkers.remove(i);
					inVehiclePointMarker.remove();
				}
			}
			List<PathsenseInVehicleLocation> points = inVehicleLocation.getPoints();
			int numPoints = points != null ? points.size() : 0;
			if (numPoints > 0)
			{
				for (int i = 0; i < numPoints; i++)
				{
					PathsenseInVehicleLocation point = points.get(i);
					inVehicleLocations.add(0, point);
					//
					Circle inVehiclePointMarker = map.addCircle((new CircleOptions()).center(new LatLng(point.getLatitude(), point.getLongitude())).fillColor(Color.BLACK).strokeColor(Color.BLACK).strokeWidth(5).radius(5));
					inVehiclePointMarkers.add(inVehiclePointMarker);
				}
			}
			numInVehicleLocations = inVehicleLocations.size();
			//
			if (numInVehicleLocations > 1)
			{
				PolylineOptions polylineOptions = new PolylineOptions();
				polylineOptions.width(15);
				polylineOptions.color(Color.BLUE);
				polylineOptions.geodesic(true);
				for (int i = 0; i < numInVehicleLocations; i++)
				{
					PathsenseInVehicleLocation q_inVehicleLocation = inVehicleLocations.get(i);
					polylineOptions.add(new LatLng(q_inVehicleLocation.getLatitude(), q_inVehicleLocation.getLongitude()));
				}
				if (mPolylineInVehicle != null)
				{
					mPolylineInVehicle.remove();
				}
				mPolylineInVehicle = map.addPolyline(polylineOptions);
			}
		}
	}
	boolean isStarted()
	{
		final SharedPreferences preferences = mPreferences;
		//
		if (preferences != null)
		{
			return preferences.getInt("startedFlag", 0) == 1;
		}
		return false;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		//
		mPreferences = getSharedPreferences("PathsenseInVehicleLocationDemoPreferences", MODE_PRIVATE);
		// receivers
		mButtonStart = (Button) findViewById(R.id.buttonStart);
		mButtonStart.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				final Button buttonStart = mButtonStart;
				final SharedPreferences preferences = mPreferences;
				//
				if (buttonStart != null && preferences != null)
				{
					if (isStarted())
					{
						// turn-off switch
						SharedPreferences.Editor editor = preferences.edit();
						editor.putInt("startedFlag", 0);
						editor.commit();
						// stop service
						Intent stopIntent = new Intent(MapActivity.this, PathsenseInVehicleLocationUpdateRunnerService.class);
						stopIntent.setAction("stop");
						startService(stopIntent);
						// stop updates
						stopUpdates();
					} else
					{
						// turn-on switch
						SharedPreferences.Editor editor = preferences.edit();
						editor.putInt("startedFlag", 1);
						editor.commit();
						// start service
						Intent startIntent = new Intent(MapActivity.this, PathsenseInVehicleLocationUpdateRunnerService.class);
						startIntent.setAction("start");
						startService(startIntent);
						// start updates
						startUpdates();
					}
				}
			}
		});
		mButtonStart.setEnabled(false);
		// Obtain the MapFragment and set the async listener to be notified when the map is ready.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		//
		mCreateFlag = 1;
	}
	@Override
	public void onLocationChanged(Location location)
	{
		final GoogleMap map = mMap;
		final Button buttonStart = mButtonStart;
		//
		if (map != null && buttonStart != null)
		{
			LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
			map.moveCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder()).target(position).zoom(18).build()));
			// initialize markers
			mMarkerGroundTruth = mMap.addMarker((new MarkerOptions()).icon(BitmapDescriptorFactory.fromResource(R.drawable.red_arrow)).anchor(.5f, .5f).position(position).title("GroundTruth"));
			//
			if (isStarted())
			{
				startUpdates();
			} else
			{
				stopUpdates();
			}
			buttonStart.setEnabled(true);
		}
	}
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;
		FusedLocationManager.getInstance(this).requestLocationUpdate(this);
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		//
		if (isStarted())
		{
			stopUpdates();
		}
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
		if (mCreateFlag == 0)
		{
			if (isStarted())
			{
				startUpdates();
			}
		}
		mCreateFlag = 0;
	}
	@Override
	public void onStatusChanged(String s, int i, Bundle bundle)
	{
	}
	void removePolylineGroundTruth()
	{
		final List<Location> groundTruthLocations = mGroundTruthLocations;
		//
		if (groundTruthLocations != null)
		{
			int numGroundTruthLocations = groundTruthLocations.size();
			if (numGroundTruthLocations > 0)
			{
				for (int i = numGroundTruthLocations - 1; i > -1; i--)
				{
					groundTruthLocations.remove(i);
				}
			}
			if (mPolylineGroundTruth != null)
			{
				mPolylineGroundTruth.remove();
				mPolylineGroundTruth = null;
			}
		}
	}
	void removePolylineInVehicle()
	{
		final List<PathsenseInVehicleLocation> inVehicleLocations = mInVehicleLocations;
		final List<Circle> inVehiclePointMarkers = mInVehiclePointMarkers;
		//
		if (inVehicleLocations != null && inVehiclePointMarkers != null)
		{
			int numInVehicleLocations = inVehicleLocations.size();
			if (numInVehicleLocations > 0)
			{
				for (int i = numInVehicleLocations - 1; i > -1; i--)
				{
					inVehicleLocations.remove(i);
				}
			}
			int numInVehiclePointMarkers = inVehiclePointMarkers.size();
			if (numInVehiclePointMarkers > 0)
			{
				for (int i = numInVehiclePointMarkers - 1; i > -1; i--)
				{
					Circle inVehiclePointMarker = inVehiclePointMarkers.remove(i);
					inVehiclePointMarker.remove();
				}
			}
			if (mPolylineInVehicle != null)
			{
				mPolylineInVehicle.remove();
				mPolylineInVehicle = null;
			}
		}
	}
	void startUpdates()
	{
		final Button buttonStart = mButtonStart;
		//
		if (buttonStart != null)
		{
			// cleanup
			removePolylineInVehicle();
			removePolylineGroundTruth();
			// register for updates
			LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
			if (mGroundTruthLocationUpdateReceiver == null)
			{
				mGroundTruthLocationUpdateReceiver = new InternalGroundTruthLocationUpdateReceiver(this);
			}
			localBroadcastManager.registerReceiver(mGroundTruthLocationUpdateReceiver, new IntentFilter("groundTruthLocationUpdate"));
			if (mInVehicleLocationUpdateReceiver == null)
			{
				mInVehicleLocationUpdateReceiver = new InternalInVehicleLocationUpdateReceiver(this);
			}
			localBroadcastManager.registerReceiver(mInVehicleLocationUpdateReceiver, new IntentFilter("inVehicleLocationUpdate"));
			// set stop button
			buttonStart.setText("Stop");
		}
	}
	void stopUpdates()
	{
		final Button buttonStart = mButtonStart;
		//
		if (buttonStart != null)
		{
			// unregister for updates
			LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
			if (mGroundTruthLocationUpdateReceiver != null)
			{
				localBroadcastManager.unregisterReceiver(mGroundTruthLocationUpdateReceiver);
			}
			if (mInVehicleLocationUpdateReceiver != null)
			{
				localBroadcastManager.unregisterReceiver(mInVehicleLocationUpdateReceiver);
			}
			// set start button
			buttonStart.setText("Start");
		}
	}
	double unwrapHeading(double heading1, double heading2)
	{
		while (heading1 >= heading2 + 180)
		{
			heading1 -= 360;
		}
		while (heading1 < heading2 - 180)
		{
			heading1 += 360;
		}
		return heading1;
	}
}
