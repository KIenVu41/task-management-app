package com.kma.taskmanagement.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.kma.taskmanagement.data.local.DatabaseHelper;
import com.kma.taskmanagement.listener.NetworkReceiverCallback;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            //NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//            NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//
//            if (mWifi.isConnected()) {
//                Log.d("TAG", "co");
//                //context.sendBroadcast(new Intent(Constants.DATA_SAVED_BROADCAST));
//            }


//        if (activeNetwork != null) {
//            //if connected to wifi or mobile data plan
//            // || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE
//            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//                context.sendBroadcast(new Intent(Constants.DATA_SAVED_BROADCAST));
//            }
//        }
        int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                WifiManager.WIFI_STATE_UNKNOWN);

        switch (wifiStateExtra) {
            case WifiManager.WIFI_STATE_ENABLED:
                context.sendBroadcast(new Intent(Constants.DATA_SAVED_BROADCAST));
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                break;
        }
    }
}
