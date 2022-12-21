package com.kma.taskmanagement.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.listener.LogoutListener;

public class BaseActivity extends AppCompatActivity implements LogoutListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskApplication.resetSession();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set Listener to receive events
        TaskApplication.registerSessionListener(this);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        //reset session when user interact
        TaskApplication.resetSession();

    }

    @Override
    public void onSessionLogout() {
        // Do You Task on session out
    }
}
