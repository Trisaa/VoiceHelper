package com.voice.android.translate.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.voice.android.R;

import java.util.List;

/**
 * Created by lebron on 17-5-10.
 */

public class TranslateAdapter extends BaseMultiItemQuickAdapter<TranslateItem> {
    private OnListenClickListener mListener;

    public TranslateAdapter(List<TranslateItem> data) {
        super(data);
        addItemType(TranslateItem.ITEM_LEFT, R.layout.translate_item_left);
        addItemType(TranslateItem.ITEM_RIGHT, R.layout.translate_item_right);
    }

    public void setOnListenClickListener(OnListenClickListener listener) {
        mListener = listener;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final TranslateItem translateItem) {
        switch (translateItem.getItemType()) {
            case TranslateItem.ITEM_LEFT:
                baseViewHolder.setText(R.id.translate_item_cn_txv, translateItem.getUntranslateStr());
                baseViewHolder.setText(R.id.translate_item_en_txv, translateItem.getTranslatedStr());
                baseViewHolder.setOnClickListener(R.id.translate_item_more_img, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onMoreClick(translateItem);
                    }
                });
                baseViewHolder.setOnClickListener(R.id.translate_item_listen_img, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onListenClick(translateItem);
                    }
                });
                break;
            case TranslateItem.ITEM_RIGHT:
                baseViewHolder.setText(R.id.translate_item_cn_txv, translateItem.getUntranslateStr());
                baseViewHolder.setText(R.id.translate_item_en_txv, translateItem.getTranslatedStr());
                baseViewHolder.setOnClickListener(R.id.translate_item_more_img, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onMoreClick(translateItem);
                    }
                });
                baseViewHolder.setOnClickListener(R.id.translate_item_listen_img, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onListenClick(translateItem);
                    }
                });
                break;
        }
    }

    public interface OnListenClickListener {
        void onMoreClick(TranslateItem item);

        void onListenClick(TranslateItem item);
    }
}
