package com.kma.taskmanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;

import com.kma.taskmanagement.TaskApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class SharedPreferencesUtil {
    private static SharedPreferencesUtil instance;

    private SharedPreferences privateSharedPreferences;

    private SharedPreferencesUtil() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                this.privateSharedPreferences =  EncryptedSharedPreferences.create(TaskApplication.getAppContext(),
                        Constants.SHARED_PREFERENCES_KEY_ENCRYPT, Objects.requireNonNull(getMasterKey()), EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.privateSharedPreferences = TaskApplication.getAppContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        }
    }

    public static SharedPreferencesUtil getInstance(Context context) {

        synchronized (SharedPreferencesUtil.class) {
            if (instance == null) {
                instance = new SharedPreferencesUtil();
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

    public void storeBytesInSharedPreferences(String key, byte[] bytes) {
        SharedPreferences.Editor editor = privateSharedPreferences.edit();
        String byteStr = Base64.encodeToString(bytes, Base64.DEFAULT);
        editor.putString(key, byteStr);
        editor.apply();
    }

    public byte[] getBytesFromSharedPreferences(String key) {
        String stringFromSharedPrefs =  privateSharedPreferences.getString(key, "");
        return Base64.decode(stringFromSharedPrefs, Base64.DEFAULT);
    }

    public void storeUserToken(String key, String content) {

        storeStringInSharedPreferences(key, content);
    }

    public String getUserToken(String key) {

        return getStringFromSharedPreferences(key);
    }

    private MasterKey getMasterKey() {
        try {
            MasterKey masterKey = new MasterKey.Builder(TaskApplication.getAppContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return masterKey;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
