package com.voice.android.quicknote;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lebron on 17-5-9.
 */

public class NoteModel implements Parcelable {
    private String time;
    private String content;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.time);
        dest.writeString(this.content);
    }

    public NoteModel() {
    }

    protected NoteModel(Parcel in) {
        this.time = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<NoteModel> CREATOR = new Parcelable.Creator<NoteModel>() {
        @Override
        public NoteModel createFromParcel(Parcel source) {
            return new NoteModel(source);
        }

        @Override
        public NoteModel[] newArray(int size) {
            return new NoteModel[size];
        }
    };
}
