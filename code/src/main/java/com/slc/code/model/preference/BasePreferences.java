package com.slc.code.model.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.slc.code.app.AppData;

import java.util.HashSet;
import java.util.Set;

public abstract class BasePreferences {
    private SharedPreferences PREFERENCES;//便捷存储

    /**
     * 初始化构造函数
     */
    public BasePreferences() {
        PREFERENCES = AppData.getApplicationContext().getSharedPreferences(getPreferencesName(), Context.MODE_PRIVATE);
    }

    /**
     * 获取便捷存储对象
     *
     * @return
     */
    public SharedPreferences getPreference() {
        return PREFERENCES;
    }

    /**
     * 获取便捷存储名称
     *
     * @return
     */
    public abstract String getPreferencesName();

    public void setCustomAppProfile(String key, String val) {
        PREFERENCES
                .edit()
                .putString(key, val)
                .apply();
    }

    public void setCustomAppProfile(String key, int val) {
        PREFERENCES
                .edit()
                .putInt(key, val)
                .apply();
    }

    public void clearCustomAppProfileOfInt(String key) {
        setCustomAppProfile(key, 0);
    }

    public void setCustomAppProfile(String key, boolean value) {
        PREFERENCES
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    public boolean getCustomAppProfileOfBoolean(String key) {
        return PREFERENCES.getBoolean(key, false);
    }

    public String getCustomAppProfileOfString(String key) {
        return PREFERENCES.getString(key, "");
    }

    public int getCustomAppProfileOfInt(String key) {
        return PREFERENCES.getInt(key, 0);
    }

    public void setCustomAppProfileOfStringSet(@NonNull String key,@NonNull String string) {
        Set<String> stringSet = new HashSet<>();
        stringSet.add(string);
        setCustomAppProfileOfStringSet(key, stringSet);
    }

    public void setCustomAppProfileOfStringSet(String key, Set<String> stringSet) {
        PREFERENCES.edit().putStringSet(key, stringSet).apply();
    }

    public void addCustomAppProfileOfStringSet(@NonNull String key, @NonNull String string) {
        Set<String> stringSet = new HashSet<>();
        stringSet.add(string);
        addCustomAppProfileOfStringSet(key, stringSet);
    }

    public void addCustomAppProfileOfStringSet(String key, Set<String> stringSet) {
        Set<String> oldStringSet = getCustomAppProfileOfStringSet(key);
        if (oldStringSet == null) {
            oldStringSet = stringSet;
        } else {
            oldStringSet.addAll(stringSet);
        }
        PREFERENCES.edit().putStringSet(key, oldStringSet).apply();
    }

    public Set<String> getCustomAppProfileOfStringSet(String key) {
        return PREFERENCES.getStringSet(key, null);
    }
}
