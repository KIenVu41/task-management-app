package com.kma.taskmanagement;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.kma.taskmanagement.broadcastReceiver.NetworkChangeReceiver;

import com.kma.taskmanagement.listener.LogoutListener;


import java.util.Timer;
import java.util.TimerTask;

public class TaskApplication extends Application {
    private static Context appContext;
    private BroadcastReceiver mNetworkReceiver;
    private static LogoutListener logoutListener = null;
    private static Timer timer = null;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
        //ApplockManager.getInstance().enableDefaultAppLockIfAvailable(TaskApplication.this);
    }

    public static Context getAppContext() {
        return appContext;
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
            registerReceiver(mNetworkReceiver, intentFilter);
            //registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        }
    }

    public static void userSessionStart() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (logoutListener != null) {
                    logoutListener.onSessionLogout();
                    Log.d("App", "Session Destroyed");
                }
            }
        },  (1000 * 10) );
    }

    public static void resetSession() {
        userSessionStart();
    }

    public static void registerSessionListener(LogoutListener listener) {
        logoutListener = listener;
    }
}
