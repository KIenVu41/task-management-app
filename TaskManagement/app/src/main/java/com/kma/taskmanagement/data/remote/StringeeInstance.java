package com.kma.taskmanagement.data.remote;

import android.content.Context;

import com.kma.taskmanagement.TaskApplication;
import com.stringee.StringeeClient;
import com.stringee.common.SocketAddress;

import java.util.ArrayList;
import java.util.List;

public class StringeeInstance {

    public static StringeeClient stringeeClient = null;

    public static synchronized StringeeClient getStringeeClient(Context context) {
        if(stringeeClient == null) {
            stringeeClient = new StringeeClient(context);
            List<SocketAddress> socketAddressList = new ArrayList<>();
            socketAddressList.add(new SocketAddress("v1.stringee.com", 9879));
            socketAddressList.add(new SocketAddress("v2.stringee.com", 9879));
            stringeeClient.setHost(socketAddressList);
        }

        return stringeeClient;
    }
}
