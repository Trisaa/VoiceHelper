package com.voice.android.common.utils;

/**
 * Created by LeBron on 2017/5/7.
 */

public class TimeUtils {
    public static String formatTime(int hour, int minute) {
        String hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minuteStr = minute < 10 ? "0" + minute : String.valueOf(minute);
        return hourStr + ":" + minuteStr;
    }
}
