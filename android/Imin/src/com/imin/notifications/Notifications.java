package com.imin.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.imin.MainActivity;
import com.imin.R;

public class Notifications {

	public static int notificationId = 0;

	public static void notify(Context context, String title, String message, Bitmap largeIcon) {
		// Build the notification
		NotificationCompat.Builder notification = new NotificationCompat.Builder(context);

		// Fill values
		notification.setSmallIcon(R.drawable.img_icon);
		notification.setLargeIcon(largeIcon);
		notification.setContentTitle(title);
		notification.setContentText(message);
		notification.setAutoCancel(true);
		notification.setDefaults(Notification.DEFAULT_ALL);

		// Create the intent
		Intent resultIntent = new Intent(context, MainActivity.class);

		// Intent matters...
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setContentIntent(resultPendingIntent);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationId++, notification.build());
	}

}
