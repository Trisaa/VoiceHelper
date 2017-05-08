package com.voice.android.common.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.voice.android.reminder.AlarmActivity;
import com.voice.android.reminder.model.AlarmModel;

/**
 * Created by LeBron on 2017/5/7.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        long intervalMillis = intent.getExtras().getLong("intervalMillis", 0);
        if (intervalMillis != 0) {
            AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis,
                    intent);
        }
        AlarmModel alarmModel = intent.getExtras().getParcelable("alarmModel");
        Log.i("Lebron", "  闹钟响了  " + alarmModel.toString());
        Intent clockIntent = new Intent(context, AlarmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("alarmModel", alarmModel);
        clockIntent.putExtras(bundle);
        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(clockIntent);
    }

}
