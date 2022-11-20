package com.kma.taskmanagement;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.lifecycle.ViewModelProvider;

import com.kma.taskmanagement.broadcastReceiver.NetworkChangeReceiver;
import com.kma.taskmanagement.data.repository.GroupRepository;
import com.kma.taskmanagement.data.repository.impl.GroupRepositoryImpl;
import com.kma.taskmanagement.ui.main.GroupViewModel;
import com.kma.taskmanagement.ui.main.GroupViewModelFactory;

public class TaskApplication extends Application {
    private static Context appContext;
    private BroadcastReceiver mNetworkReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
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
}
