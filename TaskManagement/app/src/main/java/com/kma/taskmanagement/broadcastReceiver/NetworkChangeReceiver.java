package com.kma.taskmanagement.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kma.taskmanagement.data.local.DatabaseHelper;
import com.kma.taskmanagement.listener.NetworkReceiverCallback;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.NetworkUtil;
import com.kma.taskmanagement.utils.Utils;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Utils.isNetworkConnected(context)) {
            context.sendBroadcast(new Intent(Constants.DATA_SAVED_BROADCAST));
        }
    }
}
