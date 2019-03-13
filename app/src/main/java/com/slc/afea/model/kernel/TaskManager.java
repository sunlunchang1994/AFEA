package com.slc.afea.model.kernel;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import com.slc.afea.model.Constant;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by slc on 2019/3/7.
 */

public class TaskManager {
    int notificationIconRes;
    private int PUSH_NOTIFICATION_ID = 0;
    private Handler mHandler;
    private Set<String> tokenSet = new HashSet<>();
    private Timer timerAccurate/*, timerTiming*/;
    private AlarmManager mAlarmManager;
    private PendingIntent pIntent;

    private static class Holder {
        private static final TaskManager INSTANCE = new TaskManager();
    }

    static TaskManager getInstance() {
        return TaskManager.Holder.INSTANCE;
    }

    void initTaskManager(Context context) {
        XpLog.log("初始化TaskManager");
        if (timerAccurate == null) {
            timerAccurate = new Timer();
        }
        /*if (timerTiming == null) {
            timerTiming = new Timer();
        }*/
        mHandler = new Handler(context.getMainLooper());
        startTimerTiming(context);
    }

    private void startTimerTiming(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Ga.ACTION_COLLECT_TIMING_NOTIFICATION);
        context.registerReceiver(new AlarmReceiver(), intentFilter);

        Intent intent = new Intent(Constant.Ga.ACTION_COLLECT_TIMING_NOTIFICATION);
        pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        repeatAlarm();
        /*timerTiming.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(() -> {
                    AliMobileAutoCollectEnergyManager.getInstance().bgCollectTiming();
                });
            }
        }, 120000, 300000);*/
    }

    private void repeatAlarm() {
        // 取消原有的定时任务
        long timingTemp = AliMobileAutoCollectEnergyManager.getInstance().getTimingValue();
        XpLog.log("定时开始" + timingTemp);
        mAlarmManager.cancel(pIntent);
        // 开启新的定时任务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timingTemp, pIntent);
        } else {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timingTemp, pIntent);
        }
    }

    // 定义一个定时广播的接收器
    public class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            XpLog.log("接收广播");
            if (Constant.Ga.ACTION_COLLECT_TIMING_NOTIFICATION.equals(intent.getAction())) {
                if (mHandler != null) {
                    mHandler.post(() -> {
                        XpLog.log("发送收取");
                        AliMobileAutoCollectEnergyManager.getInstance().bgCollectTiming();
                        repeatAlarm();
                    });
                }
            }
        }
    }

    void setReminders(HashMap<String, Long> canCollectLaterTimeMap) {
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
                                XpLog.log("开始发送收取信息");
                                if (!AliMobileAutoCollectEnergyManager.getInstance().bgAutoCollect(userId)) {
                                    //TODO 此处发送通知
                                    XpLog.log("不在该页面 开始发送通知");
                                    //sendNotification(Context context);
                                }
                                tokenSet.remove(token);
                            } catch (Exception e) {
                                XpLog.log("发送通知异常：" + e.getMessage());
                            }
                        });
                    }
                }, new Date(timestamp + 1000));
            }
        }
    }

    /**
     * 发送通知
     *
     * @param context
     */
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
}
