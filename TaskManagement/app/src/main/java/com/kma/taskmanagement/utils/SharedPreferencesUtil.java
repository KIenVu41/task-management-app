package com.kma.taskmanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    private static SharedPreferencesUtil instance;

    private SharedPreferences privateSharedPreferences;

    private SharedPreferencesUtil(Context context) {

        this.privateSharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance(Context context) {

        synchronized (SharedPreferencesUtil.class) {
            if (instance == null) {
                instance = new SharedPreferencesUtil(context);
            }
            return instance;
        }
    }

    public void storeStringInSharedPreferences(String key, String content) {

        SharedPreferences.Editor editor = privateSharedPreferences.edit();
        editor.putString(key, content);
        editor.apply();
    }

    public String getStringFromSharedPreferences(String key) {

        return privateSharedPreferences.getString(key, "");
    }

    public void storeIntInSharedPreferences(String key, int content) {
        SharedPreferences.Editor editor = privateSharedPreferences.edit();
        editor.putInt(key, content);
        editor.apply();
    }

    public int getIntFromSharedPreferences(String key) {
        return privateSharedPreferences.getInt(key, 0);
    }

    public void storeLongInSharedPreferences(String key, long content) {
        SharedPreferences.Editor editor = privateSharedPreferences.edit();
        editor.putLong(key, content);
        editor.apply();
    }

    public long getLongFromSharedPreferences(String key) {
        return privateSharedPreferences.getLong(key, -1);
    }

    public void storeBooleanInSharedPreferences(String key, boolean content) {
        SharedPreferences.Editor editor = privateSharedPreferences.edit();
        editor.putBoolean(key, content);
        editor.apply();
    }

    public Boolean getBooleanFromSharedPreferences(String key) {
        return privateSharedPreferences.getBoolean(key, false);
    }

    public void storeUserToken(String key, String content) {

        storeStringInSharedPreferences(key, content);
    }

    public String getUserToken(String key) {

        return getStringFromSharedPreferences(key);
    }
}
