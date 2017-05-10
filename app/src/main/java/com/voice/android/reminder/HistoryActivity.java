package com.voice.android.reminder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voice.android.R;
import com.voice.android.common.base.BaseActivity;
import com.voice.android.common.utils.SharedPreferencesUtils;
import com.voice.android.common.utils.TimeUtils;
import com.voice.android.reminder.model.AlarmModel;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lebron on 17-5-10.
 */

public class HistoryActivity extends BaseActivity {
    @BindView(R.id.history_recyclerview)
    RecyclerView mRecyclerView;

    private List<AlarmModel> mDatas;
    private BaseQuickAdapter mAdapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, HistoryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_history;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_history_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear) {
            if (mDatas != null) {
                mDatas.clear();
                mAdapter.notifyDataSetChanged();
                SharedPreferencesUtils.saveString(this, SharedPreferencesUtils.KEY_FINISHED_ALARM_LIST, new Gson().toJson(mDatas));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String string = SharedPreferencesUtils.getString(this, SharedPreferencesUtils.KEY_FINISHED_ALARM_LIST, "");
        mDatas = new Gson().fromJson(string, new TypeToken<List<AlarmModel>>() {
        }.getType());
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }

        mAdapter = new BaseQuickAdapter<AlarmModel>(R.layout.alarm_new_item, mDatas) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, final AlarmModel alarmModel) {
                baseViewHolder.setText(R.id.alarm_item_time_txv, TimeUtils.formatTime(alarmModel.getHour(), alarmModel.getMinute()));
                baseViewHolder.setText(R.id.alarm_item_date_txv, alarmModel.getDescribe());
                baseViewHolder.getView(R.id.alarm_item_finished_txv).setVisibility(View.VISIBLE);
                SwitchCompat switchCompat = baseViewHolder.getView(R.id.alarm_item_switchcompat);
                switchCompat.setVisibility(View.GONE);
            }
        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.common_empty_layout, (ViewGroup) mRecyclerView.getParent(), false));
        mRecyclerView.setAdapter(mAdapter);
    }
}
