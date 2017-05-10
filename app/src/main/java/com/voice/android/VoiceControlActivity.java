package com.voice.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.voice.android.common.base.BaseActivity;
import com.voice.android.common.utils.AppUtils;
import com.voice.android.quicknote.Voice;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * Created by lebron on 17-5-10.
 */

public class VoiceControlActivity extends BaseActivity {
    private String mCurrentStr;
    private boolean isInstalled;

    public static void start(Context context) {
        Intent intent = new Intent(context, VoiceControlActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_voice_control;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=591051a2");
    }

    @OnClick(R.id.voice_control_floating_btn)
    public void voiceControl() {
        initRecognition(this);
    }

    private void checkIfAppExist(String appName) {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            String name = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            if (appName.equals(name)) {
                AppUtils.jumpApp(this, packageInfo.packageName);
                isInstalled = true;
                break;
            }
        }
        if (!isInstalled) {
            Toast.makeText(this, appName + " 应用未安装", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAppName(String result) {
        if (result.contains("打开")) {
            int i = result.indexOf("打开");
            i += 2;
            int endPos = result.length();
            if (endPos > i) {
                return result.substring(i, endPos);
            }
        } else {
            AppUtils.openBrower(this, result);
        }
        return "";
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
                    mCurrentStr = result;
                    String appName = getAppName(result);
                    Log.i("Lebron", appName);
                    if (!TextUtils.isEmpty(appName)) {
                        checkIfAppExist(appName);
                    }
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
