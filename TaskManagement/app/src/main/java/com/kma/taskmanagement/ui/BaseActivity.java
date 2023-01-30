package com.kma.taskmanagement.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.listener.LogoutListener;
import com.kma.taskmanagement.ui.user.LoginActivity;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

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

        TaskApplication.resetSession();

    }

    @Override
    public void onSessionLogout() {
        String key = Constants.INTRO;
        String token = Constants.TOKEN;
        SharedPreferencesUtil.getInstance(TaskApplication.getAppContext()).storeBooleanInSharedPreferences(key, false);
        SharedPreferencesUtil.getInstance(TaskApplication.getAppContext()).storeUserToken(token + GlobalInfor.username, "");
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.putExtra(GlobalInfor.actionTimeout, -1);
        startActivity(intent);
        TaskApplication.unregisterSessionListener();
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
