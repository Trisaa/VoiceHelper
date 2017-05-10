package com.voice.android.reminder;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;

import com.voice.android.R;
import com.voice.android.common.base.BaseFragment;
import com.voice.android.common.utils.SharedPreferencesUtils;

import butterknife.BindView;

/**
 * Created by lebron on 17-4-27.
 */

public class WarmFragment extends BaseFragment {
    @BindView(R.id.warm_raining_reminder_btn)
    SwitchCompat mRainSwitch;
    @BindView(R.id.warm_colder_reminder_btn)
    SwitchCompat mColderSwitch;
    @BindView(R.id.warm_wifi_reminder_btn)
    SwitchCompat mWifiSwitch;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_warm;
    }

    @Override
    protected void initViewsAndData(View view) {
        mRainSwitch.setChecked(SharedPreferencesUtils.getBoolean(getActivity(), SharedPreferencesUtils.KEY_RAIN_NOTIFY, false));
        mColderSwitch.setChecked(SharedPreferencesUtils.getBoolean(getActivity(), SharedPreferencesUtils.KEY_COOL_NOTIFY, false));
        mWifiSwitch.setChecked(SharedPreferencesUtils.getBoolean(getActivity(), SharedPreferencesUtils.KEY_WIFI_NOTIFY, false));
        mRainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBoolean(getActivity(), SharedPreferencesUtils.KEY_RAIN_NOTIFY, isChecked);
            }
        });
        mColderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBoolean(getActivity(), SharedPreferencesUtils.KEY_COOL_NOTIFY, isChecked);
            }
        });
        mWifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBoolean(getActivity(), SharedPreferencesUtils.KEY_WIFI_NOTIFY, isChecked);
            }
        });
    }
}
