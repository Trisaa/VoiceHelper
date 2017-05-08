package com.voice.android.quicknote;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.voice.android.R;
import com.voice.android.common.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by LeBron on 2017/5/2.
 */

public class NoteFragment extends BaseFragment {
    @BindView(R.id.note_recyclerview)
    RecyclerView mRecyclerView;

    private List<String> mDatas;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_quicknote;
    }

    @Override
    protected void initViewsAndData(View view) {
        mDatas = new ArrayList<>();
        mDatas.add("1");
        mDatas.add("2");
        mDatas.add("3");
        mDatas.add("4");
        BaseQuickAdapter<String> adapter = new BaseQuickAdapter<String>(R.layout.note_new_item, mDatas) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, final String alarmModel) {

            }
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.note_floating_btn)
    public void addNote() {
        AddNoteActivity.start(getActivity());
    }
}
