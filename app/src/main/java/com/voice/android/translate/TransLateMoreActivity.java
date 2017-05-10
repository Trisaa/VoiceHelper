package com.voice.android.translate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.voice.android.R;
import com.voice.android.common.base.BaseActivity;
import com.voice.android.common.http.HttpMethods;
import com.voice.android.translate.adapter.TranslateItem;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Created by lebron on 17-5-10.
 */

public class TransLateMoreActivity extends BaseActivity {
    public static final String KEY_TRANSLATE_TEXT = "KEY_TRANSLATE_TEXT";
    public static final String KEY_TRANSLATE_TYPE = "KEY_TRANSLATE_TYPE";
    @BindView(R.id.translate_more_edittext)
    EditText mEditText;

    private String mStr;
    private int itemType;

    public static void start(Context context, String text, int itemType) {
        Intent intent = new Intent(context, TransLateMoreActivity.class);
        intent.putExtra(KEY_TRANSLATE_TEXT, text);
        intent.putExtra(KEY_TRANSLATE_TYPE, itemType);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.acticity_translate_more;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStr = getIntent().getStringExtra(KEY_TRANSLATE_TEXT);
        itemType = getIntent().getIntExtra(KEY_TRANSLATE_TYPE, TranslateItem.ITEM_LEFT);
        if (!TextUtils.isEmpty(mStr)) {
            mEditText.setText(mStr);
            mEditText.setSelection(mStr.length());
        }
    }

    @OnClick(R.id.translate_btn)
    public void translate() {
        mStr = mEditText.getText().toString();
        if (!TextUtils.isEmpty(mStr)) {
            HttpMethods.getInstance().getTranslateResult(new Subscriber<TranslateItem>() {
                @Override
                public void onCompleted() {
                    Log.i("Lebron", " onCompleted ");
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(TransLateMoreActivity.this, "翻译失败，请重试", Toast.LENGTH_SHORT).show();
                    Log.i("Lebron", " onError " + e.toString());
                }

                @Override
                public void onNext(TranslateItem translateItem) {
                    Log.i("Lebron", " onNext " + translateItem.getUntranslateStr() + " " + translateItem.getTranslatedStr());
                    translateItem.setItemType(itemType);
                    EventBus.getDefault().post(translateItem);
                    finish();
                }
            }, mStr);
        }
    }


}
