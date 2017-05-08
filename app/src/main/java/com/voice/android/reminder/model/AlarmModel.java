package com.voice.android.reminder.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lebron on 17-4-28.
 */

public class AlarmModel implements Parcelable {
    public static final int ALARM_ONCE = 0;
    public static final int ALARM_EVERYDAY = 1;
    public static final int ALARM_BYWEEK = 2;
    public static final int ALARM_RING_AND_VIBRATE = 2;
    public static final int ALARM_RING = 1;
    public static final int ALARM_VIBRATE = 0;
    private int id;
    private int hour;
    private int minute;
    private boolean isOpen;
    private int vibrate;
    private String describe;
    private int flag;
    private int week;
    private String ringtoneName;
    private String ringtoneUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getVibrate() {
        return vibrate;
    }

    public void setVibrate(int vibrate) {
        this.vibrate = vibrate;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getRingtoneName() {
        return ringtoneName;
    }

    public void setRingtoneName(String ringtoneName) {
        this.ringtoneName = ringtoneName;
    }

    public String getRingtoneUrl() {
        return ringtoneUrl;
    }

    public void setRingtoneUrl(String ringtoneUrl) {
        this.ringtoneUrl = ringtoneUrl;
    }

    @Override
    public String toString() {
        return " id = " + id + " hour " + hour + " minute " + minute + " vibrate " + vibrate + " describe " + describe + " isopen " + isOpen;
    }


    public AlarmModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.hour);
        dest.writeInt(this.minute);
        dest.writeByte(this.isOpen ? (byte) 1 : (byte) 0);
        dest.writeInt(this.vibrate);
        dest.writeString(this.describe);
        dest.writeInt(this.flag);
        dest.writeInt(this.week);
        dest.writeString(this.ringtoneName);
        dest.writeString(this.ringtoneUrl);
    }

    protected AlarmModel(Parcel in) {
        this.id = in.readInt();
        this.hour = in.readInt();
        this.minute = in.readInt();
        this.isOpen = in.readByte() != 0;
        this.vibrate = in.readInt();
        this.describe = in.readString();
        this.flag = in.readInt();
        this.week = in.readInt();
        this.ringtoneName = in.readString();
        this.ringtoneUrl = in.readString();
    }

    public static final Creator<AlarmModel> CREATOR = new Creator<AlarmModel>() {
        @Override
        public AlarmModel createFromParcel(Parcel source) {
            return new AlarmModel(source);
        }

        @Override
        public AlarmModel[] newArray(int size) {
            return new AlarmModel[size];
        }
    };
}
