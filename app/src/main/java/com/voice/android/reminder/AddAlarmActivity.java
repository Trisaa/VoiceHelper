package com.voice.android.reminder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voice.android.R;
import com.voice.android.common.base.BaseActivity;
import com.voice.android.common.utils.AlarmManagerUtil;
import com.voice.android.common.utils.SharedPreferencesUtils;
import com.voice.android.common.utils.TimeUtils;
import com.voice.android.reminder.model.AlarmModel;
import com.voice.android.reminder.model.RingtoneModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.voice.android.common.utils.AlarmManagerUtil.ALARM_ACTION;

/**
 * Created by LeBron on 2017/5/2.
 */

public class AddAlarmActivity extends BaseActivity {
    private static final String KEY_ALARM_MODEL = "KEY_ALARM_MODEL";
    public static final String KEY_ADD_ALARM = "KEY_ADD_ALARM";
    @BindView(R.id.add_alarm_time_txv)
    TextView mTimeView;
    @BindView(R.id.add_alarm_label_txv)
    TextView mLabelView;
    @BindView(R.id.add_alarm_vibrate_switch)
    SwitchCompat mVibrateSwitch;
    @BindView(R.id.add_alarm_repeat_txv)
    TextView mRepeatView;
    @BindView(R.id.add_alarm_delete_btn)
    Button mDeleteBtn;
    @BindView(R.id.add_alarm_ringtone_txv)
    TextView mRingtoneView;

    private int hour, minute, tempHour, tempMinute;
    private boolean vibrate;
    private String label, ringtoneName, ringtoneUrl;
    private int week, flag;
    private AlarmModel mAlarmModel;
    private boolean isAddedAlarm;

    public static void start(Context context) {
        Intent intent = new Intent(context, AddAlarmActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, AlarmModel model) {
        Intent intent = new Intent(context, AddAlarmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_ALARM_MODEL, model);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_add_alarm;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlarmModel = getIntent().getParcelableExtra(KEY_ALARM_MODEL);
        initView();
    }

    private void initView() {
        if (mAlarmModel != null) {
            isAddedAlarm = true;
            hour = mAlarmModel.getHour();
            minute = mAlarmModel.getMinute();
            label = mAlarmModel.getDescribe();
            vibrate = mAlarmModel.getVibrate() != AlarmModel.ALARM_RING;
            flag = mAlarmModel.getFlag();
            week = mAlarmModel.getWeek();
            ringtoneName = mAlarmModel.getRingtoneName();
            ringtoneUrl = mAlarmModel.getRingtoneUrl();
            mTimeView.setText(TimeUtils.formatTime(hour, minute));
            mLabelView.setText(label);
            mRingtoneView.setText(TextUtils.isEmpty(ringtoneName) ? "无" : ringtoneName);
            mVibrateSwitch.setChecked(vibrate);
            switch (flag) {
                case AlarmModel.ALARM_ONCE:
                    mRepeatView.setText("只响一次");
                    break;
                case AlarmModel.ALARM_EVERYDAY:
                    mRepeatView.setText("每天");
                    break;
                case AlarmModel.ALARM_BYWEEK:
                    mRepeatView.setText("每周" + week);
                    break;
            }
            mDeleteBtn.setVisibility(View.VISIBLE);
        }

        mVibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vibrate = isChecked;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reminder_add_alarm_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ok) {
            String alarmStr = SharedPreferencesUtils.getString(this, SharedPreferencesUtils.KEY_SETTED_ALARM_LIST, "");
            List<AlarmModel> list = new Gson().fromJson(alarmStr, new TypeToken<List<AlarmModel>>() {
            }.getType());
            if (list == null) {
                list = new ArrayList<>();
            }

            if (mAlarmModel == null) {
                Log.i("Lebron", " mAlarmModel == null ");
                mAlarmModel = new AlarmModel();
                int id = SharedPreferencesUtils.getInt(this, SharedPreferencesUtils.KEY_SETTED_ALARM_ID, 0);
                id++;
                mAlarmModel.setId(id);
                SharedPreferencesUtils.saveInt(this, SharedPreferencesUtils.KEY_SETTED_ALARM_ID, id);
            }
            mAlarmModel.setMinute(minute);
            mAlarmModel.setHour(hour);
            mAlarmModel.setDescribe(label);
            mAlarmModel.setVibrate(vibrate ? AlarmModel.ALARM_RING_AND_VIBRATE : AlarmModel.ALARM_RING);
            mAlarmModel.setOpen(true);
            mAlarmModel.setFlag(flag);
            mAlarmModel.setWeek(week);
            mAlarmModel.setRingtoneName(ringtoneName);
            mAlarmModel.setRingtoneUrl(ringtoneUrl);

            if (isAddedAlarm) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId() == mAlarmModel.getId()) {
                        list.set(i, mAlarmModel);
                        break;
                    }
                }
            } else {
                list.add(mAlarmModel);
            }

            AlarmManagerUtil.setAlarm(this, mAlarmModel);
            SharedPreferencesUtils.saveString(this, SharedPreferencesUtils.KEY_SETTED_ALARM_LIST, new Gson().toJson(list));
            Log.i("Lebron", mAlarmModel.toString());
            EventBus.getDefault().post(KEY_ADD_ALARM);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void getRingtone(RingtoneModel model) {
        if (model != null) {
            ringtoneName = model.getName();
            ringtoneUrl = model.getUrl();
            mRingtoneView.setText(model.getName());
        }
    }

    @OnClick(R.id.add_alarm_time_layout)
    public void chooseTime() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_choose_time, null);
        TimePicker timePicker = (TimePicker) view.findViewById(R.id.alarm_choose_time_picker);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.i("Lebron", " " + hourOfDay + " " + minute);
                tempHour = hourOfDay;
                tempMinute = minute;
            }
        });
        view.findViewById(R.id.account_update_submit_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAlarmActivity.this.hour = tempHour;
                AddAlarmActivity.this.minute = tempMinute;
                mTimeView.setText(TimeUtils.formatTime(AddAlarmActivity.this.hour, AddAlarmActivity.this.minute));
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @OnClick(R.id.add_alarm_label_layout)
    public void chooseLabel() {
        View view = LayoutInflater.from(this).inflate(R.layout.common_input_dialog, null);
        final EditText editText = (EditText) view.findViewById(R.id.account_update_edt);
        TextView accountSubmitOk = (TextView) view.findViewById(R.id.account_update_submit_txt);
        if (editText.getText() != null) {
            accountSubmitOk.setTextColor(ContextCompat.getColor(this, R.color.account_update_submit));
        }
        editText.setText(mLabelView.getText());
        editText.setSelection(mLabelView.getText().length());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.account_update_submit_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    label = editText.getText().toString();
                    mLabelView.setText(label);
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @OnClick(R.id.add_alarm_repeat_layout)
    public void chooseRepeat() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_choose_repeat, null);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.dialog_repeat_radiogroup);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        if (mAlarmModel != null) {
            if (mAlarmModel.getFlag() == AlarmModel.ALARM_ONCE) {
                ((RadioButton) view.findViewById(R.id.dialog_repeat_once_radiobtn)).setChecked(true);
            } else if (mAlarmModel.getFlag() == AlarmModel.ALARM_EVERYDAY) {
                ((RadioButton) view.findViewById(R.id.dialog_repeat_everyday_radiobtn)).setChecked(true);
            } else {
                switch (mAlarmModel.getWeek()) {
                    case 1:
                        ((RadioButton) view.findViewById(R.id.dialog_repeat_monday_radiobtn)).setChecked(true);
                        break;
                    case 2:
                        ((RadioButton) view.findViewById(R.id.dialog_repeat_tuesday_radiobtn)).setChecked(true);
                        break;
                    case 3:
                        ((RadioButton) view.findViewById(R.id.dialog_repeat_wednesday_radiobtn)).setChecked(true);
                        break;
                    case 4:
                        ((RadioButton) view.findViewById(R.id.dialog_repeat_thursday_radiobtn)).setChecked(true);
                        break;
                    case 5:
                        ((RadioButton) view.findViewById(R.id.dialog_repeat_friday_radiobtn)).setChecked(true);
                        break;
                    case 6:
                        ((RadioButton) view.findViewById(R.id.dialog_repeat_saturday_radiobtn)).setChecked(true);
                        break;
                    case 7:
                        ((RadioButton) view.findViewById(R.id.dialog_repeat_sunday_radiobtn)).setChecked(true);
                        break;
                }
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.dialog_repeat_once_radiobtn:
                        mRepeatView.setText("只响一次");
                        flag = AlarmModel.ALARM_ONCE;
                        week = 0;
                        break;
                    case R.id.dialog_repeat_everyday_radiobtn:
                        mRepeatView.setText("每天");
                        flag = AlarmModel.ALARM_EVERYDAY;
                        week = 0;
                        break;
                    case R.id.dialog_repeat_monday_radiobtn:
                        updateAlarmByWeek("每周一", 1);
                        break;
                    case R.id.dialog_repeat_tuesday_radiobtn:
                        updateAlarmByWeek("每周二", 2);
                        break;
                    case R.id.dialog_repeat_wednesday_radiobtn:
                        updateAlarmByWeek("每周三", 3);
                        break;
                    case R.id.dialog_repeat_thursday_radiobtn:
                        updateAlarmByWeek("每周四", 4);
                        break;
                    case R.id.dialog_repeat_friday_radiobtn:
                        updateAlarmByWeek("每周五", 5);
                        break;
                    case R.id.dialog_repeat_saturday_radiobtn:
                        updateAlarmByWeek("每周六", 6);
                        break;
                    case R.id.dialog_repeat_sunday_radiobtn:
                        updateAlarmByWeek("每周日", 7);
                        break;
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @OnClick(R.id.add_alarm_ringtone_layout)
    public void chooseRingtone() {
        RingtoneChooseActivity.start(this, ringtoneName);
    }

    @OnClick(R.id.add_alarm_delete_btn)
    public void delete() {
        String alarmStr = SharedPreferencesUtils.getString(this, SharedPreferencesUtils.KEY_SETTED_ALARM_LIST, "");
        List<AlarmModel> list = new Gson().fromJson(alarmStr, new TypeToken<List<AlarmModel>>() {
        }.getType());
        if (mAlarmModel != null && list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (mAlarmModel.getId() == list.get(i).getId()) {
                    Log.i("Lebron", " remove pos " + i);
                    list.remove(i);
                    AlarmManagerUtil.cancelAlarm(AddAlarmActivity.this, ALARM_ACTION, mAlarmModel.getId());
                    break;
                }
            }
            SharedPreferencesUtils.saveString(this, SharedPreferencesUtils.KEY_SETTED_ALARM_LIST, new Gson().toJson(list));
            Log.i("Lebron", mAlarmModel.toString());
            EventBus.getDefault().post(KEY_ADD_ALARM);
            finish();
        }
    }

    private void updateAlarmByWeek(String weekStr, int week) {
        mRepeatView.setText(weekStr);
        this.week = week;
        flag = AlarmModel.ALARM_BYWEEK;
    }

}
