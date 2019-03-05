package com.slc.code.ui.utils;

import android.app.Activity;
import android.os.Handler;

import com.slc.code.app.AppData;
import com.slc.code.app.ConfigKeys;

import java.util.HashSet;

public class ActivityUtil {
    //private boolean isHaveShowActivity;
    private static HashSet<Activity> hashSet = new HashSet<Activity>();

    private static class Holder {
        private static ActivityUtil INSTANCE = new ActivityUtil();
    }

    public static ActivityUtil getInstance() {
        return Holder.INSTANCE;
    }
/*

    public void onPause() {
        isHaveShowActivity = false;
    }

    public void onResume() {
        isHaveShowActivity = true;
    }

    */
/**
     * 是否有显示的activity
     *
     * @return
     *//*

    public boolean isHaveShowActivity() {
        return isHaveShowActivity;
    }
*/

    /**
     * 每一个Activity 在 onCreate 方法的时候，可以装入当前this
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        try {
            hashSet.add(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeActivity(Activity activity) {
        try {
            hashSet.remove(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用此方法用于退出整个Project
     */
    public void exit() {
        try {
            for (Activity activity : hashSet) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            new Handler(AppData.getApplicationContext().getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, (Integer) AppData.getConfiguration(ConfigKeys.CRASH_EXIT_TIME.name()));
        }
    }
}