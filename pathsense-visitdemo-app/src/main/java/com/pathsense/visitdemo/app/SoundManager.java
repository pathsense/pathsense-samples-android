/*
 * Copyright (c) 2015 PathSense, Inc.
 */

package com.pathsense.visitdemo.app;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;
import java.util.HashSet;
public class SoundManager implements SoundPool.OnLoadCompleteListener
{
	static SoundManager sInstance;
	//
	public static synchronized SoundManager getInstance(Context context)
	{
		if (sInstance == null)
		{
			sInstance = new SoundManager(context);
		}
		return sInstance;
	}
	//
	Context mContext;
	//
	int mDing;
	int mJingle;
	HashSet<Integer> mSamplesLoaded = new HashSet<Integer>();
	HashSet<Integer> mSamplesPending = new HashSet<Integer>();
	SoundPool mSoundPool;
	//
	private SoundManager(Context context)
	{
		mContext = context;
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		mSoundPool.setOnLoadCompleteListener(this);
		try
		{
			mDing = mSoundPool.load(mContext.getAssets().openFd("sounds/ding.wav"), 1);
			mJingle = mSoundPool.load(mContext.getAssets().openFd("sounds/jingle.wav"), 1);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public void onLoadComplete(SoundPool soundPool, int i, int i2)
	{
		final HashSet<Integer> samplesLoaded = mSamplesLoaded;
		final HashSet<Integer> samplesPending = mSamplesPending;
		//
		if (samplesLoaded != null && samplesPending != null)
		{
			samplesLoaded.add(i);
			//
			if (samplesPending.remove(i))
			{
				soundPool.play(i, 1.0f, 1.0f, 1, 0, 0);
			}
		}
	}
	public void playDing()
	{
		final HashSet<Integer> samplesLoaded = mSamplesLoaded;
		final HashSet<Integer> samplesPending = mSamplesPending;
		final SoundPool soundPool = mSoundPool;
		//
		if (samplesLoaded != null && soundPool != null && samplesPending != null)
		{
			if (samplesLoaded.contains(mDing))
			{
				soundPool.play(mDing, 1.0f, 1.0f, 1, 0, 0);
			} else
			{
				samplesPending.add(mDing);
			}
		}
	}
	public void playJingle()
	{
		final HashSet<Integer> samplesLoaded = mSamplesLoaded;
		final HashSet<Integer> samplesPending = mSamplesPending;
		final SoundPool soundPool = mSoundPool;
		//
		if (samplesLoaded != null && soundPool != null && samplesPending != null)
		{
			if (samplesLoaded.contains(mJingle))
			{
				soundPool.play(mJingle, 1.0f, 1.0f, 1, 0, 0);
			} else
			{
				samplesPending.add(mJingle);
			}
		}
	}
}