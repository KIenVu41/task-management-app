package com.kma.taskmanagement.utils;

public class Constants {
    // school 5ghz
    //public static String BASE_URL = "http://192.168.10.210:8080/";
    // home ethernet
    //public static String BASE_URL = "http://192.168.0.102:8080/api/v1/";
    // home wifi
//    public static String BASE_URL = "http://192.168.0.101:3000/api/v1/";
    // emulator
    public static final String BASE_URL = "http://10.0.2.2:3000/";

    // Auth
    public static final String BEARER = "Bearer ";
    // shared preferences
    public static final String SHARED_PREFERENCES_KEY = "com.kma.task";
    public static final String TOKEN = "token_";
    public static final String INTRO = "isIntroOpnend";
    public static final String REMIND = "DATE_REMINDER_";
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
}
