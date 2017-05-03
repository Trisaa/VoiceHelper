package com.voice.android.reminder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.voice.android.R;
import com.voice.android.common.base.BaseFragment;
import com.voice.android.reminder.model.AlarmModel;

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

    private List<AlarmModel> mDatas;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_alarm;
    }

    @Override
    protected void initViewsAndData(View view) {
        initData();
        BaseQuickAdapter<AlarmModel> adapter = new BaseQuickAdapter<AlarmModel>(R.layout.alarm_new_item, mDatas) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, final AlarmModel alarmModel) {
                baseViewHolder.setText(R.id.alarm_item_time_txv, "12:01");
                baseViewHolder.setText(R.id.alarm_item_date_txv, "2017-05-01");
                SwitchCompat switchCompat = baseViewHolder.getView(R.id.alarm_item_switchcompat);
                switchCompat.setChecked(alarmModel.isOpen());
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        alarmModel.setOpen(isChecked);
                    }
                });
            }
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.alarm_floating_btn)
    public void addAlarm() {
        AddAlarmActivity.start(getActivity());
    }

    private void initData() {
        mDatas = new ArrayList<>();
        AlarmModel alarmModel = new AlarmModel();
        alarmModel.setTime(123456);
        alarmModel.setDate(123456);
        alarmModel.setOpen(true);
        for (int i = 0; i < 15; i++) {
            mDatas.add(alarmModel);
        }
    }
}
