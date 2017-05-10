package com.voice.android.reminder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voice.android.R;
import com.voice.android.common.base.BaseFragment;
import com.voice.android.common.utils.AlarmManagerUtil;
import com.voice.android.common.utils.SharedPreferencesUtils;
import com.voice.android.common.utils.TimeUtils;
import com.voice.android.reminder.model.AlarmModel;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lebron on 17-4-27.
 */

public class AlarmFragment extends BaseFragment {
    @BindView(R.id.alarm_recyclerview)
    RecyclerView mRecyclerView;

    BaseQuickAdapter<AlarmModel> mAdapter;
    private List<AlarmModel> mDatas;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_alarm;
    }

    @Override
    protected void initViewsAndData(View view) {
        initData();
        mAdapter = new BaseQuickAdapter<AlarmModel>(R.layout.alarm_new_item, mDatas) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, final AlarmModel alarmModel) {
                baseViewHolder.setText(R.id.alarm_item_time_txv, TimeUtils.formatTime(alarmModel.getHour(), alarmModel.getMinute()));
                baseViewHolder.setText(R.id.alarm_item_date_txv, alarmModel.getDescribe());
                SwitchCompat switchCompat = baseViewHolder.getView(R.id.alarm_item_switchcompat);
                switchCompat.setChecked(alarmModel.isOpen());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (mDatas != null && alarmModel != null) {
                            if (mDatas.contains(alarmModel)) {
                                alarmModel.setOpen(isChecked);
                                mDatas.set(mDatas.indexOf(alarmModel), alarmModel);
                                SharedPreferencesUtils.saveString(getActivity(), SharedPreferencesUtils.KEY_SETTED_ALARM_LIST, new Gson().toJson(mDatas));
                            }
                            if (isChecked) {
                                AlarmManagerUtil.setAlarm(getActivity(), alarmModel);
                            } else {
                                AlarmManagerUtil.cancelAlarm(getActivity(), AlarmManagerUtil.ALARM_ACTION, alarmModel.getId());
                            }
                        }
                    }
                });
            }
        };
        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                AddAlarmActivity.start(getActivity(), mDatas.get(i));
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setEmptyView(getActivity().getLayoutInflater().inflate(R.layout.common_empty_layout, (ViewGroup) mRecyclerView.getParent(), false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Subscribe
    public void onEvent(String event) {
        if (AddAlarmActivity.KEY_ADD_ALARM.equals(event)) {
            initData();
            mAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.alarm_floating_btn)
    public void addAlarm() {
        AddAlarmActivity.start(getActivity());
    }

    private void initData() {
        String string = SharedPreferencesUtils.getString(getActivity(), SharedPreferencesUtils.KEY_SETTED_ALARM_LIST, "");
        List<AlarmModel> list = new Gson().fromJson(string, new TypeToken<List<AlarmModel>>() {
        }.getType());
        if (list == null) {
            list = new ArrayList<>();
        }
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        mDatas.clear();
        mDatas.addAll(list);
        for (int i = 0; i < mDatas.size(); i++) {
            Log.i("Lebron", mDatas.get(i).toString());
        }
    }
}
