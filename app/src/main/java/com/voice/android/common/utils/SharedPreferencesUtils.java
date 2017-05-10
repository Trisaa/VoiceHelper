package com.voice.android.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    public static final String KEY_SETTED_ALARM_ID = "KEY_SETTED_ALARM_ID";
    public static final String KEY_SETTED_ALARM_LIST = "KEY_SETTED_ALARM_LIST";
    public static final String KEY_FINISHED_ALARM_LIST = "KEY_FINISHED_ALARM_LIST";
    public static final String KEY_SETTED_NOTE_LIST = "KEY_SETTED_NOTE_LIST";
    public static final String KEY_RAIN_NOTIFY = "KEY_RAIN_NOTIFY";
    public static final String KEY_COOL_NOTIFY = "KEY_COOL_NOTIFY";
    public static final String KEY_WIFI_NOTIFY = "KEY_WIFI_NOTIFY";

    public static SharedPreferences getSharedPreferenced(Context context) {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    /**
     * 保存boolean型到SharedPreferences
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveBoolean(Context context, String key, boolean value) {
        getSharedPreferenced(context).edit().putBoolean(key, value).commit();
    }

    /**
     * 从SharedPreferences读取boolean型
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getSharedPreferenced(context).getBoolean(key, defValue);
    }

    /**
     * 保存String型到SharedPreferences
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveString(Context context, String key, String value) {
        getSharedPreferenced(context).edit().putString(key, value).commit();
    }

    /**
     * 从SharedPreferebces中获取String
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        return getSharedPreferenced(context).getString(key, defValue);
    }

    public static void saveInt(Context context, String key, int value) {
        getSharedPreferenced(context).edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        return getSharedPreferenced(context).getInt(key, defValue);
    }

    public static void saveLong(Context context, String key, long value) {
        getSharedPreferenced(context).edit().putLong(key, value).commit();
    }

    public static long getLong(Context context, String key, long defValue) {
        return getSharedPreferenced(context).getLong(key, defValue);
    }
}
