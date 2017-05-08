package com.voice.android.reminder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.voice.android.R;
import com.voice.android.common.base.BaseActivity;
import com.voice.android.common.utils.AudioPlayer;
import com.voice.android.common.utils.FileUtils;
import com.voice.android.reminder.model.RingtoneModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;


/**
 * Created by lebron on 17-5-8.
 */

public class RingtoneChooseActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_SETTED_RINGTONE = "EXTRA_SETTED_RINGTONE";
    @BindView(R.id.ringtone_recyclerview)
    RecyclerView mRecyclerView;
    private static final int LOADER_ID = 1;
    private String ringName1;

    public static void start(Context context, String ringtoneName) {
        Intent intent = new Intent(context, RingtoneChooseActivity.class);
        intent.putExtra(EXTRA_SETTED_RINGTONE, ringtoneName);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_choose_ringtone;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ringName1 = getIntent().getStringExtra(EXTRA_SETTED_RINGTONE);
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // 查询内部存储音频文件
        return new CursorLoader(this,
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, new String[]{
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA}, null, null,
                MediaStore.Audio.Media.DISPLAY_NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == LOADER_ID) {
            // 过滤重复音频文件的Set
            HashSet<String> set = new HashSet<>();
            //  保存铃声信息的List
            List<RingtoneModel> list = new ArrayList<>();
            if (cursor != null) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                        .moveToNext()) {
                    // 音频文件名
                    String ringName = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    if (ringName != null) {
                        // 当过滤集合里不存在此音频文件
                        if (!set.contains(ringName)) {
                            // 添加音频文件到列表过滤同名文件
                            set.add(ringName);
                            // 去掉音频文件的扩展名
                            ringName = FileUtils.removeEx(ringName);
                            // 取得音频文件的地址
                            String ringUrl = cursor.getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DATA));
                            RingtoneModel model = new RingtoneModel();
                            model.setName(ringName);
                            model.setUrl(ringUrl);
                            // 当列表中存在与保存的铃声名一致时，设置该列表的显示位置
                            if (ringName.equals(ringName1)) {
                                model.setChecked(true);
                            }
                            list.add(model);

                            initAdapter(list);
                        }
                    }
                }
            }
        }
    }

    private void initAdapter(final List<RingtoneModel> list) {
        final BaseQuickAdapter<RingtoneModel> adapter = new BaseQuickAdapter<RingtoneModel>(R.layout.ringtone_choose_item, list) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, final RingtoneModel ringtoneModel) {
                baseViewHolder.setText(R.id.ringtone_choose_title_txv, ringtoneModel.getName());
                ImageView imageView = (ImageView) baseViewHolder.getView(R.id.ringtone_choose_img);
                imageView.setVisibility(ringtoneModel.isChecked() ? View.VISIBLE : View.GONE);
            }
        };

        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                for (int j = 0; j < list.size(); j++) {
                    list.get(j).setChecked(false);
                }
                list.get(i).setChecked(true);
                adapter.notifyDataSetChanged();
                AudioPlayer.getInstance(RingtoneChooseActivity.this).play(list.get(i).getUrl(), false, false);
                EventBus.getDefault().post(list.get(i));
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayer.getInstance(this).stop();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
