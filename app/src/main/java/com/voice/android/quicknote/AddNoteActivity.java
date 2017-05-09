package com.voice.android.quicknote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.voice.android.R;
import com.voice.android.common.base.BaseActivity;
import com.voice.android.common.utils.SharedPreferencesUtils;


import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lebron on 17-5-8.
 */

public class AddNoteActivity extends BaseActivity {
    private static final String KEY_NOTE_MODEL = "KEY_NOTE_MODEL";
    public static final String KEY_ADD_NOTE = "KEY_ADD_NOTE";
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
    @BindView(R.id.add_note_bottom_voice_layout)
    RelativeLayout mBottomVoiceLayout;
    @BindView(R.id.add_note_input_type_layout)
    LinearLayout mBottomTypeLayout;

    private String mCurrentStr, mTime;
    private NoteModel mNoteModel;
    private boolean isAddedNote;
    private Menu mMenu;
    private SpeechSynthesizer mTts;

    public static void start(Context context) {
        Intent intent = new Intent(context, AddNoteActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, NoteModel model) {
        Intent intent = new Intent(context, AddNoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_NOTE_MODEL, model);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_add_note;
    }

    @OnClick(R.id.add_note_voice_layout)
    public void startVoice() {
        closeSoftKeybord(mEditText);
        mVoiceImageView.setBackgroundResource(R.mipmap.voice_blue_ic);
        mKeyboardImageView.setBackgroundResource(R.mipmap.keyboard_gray_ic);
        mBottomVoiceLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.add_note_keyboard_layout)
    public void startKeyboard() {
        mVoiceImageView.setBackgroundResource(R.mipmap.voice_gray_ic);
        mKeyboardImageView.setBackgroundResource(R.mipmap.keyboard_blue_ic);
        mBottomVoiceLayout.setVisibility(View.GONE);
        showSoftKeyboard(mEditText);
    }

    @OnClick(R.id.add_note_edittext)
    public void clickEditText() {
        startKeyboard();
    }

    private void closeSoftKeybord(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @OnClick(R.id.add_note_voice_btn)
    public void startRecording() {
        initRecognition(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNoteModel = getIntent().getParcelableExtra(KEY_NOTE_MODEL);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=591051a2");
        getCurrentTime();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTts != null) {
            mTts.stopSpeaking();
            mTts.destroy();
        }
    }

    private void initView() {
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCurrentStr = s.toString();
                mLengthView.setText(mCurrentStr.length() + "字");
                Log.i("Lebron", " " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (mNoteModel != null) {
            isAddedNote = true;
            mCurrentStr = mNoteModel.getContent();
            mTime = mNoteModel.getTime();
            mTimeView.setText(mTime);
            mEditText.setText(mCurrentStr);
            mEditText.setSelection(mCurrentStr.length());
        }
    }

    private void getCurrentTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");
        mTime = sDateFormat.format(new java.util.Date());
        mTimeView.setText(mTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        if (mNoteModel != null) {
            mMenu.getItem(1).setVisible(true);
            mMenu.getItem(2).setVisible(true);
        } else {
            mMenu.getItem(1).setVisible(false);
            mMenu.getItem(2).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ok) {
            if (!TextUtils.isEmpty(mCurrentStr)) {
                String alarmStr = SharedPreferencesUtils.getString(this, SharedPreferencesUtils.KEY_SETTED_NOTE_LIST, "");
                List<NoteModel> list = new Gson().fromJson(alarmStr, new TypeToken<List<NoteModel>>() {
                }.getType());
                if (list == null) {
                    list = new ArrayList<>();
                }

                if (mNoteModel == null) {
                    mNoteModel = new NoteModel();
                }
                mNoteModel.setTime(mTime);
                mNoteModel.setContent(mCurrentStr);

                if (isAddedNote) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTime().equals(mNoteModel.getTime())) {
                            list.set(i, mNoteModel);
                            break;
                        }
                    }
                } else {
                    list.add(mNoteModel);
                }

                SharedPreferencesUtils.saveString(this, SharedPreferencesUtils.KEY_SETTED_NOTE_LIST, new Gson().toJson(list));
                Log.i("Lebron", mNoteModel.toString());
                EventBus.getDefault().post(KEY_ADD_NOTE);
                finish();
            } else {
                Toast.makeText(this, "未见测到输入", Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.setMessage("确定要删除当前笔记?")
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String alarmStr = SharedPreferencesUtils.getString(AddNoteActivity.this, SharedPreferencesUtils.KEY_SETTED_NOTE_LIST, "");
                            List<NoteModel> list = new Gson().fromJson(alarmStr, new TypeToken<List<NoteModel>>() {
                            }.getType());
                            if (mNoteModel != null && list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    if (mNoteModel.getTime().equals(list.get(i).getTime())) {
                                        Log.i("Lebron", " remove pos " + i);
                                        list.remove(i);
                                        break;
                                    }
                                }
                                SharedPreferencesUtils.saveString(AddNoteActivity.this, SharedPreferencesUtils.KEY_SETTED_NOTE_LIST, new Gson().toJson(list));
                                EventBus.getDefault().post(KEY_ADD_NOTE);
                                dialog.dismiss();
                                finish();
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
            dialog.show();
        } else if (item.getItemId() == R.id.action_listen) {
            initSpeech();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSpeech() {
        if (TextUtils.isEmpty(mCurrentStr)) {
            return;
        }
        mTts = SpeechSynthesizer.createSynthesizer(this, null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端

        int code = mTts.startSpeaking(mCurrentStr, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {

            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                Toast.makeText(this, "需要安装助手", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "语音合成失败,错误码: " + code, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 初始化语音识别
     */
    public void initRecognition(final Context context) {
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
                    mCurrentStr = mEditText.getText().toString() + result;
                    mEditText.setText(mCurrentStr);
                    mEditText.setSelection(mCurrentStr.length());
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
