package com.slc.code.app;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by on the way on 2017/11/8.
 */

public class AppData {
    /**
     * 初始化配置
     *
     * @param context
     * @return
     */
    public static Configurator init(Context context) {
        getConfigurator().getAppConfigs().put(ConfigKeys.APPLICATION_CONTEXT.name(), context.getApplicationContext());
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        getConfigurator().getAppConfigs().put(ConfigKeys.SCREEN_WIDTH.name(), displayMetrics.widthPixels);
        getConfigurator().getAppConfigs().put(ConfigKeys.SCREEN_HEIGHT.name(), displayMetrics.heightPixels);
        getConfigurator().getAppConfigs().put(ConfigKeys.CRASH_EXIT_TIME.name(), 2000);
        return Configurator.getInstance();
    }

    /**
     * 获取配置器
     *
     * @return
     */
    public static Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    /**
     * 存入一个配置
     *
     * @param key
     * @param value
     * @return
     */
    public static Configurator putConfiguration(Object key, Object value) {
        getConfigurator().getAppConfigs().put(key, value);
        return getConfigurator();
    }

    /**
     * 获取一个配置的值
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getConfiguration(Object key) {
        return getConfigurator().getConfiguration(key);
    }

    /**
     * 获取上下文环境
     *
     * @return
     */
    public static Context getApplicationContext() {
        return getConfigurator().getConfiguration(ConfigKeys.APPLICATION_CONTEXT.name());
    }

    public static String getGlobalStoragePath() {
        return getConfigurator().getConfiguration(ConfigKeys.GLOBAL_STORAGE_PATH.name());
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        return getConfigurator().getConfiguration(ConfigKeys.SCREEN_WIDTH.name());
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        return getConfigurator().getConfiguration(ConfigKeys.SCREEN_HEIGHT.name());
    }
}
