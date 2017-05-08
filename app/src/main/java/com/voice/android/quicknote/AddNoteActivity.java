package com.voice.android.quicknote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.voice.android.R;
import com.voice.android.common.base.BaseActivity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lebron on 17-5-8.
 */

public class AddNoteActivity extends BaseActivity {
    @BindView(R.id.add_note_edittext)
    EditText mEditText;
    @BindView(R.id.add_note_current_time_txv)
    TextView mTimeView;
    @BindView(R.id.add_note_current_length_txv)
    TextView mLengthView;
    @BindView(R.id.add_note_voice_img)
    ImageView mVoiceImageView;
    @BindView(R.id.add_note_keyboard_img)
    ImageView mKeyboardImageView;
    @BindView(R.id.add_note_voice_btn)
    FloatingActionButton mRecordingBtn;

    private StringBuffer mStringBuffer = new StringBuffer();

    public static void start(Context context) {
        Intent intent = new Intent(context, AddNoteActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_add_note;
    }

    @OnClick(R.id.add_note_voice_layout)
    public void startVoice() {
        mVoiceImageView.setBackgroundResource(R.mipmap.voice_blue_ic);
        mKeyboardImageView.setBackgroundResource(R.mipmap.keyboard_gray_ic);
        mRecordingBtn.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.add_note_keyboard_layout)
    public void startKeyboard() {
        mVoiceImageView.setBackgroundResource(R.mipmap.voice_gray_ic);
        mKeyboardImageView.setBackgroundResource(R.mipmap.keyboard_blue_ic);
        mRecordingBtn.setVisibility(View.GONE);
        showSoftInputFromWindow(mEditText);
    }

    @OnClick(R.id.add_note_voice_btn)
    public void startRecording() {
        initSpeech(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=591051a2");
        getCurrentTime();
        showSoftInputFromWindow(mEditText);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //mLengthView.setText(s.length());
            }
        });
    }

    private void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        AddNoteActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void getCurrentTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        mTimeView.setText(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reminder_add_alarm_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ok) {

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化语音识别
     */
    public void initSpeech(final Context context) {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(context, null);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    //解析语音
                    String result = parseVoice(recognizerResult.getResultString());
                    Log.i("Lebron", result);
                    mStringBuffer.append(result);
                    mEditText.setText(mStringBuffer);
                    mEditText.setSelection(mStringBuffer.length());
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    /**
     * 解析语音json
     */
    public String parseVoice(String resultString) {
        Gson gson = new Gson();
        Voice voiceBean = gson.fromJson(resultString, Voice.class);

        StringBuffer sb = new StringBuffer();
        ArrayList<Voice.WSBean> ws = voiceBean.ws;
        for (Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }

    /**
     * 语音对象封装
     */
    public class Voice {

        public ArrayList<WSBean> ws;

        public class WSBean {
            public ArrayList<CWBean> cw;
        }

        public class CWBean {
            public String w;
        }
    }


}
