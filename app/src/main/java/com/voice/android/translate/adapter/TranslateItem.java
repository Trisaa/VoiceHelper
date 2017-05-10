package com.voice.android.translate.adapter;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lebron on 17-5-10.
 */

public class TranslateItem extends MultiItemEntity {
    public static final int ITEM_LEFT = 1;
    public static final int ITEM_RIGHT = 2;
    @SerializedName("query")
    private String untranslateStr;
    @SerializedName("translation")
    private String[] translatedArray;
    @SerializedName("errorCode")
    private int errorCode;

    private String translatedStr;

    public String getUntranslateStr() {
        return untranslateStr;
    }

    public void setUntranslateStr(String untranslateStr) {
        this.untranslateStr = untranslateStr;
    }

    public String getTranslatedStr() {
        if (translatedArray != null && translatedArray.length > 0) {
            translatedStr = translatedArray[0];
        }
        return translatedStr;
    }

    public void setTranslatedStr(String translatedStr) {
        this.translatedStr = translatedStr;
    }
}
