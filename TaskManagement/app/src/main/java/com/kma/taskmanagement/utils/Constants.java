package com.kma.taskmanagement.utils;

public class Constants {
    // school 5ghz
    //public static String BASE_URL = "http://192.168.10.210:8080/";
    // home ethernet
    //public static String BASE_URL = "http://192.168.0.102:8080/api/v1/";
    // home wifi
    //public static String BASE_URL = "http://192.168.0.101:3000/api/v1/";
    // emulator
    public static final String BASE_URL = "https://10.0.2.2:443/";
    // ngrok
    //public static final String BASE_URL = "http://9250-59-153-220-161.ngrok.io/";
    // firebase
    public static final String FIREBASE_URL = "https://fcm.googleapis.com/fcm/send";

    //ws
    public static final String SERVER_PATH = "ws://10.0.2.2:5000";
    public static final String SERVER_KEY = "AAAAKkuuFeY:APA91bHLYLWUKlufwM247ExAeX6Lk9Q2Vt77GwYSJJLP9NgvmIGJcQuAzSoD0WJO5z0aGPXJreHxvMGvgjr-49tOl_E8VxVge5KvgnglZiOCD1bmOLuDYBD7AhuXfcWNAPzVvpHI80Wa";

    // Auth
    public static final String BEARER = "Bearer ";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    // shared preferences
    public static final String SHARED_PREFERENCES_KEY = "com.kma.task";
    public static final String SHARED_PREFERENCES_KEY_ENCRYPT = "com.kma.task.encrypt";
    public static final String TOKEN = "token_";
    public static final String REFRESHTOKEN = "refreshtoken_";
    public static final String INTRO = "isIntroOpnend";
    public static final String REMIND = "DATE_REMINDER_";
    public static final String SECURE = "SECURE_";
    public static final String CATE_INDEX = "CATE_INDEX_";

    // sync
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "kv.kma.datasaved";
    public static final String INVITE_BROADCAST = "kv.kma.invite";
    public static final String NO_NETWORK_BROADCAST = "kv.kma.nonetwork";

    //stringee
    public static final String SID = "SK.0.q4Z75UMNCtGhOBQkTNDqANU8EHX54PLC";
    public static final String API_KEY = "OWViTG9iQTRFNkNHQlZUYTl3aGNOY2NmazdOWFpXRA==";

    // fingerprint
    public static final int REQUESTCODE_FINGERPRINT_ENROLLMENT = 100;
    public static final int REQUESTCODE_SECURITY_SETTINGS = 101;

    // firebase channel
    public static final String CHANNEL_ID = "FIREBASE_CHANNEL";
    public static final String CHANNEL_NAME = "FIREBASE_CHANNEL_FRIEND_REQUEST";
    public static final String CHANNEL_DESC = "FIREBASE_CHANNEL_DESC";
}
