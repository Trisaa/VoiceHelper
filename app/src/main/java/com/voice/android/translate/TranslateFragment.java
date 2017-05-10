package com.voice.android.translate;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.voice.android.R;
import com.voice.android.common.base.BaseFragment;
import com.voice.android.common.http.HttpMethods;
import com.voice.android.quicknote.AddNoteActivity;
import com.voice.android.quicknote.Voice;
import com.voice.android.translate.adapter.TranslateAdapter;
import com.voice.android.translate.adapter.TranslateItem;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.functions.Action1;

import static com.voice.android.MainActivity.EVENT_CLEAR_TRANSLATE_DATA;

/**
 * Created by LeBron on 2017/5/2.
 */

public class TranslateFragment extends BaseFragment {
    @BindView(R.id.translate_recyclerview)
    RecyclerView mRecyclerView;

    private TranslateAdapter mAdapter;
    private List<TranslateItem> mDatas;
    private String mCurrentStr;
    private SpeechSynthesizer mTts;

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_translate;
    }

    @Override
    protected void initViewsAndData(View view) {
        SpeechUtility.createUtility(getActivity(), SpeechConstant.APPID + "=591051a2");
        mDatas = new ArrayList<>();
        mAdapter = new TranslateAdapter(mDatas);
        mAdapter.setOnListenClickListener(new TranslateAdapter.OnListenClickListener() {
            @Override
            public void onMoreClick(TranslateItem item) {
                TransLateMoreActivity.start(getActivity(), item.getUntranslateStr(), item.getItemType());
            }

            @Override
            public void onListenClick(TranslateItem item) {
                initSpeech(item.getTranslatedStr());
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setEmptyView(getActivity().getLayoutInflater().inflate(R.layout.common_empty_layout, (ViewGroup) mRecyclerView.getParent(), false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.translate_cn_layout)
    public void clickCN() {
        new RxPermissions(getActivity()).request(Manifest.permission.RECORD_AUDIO).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean grant) {
                if (grant) {
                    initRecognition(getActivity(), TranslateItem.ITEM_LEFT);
                } else {
                    Toast.makeText(getActivity(), "未获取所需权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Subscribe
    public void clearData(String event) {
        if (EVENT_CLEAR_TRANSLATE_DATA.equals(event)) {
            mDatas.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void addData(TranslateItem item) {
        if (item != null && mDatas != null) {
            mDatas.add(item);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTts != null) {
            mTts.stopSpeaking();
            mTts.destroy();
        }
    }

    @OnClick(R.id.translate_uk_layout)
    public void clickUK() {
        new RxPermissions(getActivity()).request(Manifest.permission.RECORD_AUDIO).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean grant) {
                if (grant) {
                    initRecognition(getActivity(), TranslateItem.ITEM_RIGHT);
                } else {
                    Toast.makeText(getActivity(), "未获取所需权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initSpeech(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (mTts == null) {
            mTts = SpeechSynthesizer.createSynthesizer(getActivity(), null);
            mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //设置发音人
            mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
            mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        } else {
            mTts.stopSpeaking();
        }

        int code = mTts.startSpeaking(text, new SynthesizerListener() {
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
                Toast.makeText(getActivity(), "需要安装助手", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "语音合成失败,错误码: " + code, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 初始化语音识别
     */
    public void initRecognition(final Context context, final int type) {
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
                    mCurrentStr = result;
                    HttpMethods.getInstance().getTranslateResult(new Subscriber<TranslateItem>() {
                        @Override
                        public void onCompleted() {
                            Log.i("Lebron", " onCompleted ");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "翻译失败，请重新输入", Toast.LENGTH_SHORT).show();
                            Log.i("Lebron", " onError " + e.toString());
                        }

                        @Override
                        public void onNext(TranslateItem translateItem) {
                            Log.i("Lebron", " onNext " + translateItem.getUntranslateStr() + " " + translateItem.getTranslatedStr());
                            if (mDatas != null) {
                                translateItem.setItemType(type);
                                mDatas.add(translateItem);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }, mCurrentStr);
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
}
