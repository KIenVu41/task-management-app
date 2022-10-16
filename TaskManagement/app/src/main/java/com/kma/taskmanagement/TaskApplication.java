package com.kma.taskmanagement;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.ViewModelProvider;

import com.kma.taskmanagement.data.repository.GroupRepository;
import com.kma.taskmanagement.data.repository.impl.GroupRepositoryImpl;
import com.kma.taskmanagement.ui.main.GroupViewModel;
import com.kma.taskmanagement.ui.main.GroupViewModelFactory;

public class TaskApplication extends Application {
    private static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
