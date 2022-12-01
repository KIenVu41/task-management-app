package com.kma.taskmanagement.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.IdRes;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.TaskApplication;

public class NotiUltils {

    public static void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Constants.CHANNEL_NAME;
            String desc = Constants.CHANNEL_DESC;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, name, importance);
            channel.setDescription(desc);

            NotificationManager notificationManager = TaskApplication.getAppContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
}
