package com.kma.taskmanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    public static final String SHARED_PREFERENCES_KEY = "com.kma.task";

    private static SharedPreferencesUtil instance;

    private SharedPreferences privateSharedPreferences;

    private SharedPreferencesUtil(Context context) {

        this.privateSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance(Context context) {

        synchronized (SharedPreferencesUtil.class) {
            if (instance == null) {
                instance = new SharedPreferencesUtil(context);
            }
            return instance;
        }
    }

    private void storeStringInSharedPreferences(String key, String content) {

        SharedPreferences.Editor editor = privateSharedPreferences.edit();
        editor.putString(key, content);
        editor.apply();
    }

    private String getStringFromSharedPreferences(String key) {

        return privateSharedPreferences.getString(key, "");
    }

    public void storeUserToken(String key, String content) {

        storeStringInSharedPreferences(key, content);
    }

    public String getUserToken(String key) {

        return getStringFromSharedPreferences(key);
    }
}
