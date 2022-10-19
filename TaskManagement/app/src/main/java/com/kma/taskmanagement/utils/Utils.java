package com.kma.taskmanagement.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
    private static final long CACHE_SIZE = 10 * 1024 * 1024;
    private static final int READ_TIMEOUT = 5000;
    private static final int WRITE_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 5000;
    private static String CACHE_CONTROL = "Cache-Control";
    private static final String TIME_CACHE_ONLINE = "public, max-age = 60";// 1 minute
    private static final String TIME_CACHE_OFFLINE = "public, only-if-cached, max-stale = 86400";//1 day

    public static boolean isNetworkConnected( Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
