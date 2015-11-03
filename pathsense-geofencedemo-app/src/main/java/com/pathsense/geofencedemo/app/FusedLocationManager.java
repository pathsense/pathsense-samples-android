/*
 * Copyright (c) 2015 PathSense, Inc.
 */

package com.pathsense.geofencedemo.app;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
public class FusedLocationManager
{
	// ---------------------- Static Classes
	static class InternalHolder
	{
		LocationListener mListener;
		List<InternalLocationListenerFilter> mFilterList;
	}
	static class InternalLocationListenerFilter implements LocationListener
	{
		FusedLocationManager mManager;
		LocationListener mListener;
		//
		InternalLocationListenerFilter(FusedLocationManager manager, LocationListener listener)
		{
			mManager = manager;
			mListener = listener;
		}
		@Override
		public void onLocationChanged(Location location)
		{
			final FusedLocationManager manager = mManager;
			final LocationListener listener = mListener;
			//
			if (manager != null && listener != null)
			{
				if (manager.validate(location) && manager.removeUpdates(listener))
				{
					// broadcast location
					listener.onLocationChanged(location);
				}
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
		public void onStatusChanged(String s, int i, Bundle bundle)
		{
		}
	}
	// ---------------------- Static Fields
	static final String TAG = FusedLocationManager.class.getName();
	//
	static FusedLocationManager sInstance;
	// ---------------------- Static Methods
	public static synchronized FusedLocationManager getInstance(Context context)
	{
		if (sInstance == null)
		{
			sInstance = new FusedLocationManager(context);
		}
		return sInstance;
	}
	// ---------------------- Instance Fields
	LocationManager mLocationManager;
	Queue<InternalHolder> mHolders = new ConcurrentLinkedQueue<InternalHolder>();
	// ---------------------- Instance Methods
	private FusedLocationManager(Context context)
	{
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	boolean removeUpdates(LocationListener listener)
	{
		final Queue<InternalHolder> holders = mHolders;
		final LocationManager locationManager = mLocationManager;
		//
		if (holders != null && locationManager != null)
		{
			synchronized (holders)
			{
				for (Iterator<InternalHolder> q = holders.iterator(); q.hasNext(); )
				{
					InternalHolder holder = q.next();
					if (holder.mListener == listener)
					{
						List<InternalLocationListenerFilter> filters = holder.mFilterList;
						int numFilters = filters != null ? filters.size() : 0;
						for (int i = numFilters - 1; i > -1; i--)
						{
							InternalLocationListenerFilter filter = filters.remove(i);
							locationManager.removeUpdates(filter);
						}
						q.remove();
						return true;
					}
				}
			}
		}
		return false;
	}
	public void requestLocationUpdate(LocationListener listener)
	{
		final LocationManager locationManager = mLocationManager;
		final Queue<InternalHolder> holders = mHolders;
		//
		if (locationManager != null && holders != null)
		{
			List<String> providers = locationManager.getProviders(true);
			int numProviders = providers != null ? providers.size() : 0;
			if (numProviders > 0)
			{
				// broadcast last known location if valid
				for (int i = 0; i < numProviders; i++)
				{
					String provider = providers.get(i);
					Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
					if (validate(lastKnownLocation))
					{
						listener.onLocationChanged(lastKnownLocation);
						return;
					}
				}
				// request location updates
				InternalHolder holder = new InternalHolder();
				holder.mListener = listener;
				holder.mFilterList = new ArrayList<InternalLocationListenerFilter>(numProviders);
				//
				for (int i = 0; i < numProviders; i++)
				{
					String provider = providers.get(i);
					InternalLocationListenerFilter filter = new InternalLocationListenerFilter(this, listener);
					locationManager.requestLocationUpdates(provider, 0, 0, filter);
					holder.mFilterList.add(filter);
				}
				holders.add(holder);
			}
		}
	}
	boolean validate(Location location)
	{
		if (location != null)
		{
			String provider = location.getProvider();
			if (LocationManager.NETWORK_PROVIDER.equals(provider))
			{
				double accuracy = location.getAccuracy();
				long age = System.currentTimeMillis() - location.getTime();
				Log.i(TAG, "provider=" + provider + ",accuracy=" + accuracy + ",age=" + age);
				if (location.getAccuracy() <= 100.0 && age <= 20000)
				{
					return true;
				}
			} else if (LocationManager.GPS_PROVIDER.equals(provider))
			{
				if (location.getAccuracy() <= 50.0 && (System.currentTimeMillis() - location.getTime()) <= 5000)
				{
					return true;
				}
			}
		}
		return false;
	}
}
