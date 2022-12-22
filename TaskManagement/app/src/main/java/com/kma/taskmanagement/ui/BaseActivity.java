package com.kma.taskmanagement.ui;

import android.content.Intent;
import android.os.Bundle;

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
        //reset session when user interact
        TaskApplication.resetSession();

    }

    @Override
    public void onSessionLogout() {
        // Do You Task on session out
        String key = Constants.INTRO;
        String token = Constants.TOKEN;
        SharedPreferencesUtil.getInstance(TaskApplication.getAppContext()).storeBooleanInSharedPreferences(key, false);
        SharedPreferencesUtil.getInstance(TaskApplication.getAppContext()).storeUserToken(token + GlobalInfor.username, "");
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialog();
            }
        });
        startActivity(intent);
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BaseActivity.this);

        alertDialogBuilder.setTitle("Your Title");
        alertDialogBuilder.setMessage("Message here!").setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
    }
}
