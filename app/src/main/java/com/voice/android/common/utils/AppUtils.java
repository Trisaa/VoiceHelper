package com.voice.android.common.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by lebron on 17-5-10.
 */

public class AppUtils {
    /**
     * 打开本地指定应用
     *
     * @param packageName 指定应用包名
     */
    public static void jumpApp(Context context, String packageName) {
        if (isApkInstalled(context, packageName)) {
            Intent appIntent = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage(packageName);
            context.getApplicationContext().startActivity(appIntent);
        } else {
            Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
        }
    }

    public static void openBrower(Context context, String keyword) {
        try {
            Uri uri = Uri.parse("https://www.baidu.com/s?wd=" + keyword);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }

    public static boolean isApkInstalled(Context context, String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return false;
        } else {
            try {
                context.getApplicationContext().getPackageManager().getPackageInfo(pkg,
                        PackageManager.GET_ACTIVITIES);
                return true;
            } catch (PackageManager.NameNotFoundException exception) {
                return false;
            }
        }
    }
}
