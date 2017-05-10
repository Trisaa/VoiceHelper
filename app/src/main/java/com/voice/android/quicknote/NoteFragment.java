package com.voice.android.quicknote;

import android.Manifest;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.voice.android.R;
import com.voice.android.common.base.BaseFragment;
import com.voice.android.common.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

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
        mRecyclerView.addItemDecoration(new NoteItemDecoration(48));
        mAdapter.setEmptyView(getActivity().getLayoutInflater().inflate(R.layout.common_empty_layout, (ViewGroup) mRecyclerView.getParent(), false));
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
        new RxPermissions(getActivity()).request(Manifest.permission.RECORD_AUDIO).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean grant) {
                if (grant) {
                    AddNoteActivity.start(getActivity());
                } else {
                    Toast.makeText(getActivity(), "未获取所需权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
