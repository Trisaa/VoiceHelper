package com.voice.android.quicknote;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voice.android.R;
import com.voice.android.common.base.BaseFragment;
import com.voice.android.common.utils.SharedPreferencesUtils;
import com.voice.android.reminder.model.AlarmModel;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.voice.android.quicknote.AddNoteActivity.KEY_ADD_NOTE;

/**
 * Created by LeBron on 2017/5/2.
 */

public class NoteFragment extends BaseFragment {
    @BindView(R.id.note_recyclerview)
    RecyclerView mRecyclerView;

    private List<NoteModel> mDatas;
    private BaseQuickAdapter<NoteModel> mAdapter;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_quicknote;
    }

    @Override
    protected void initViewsAndData(View view) {
        initData();
        mAdapter = new BaseQuickAdapter<NoteModel>(R.layout.note_new_item, mDatas) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, final NoteModel alarmModel) {
                baseViewHolder.setText(R.id.note_item_time_txv, alarmModel.getTime());
                baseViewHolder.setText(R.id.note_item_content_txv, alarmModel.getContent());
            }
        };
        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                AddNoteActivity.start(getActivity(), mDatas.get(i));
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Subscribe
    public void newNote(String event) {
        if (KEY_ADD_NOTE.equals(event)) {
            initData();
            mAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.note_floating_btn)
    public void addNote() {
        AddNoteActivity.start(getActivity());
    }

    private void initData() {
        String string = SharedPreferencesUtils.getString(getActivity(), SharedPreferencesUtils.KEY_SETTED_NOTE_LIST, "");
        List<NoteModel> list = new Gson().fromJson(string, new TypeToken<List<NoteModel>>() {
        }.getType());
        if (list == null) {
            list = new ArrayList<>();
        }
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
        mDatas.clear();
        mDatas.addAll(list);
    }
}
