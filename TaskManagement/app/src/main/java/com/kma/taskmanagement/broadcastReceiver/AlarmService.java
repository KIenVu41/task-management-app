package com.kma.taskmanagement.broadcastReceiver;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.ui.main.MainActivity;
import com.kma.taskmanagement.ui.main.fragments.PersonTaskFragment;

public class AlarmService extends IntentService {
    private static final int NOTIFICATION_ID = 3;

    public AlarmService() {
        super(AlarmService.class.getSimpleName());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
                NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("111",
                    "CHANEL_REMIND",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "111")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle("Remind") // title for notification
                .setContentText("Deadline!!1")// message for notification
                .setAutoCancel(true); // clear notification after click
        mNotificationManager.notify(0, mBuilder.build());
    }
}