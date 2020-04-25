/*
 * Copyright (c) 2020 PathSense, Inc.
 */

package com.pathsense.activitydemo.app;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import com.pathsense.android.sdk.location.PathsenseNotificationFactory;

public class PathsenseActivityDemoNotificationFactory extends PathsenseNotificationFactory {

    @Override
    protected Notification createForegroundNotification(Context context) {
        Resources resources = context.getResources();
        PendingIntent contentIntent = PendingIntent.getActivity(context, 2020, MainActivity.createIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context).setSmallIcon(R.drawable.pathsense).setTicker(resources.getString(R.string.NotificationFactory_foreground_tickerText)).setWhen(System.currentTimeMillis()).setContentTitle(resources.getString(R.string.NotificationFactory_foreground_contentTitle)).setContentText(resources.getString(R.string.NotificationFactory_foreground_contentText)).setContentIntent(contentIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(getNotificationChannel(context).getId());
            return builder.build();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_MIN);
            return builder.build();
        }
        return builder.getNotification();
    }

    @TargetApi(26)
    private NotificationChannel getNotificationChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String notificationChannelId = "pathsense-activity-demo-2020";
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(notificationChannelId);
        if (notificationChannel == null) {
            String notificationChannelName = "pathsense-activitydemo-app";
            notificationChannel = new NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        return notificationChannel;
    }

}
