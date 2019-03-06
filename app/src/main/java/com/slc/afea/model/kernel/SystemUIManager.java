package com.slc.afea.model.kernel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.slc.afea.model.Constant;
import com.slc.afea.utils.MyToast;
import com.slc.code.receiver.ImmediatelyBroadcastReceiver;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by achang on 2019/1/25.
 * 存储能量成熟时间，发送通知
 */

public class SystemUIManager /*extends XC_MethodHook */ {
    int notificationIconRes;
    private int PUSH_NOTIFICATION_ID = 0;
    private Handler mHandler;
    private Set<String> tokenSet = new HashSet<>();
    private Timer timerAccurate, timerTiming;
    private SystemUIBroadcastReceiver systemUIBroadcastReceiver;

    private static class Holder {
        private static final SystemUIManager INSTANCE = new SystemUIManager();
    }

    static SystemUIManager getInstance() {
        return SystemUIManager.Holder.INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void onCreate(Context context) {
        XpLog.log("创建通知栏服务");
        if (timerAccurate == null) {
            timerAccurate = new Timer();
        }
        if (timerTiming == null) {
            timerTiming = new Timer();
        }
        mHandler = new Handler(context.getMainLooper());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Ga.ACTION_COLLECT_LATER_TIME_NOTIFICATION);
        intentFilter.addAction(Constant.Ga.ACTION_REQUEST_ACTION_INFO);
        systemUIBroadcastReceiver = new SystemUIBroadcastReceiver();
        context.registerReceiver(systemUIBroadcastReceiver, intentFilter);
        startTimerTiming(context);
    }

    /**
     * 销毁
     */
    private void onDestroy(Context context) {
        XpLog.log("关闭通知栏服务");
        stopTimerAccurate();
        stopTimerTiming();
        try {
            mHandler.removeCallbacksAndMessages(null);
            context.unregisterReceiver(systemUIBroadcastReceiver);
        } catch (Exception e) {
            XpLog.log("关闭timer异常" + Log.getStackTraceString(e));
        }
    }

    private void stopTimerAccurate() {
        try {
            if (timerAccurate != null) {
                timerAccurate.cancel();
                timerAccurate = null;
            }
        } catch (Exception e) {

        }
    }

    private void stopTimerTiming() {
        try {
            if (timerTiming != null) {
                timerTiming.cancel();
                timerTiming = null;
            }
        } catch (Exception e) {

        }
    }
    private void startTimerTiming(Context context){
        timerTiming.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(()->{
                    systemUIBroadcastReceiver.addSendInfo(Constant.Ga.KEY_BG_COLLECT_TIMING);
                    systemUIBroadcastReceiver.sendMsg(context);
                });
            }
        },20000,300000);
    }
    private class SystemUIBroadcastReceiver extends ImmediatelyBroadcastReceiver {
        private SystemUIBroadcastReceiver() {
            super(Constant.Ga.ACTION_RESULT_ACTION_INFO, Constant.Ga.ACTION_REQUEST_ACTION_INFO);
        }

        @Override
        public boolean handlerMsg(Set<String> extraKeySet, Bundle extrasBundle) {
            for (String extraKey : extraKeySet) {
                if (Constant.Ga.KEY_BG_AUTO_COLLECT.equals(extraKey)) {
                    boolean isCollect = extrasBundle.getBoolean(extraKey, false);
                    tokenSet.remove(extrasBundle.getString(Constant.Ga.KEY_BG_COLLECT_TOKEN));
                    XpLog.log("开始收取回馈" + isCollect);
                    if (!isCollect) {//TODO 是否收集
                        XpLog.log("不在森林界面开始发送通知提醒用户");
                        sendNotification(mContext);
                    }
                }
            }
            return false;
        }

        @Override
        protected void onOtherReceive(Context context, Intent intent) {
            if (Constant.Ga.ACTION_COLLECT_LATER_TIME_NOTIFICATION.equals(intent.getAction())) {
                if (Constant.Ga.KEY_SEND_COLLECT_LATER_TIME.equals(intent.getStringExtra(Constant.Ga.EXTRA_KEY))) {
                    try {
                        SystemUIManager.getInstance().setReminders(context, (HashMap<String, Long>) intent.getSerializableExtra(Constant.Ga.KEY_SEND_COLLECT_LATER_TIME));
                    } catch (Exception e) {
                        XpLog.log("存储收集信息异常" + Log.getStackTraceString(e));
                        try {
                            MyToast.Long(context, "保存记录失败 请将Xposed中的日志文件反馈到交流群");
                        } catch (Exception e1) {
                            XpLog.log("存储收集信息异常" + Log.getStackTraceString(e1));
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置提醒
     *
     * @param context
     * @param canCollectLaterTimeMap
     * @throws Exception
     */
    private void setReminders(Context context, HashMap<String, Long> canCollectLaterTimeMap) throws Exception {
        if (canCollectLaterTimeMap == null) {
            return;
        }
        for (Map.Entry<String, Long> entry : canCollectLaterTimeMap.entrySet()) {
            String userId = entry.getKey();
            Long timestamp = entry.getValue();
            String token = userId + "*" + timestamp;
            if (!tokenSet.contains(token)) {
                tokenSet.add(token);
                timerAccurate.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(() -> {
                            try {
                                //tokenSet.remove(token);
                                //sendNotification(context)
                                //TODO 以上是旧版
                                XpLog.log("开始发送收取信息");
                                systemUIBroadcastReceiver.addSendInfo(Constant.Ga.KEY_BG_AUTO_COLLECT);
                                systemUIBroadcastReceiver.addSendInfo(Constant.Ga.KEY_BG_COLLECT_TOKEN, token);
                                systemUIBroadcastReceiver.addSendInfo(Constant.Ga.KEY_BG_COLLECT_USER_ID, userId);
                                systemUIBroadcastReceiver.sendMsg(context);
                            } catch (Exception e) {
                                XpLog.log("发送通知异常：" + e.getMessage());
                            }
                        });
                    }
                }, new Date(timestamp + 1000));
            }
        }
    }

    private void sendNotification(Context context) {
        PUSH_NOTIFICATION_ID++;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
        String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PUSH_CHANNEL_ID);
        builder.setContentTitle("有可收取的能量")//设置通知栏标题
                .setContentIntent(getOnClickPendingIntent(context)) //TODO 设置通知栏点击意图 此处需修改
                .setContentText("点击此通知即可进入蚂蚁森林偷取！").setTicker("点击此通知即可进入蚂蚁森林偷取！")
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setSmallIcon(notificationIconRes)//设置通知小ICON
                .setDefaults(Notification.DEFAULT_ALL);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        if (notificationManager != null) {
            notificationManager.notify(PUSH_NOTIFICATION_ID, notification);
        }
    }

    private PendingIntent getOnClickPendingIntent(Context context) {
        Intent intentClick = new Intent(Constant.Ga.ACTION_START_ANT_UI);
        intentClick.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);
    }

    public static void initHook(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.android.systemui.SystemUIService", classLoader, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                SystemUIManager.getInstance().onCreate((Context) param.thisObject);
            }
        });
        XposedHelpers.findAndHookMethod("com.android.systemui.SystemUIService", classLoader, "onDestroy", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                SystemUIManager.getInstance().onDestroy((Context) param.thisObject);
            }
        });
    }
}
