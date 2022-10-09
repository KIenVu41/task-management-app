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
    String title, desc, content;
    int type;
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
        title = intent.getStringExtra("TITLE");
        desc = intent.getStringExtra("DESC");
        type = intent.getIntExtra("TYPE", 0);
        if(type == 0) {
            content = "Đến hạn công việc " + title;
        } else if(type == 1) {
            content = "Còn 3 ngày là đến hạn công việc " + title;
        } else {
            content = "Còn 7 ngày là đến hạn công việc " + title;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("111",
                    "CHANEL_REMIND",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "111")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle("Nhắc nhở") // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true); // clear notification after click
        mNotificationManager.notify(0, mBuilder.build());
    }
}