package com.voice.android.reminder;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voice.android.R;
import com.voice.android.common.utils.AudioPlayer;
import com.voice.android.common.utils.SharedPreferencesUtils;
import com.voice.android.reminder.model.AlarmModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lebron on 17-5-8.
 */

public class AlarmActivity extends Activity {
    private AlarmModel alarmModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        alarmModel = (AlarmModel) getIntent().getExtras().getParcelable("alarmModel");
        if (alarmModel != null && alarmModel.isOpen()) {
            Log.i("Lebron", " AlarmActivity onCreate");
            showDialog();
            recordAlarm();
        } else {
            finish();
        }
    }

    private void recordAlarm() {
        String string = SharedPreferencesUtils.getString(this, SharedPreferencesUtils.KEY_FINISHED_ALARM_LIST, "");
        List<AlarmModel> list = new Gson().fromJson(string, new TypeToken<List<AlarmModel>>() {
        }.getType());
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(alarmModel);
        SharedPreferencesUtils.saveString(this, SharedPreferencesUtils.KEY_FINISHED_ALARM_LIST, new Gson().toJson(list));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayer.getInstance(AlarmActivity.this).stop();
    }

    private void showDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_alarm, null);
        TextView labelTextView = (TextView) view.findViewById(R.id.dialog_alarm_label_txv);
        labelTextView.setText(alarmModel.getDescribe());

        if (alarmModel.getVibrate() == 2) {
            AudioPlayer.getInstance(this).play(alarmModel.getRingtoneUrl() == null ? "" : alarmModel.getRingtoneUrl(), true, true);
        } else if (alarmModel.getVibrate() == 1) {
            AudioPlayer.getInstance(this).play(alarmModel.getRingtoneUrl() == null ? "" : alarmModel.getRingtoneUrl(), true, false);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.dialog_alarm_ok_txv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioPlayer.getInstance(AlarmActivity.this).stop();
                alertDialog.dismiss();
                finish();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AudioPlayer.getInstance(AlarmActivity.this).stop();
                finish();
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
