package com.voice.android.reminder.model;

/**
 * Created by lebron on 17-4-28.
 */

public class AlarmModel {
    private long time;
    private long date;
    private boolean isOpen;
    private int ringtone;
    private String describe;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getRingtone() {
        return ringtone;
    }

    public void setRingtone(int ringtone) {
        this.ringtone = ringtone;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
