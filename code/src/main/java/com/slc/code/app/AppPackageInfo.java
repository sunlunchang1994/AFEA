package com.slc.code.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by on the way on 2017/11/8.
 */

public class AppPackageInfo {
    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode() {
        Context context = AppData.getApplicationContext();
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        int versionCode = 0;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionCode = 0;
        } finally {
            return versionCode;
        }
    }

    /**
     * 获取版本名称
     *
     * @return
     */
    public static String getVersionOfName() {
        Context context = AppData.getApplicationContext();
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        String versionName = "1.0";
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "1.0";
        } finally {
            return versionName;
        }
    }
}
