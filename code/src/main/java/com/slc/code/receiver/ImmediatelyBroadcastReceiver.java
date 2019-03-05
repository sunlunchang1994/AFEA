package com.slc.code.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by slc on 2018/12/4.
 */

public abstract class ImmediatelyBroadcastReceiver extends BroadcastReceiver {
    public static final String EXTRA_KEY_LIST = "actionKeyList";
    public static final String EXTRA_VALUE_BUNDLE = "actionValueBundle";
    private String sendAction;
    private String receiveAction;
    private Extras extras;
    protected Context mContext;
    public ImmediatelyBroadcastReceiver(@NonNull String sendAction, @NonNull String receiveAction) {
        this.sendAction = sendAction;
        this.receiveAction = receiveAction;
    }

    @SuppressWarnings("all")
    @Override
    public final void onReceive(Context context, Intent intent) {
        this.mContext=context;
        if (receiveAction.equals(intent.getAction())) {
            try {
                Set<String> extraKeySet = (Set<String>) intent.getSerializableExtra(EXTRA_KEY_LIST);
                intent.removeExtra(EXTRA_KEY_LIST);
                if (extraKeySet == null) {
                    extraKeySet = new HashSet<>();
                }
                Bundle extrasBundle = intent.getBundleExtra(EXTRA_VALUE_BUNDLE);
                intent.removeExtra(EXTRA_VALUE_BUNDLE);
                if (extrasBundle == null) {
                    extrasBundle = new Bundle();
                }
                if (handlerMsg(extraKeySet, extrasBundle)) {
                    sendMsg(context);
                }
            }catch (Exception e){

            }
        }
        onOtherReceive(context, intent);
    }

    protected abstract void onOtherReceive(Context context, Intent intent);

    /**
     * 发送广播
     *
     * @param context
     */
    public void sendMsg(Context context) {
        Intent intent = new Intent(sendAction);
        intent.putExtra(EXTRA_KEY_LIST, (Serializable) getExtras().extraKeySet);
        intent.putExtra(EXTRA_VALUE_BUNDLE, getExtras().extrasBundle);
        intent.putExtras(getExtras().extrasBundle);
        context.sendBroadcast(intent);
        extras = null;
    }

    /**
     * 处理消息
     *
     * @param extraKeySet  消息key
     * @param extrasBundle 消息值（根据key获取
     * @return 返回true则返回回馈消息，false则不返回
     */
    public boolean handlerMsg(final Set<String> extraKeySet, final Bundle extrasBundle) {
        return true;
    }

    public ImmediatelyBroadcastReceiver addSendInfo(String key) {
        getExtras().extraKeySet.add(key);
        return this;
    }

    public ImmediatelyBroadcastReceiver addSendInfo(String key, int value) {
        getExtras().extraKeySet.add(key);
        getExtras().extrasBundle.putInt(key, value);
        return this;
    }

    public ImmediatelyBroadcastReceiver addSendInfo(String key, boolean value) {
        getExtras().extraKeySet.add(key);
        getExtras().extrasBundle.putBoolean(key, value);
        return this;
    }

    public ImmediatelyBroadcastReceiver addSendInfo(String key, long value) {
        getExtras().extraKeySet.add(key);
        getExtras().extrasBundle.putLong(key, value);
        return this;
    }

    public ImmediatelyBroadcastReceiver addSendInfo(String key, String value) {
        getExtras().extraKeySet.add(key);
        getExtras().extrasBundle.putString(key, value);
        return this;
    }

    public ImmediatelyBroadcastReceiver addSendInfo(String key, String[] value) {
        getExtras().extraKeySet.add(key);
        getExtras().extrasBundle.putStringArray(key, value);
        return this;
    }

    public ImmediatelyBroadcastReceiver addSendInfo(String key, ArrayList<String> value) {
        getExtras().extraKeySet.add(key);
        getExtras().extrasBundle.putStringArrayList(key, value);
        return this;
    }

    public ImmediatelyBroadcastReceiver addSendInfo(String key, byte value) {
        getExtras().extraKeySet.add(key);
        getExtras().extrasBundle.putByte(key, value);
        return this;
    }

    public ImmediatelyBroadcastReceiver addSendInfo(String key, Serializable value) {
        getExtras().extraKeySet.add(key);
        getExtras().extrasBundle.putSerializable(key, value);
        return this;
    }

    public ImmediatelyBroadcastReceiver addSendInfo(String key, Parcelable value) {
        getExtras().extraKeySet.add(key);
        getExtras().extrasBundle.putParcelable(key, value);
        return this;
    }

    public ImmediatelyBroadcastReceiver addSendInfo(Bundle value) {
        getExtras().extrasBundle.putAll(value);
        return this;
    }

    /**
     * 获取额外的参数 保证数据唯一
     *
     * @return
     */
    protected Extras getExtras() {
        if (extras == null) {
            synchronized (this) {
                if (extras == null) {
                    extras = new Extras();
                }
            }
        }
        return extras;
    }

    private static class Extras {
        private Set<String> extraKeySet = new HashSet<>();
        private Bundle extrasBundle = new Bundle();
    }
}
