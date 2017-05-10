package com.voice.android.common.http;

import com.voice.android.translate.adapter.TranslateItem;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by lebron on 17-5-10.
 */

public interface TranslateService {
    @GET("/openapi.do?keyfrom=VoiceHelper&key=1279678304&type=data&doctype=json&version=1.1")
    Observable<TranslateItem> getTranslatedResult(@Query("q") String text);
}
