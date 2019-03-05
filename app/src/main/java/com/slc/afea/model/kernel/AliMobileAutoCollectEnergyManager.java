package com.slc.afea.model.kernel;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.slc.afea.database.DaoConstant;
import com.slc.afea.model.Constant;
import com.slc.afea.utils.MyToast;
import com.slc.code.provider.RemotePreferences;
import com.slc.code.receiver.ImmediatelyBroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by achang on 2019/1/10.
 */

public class AliMobileAutoCollectEnergyManager {
    private final static int AS_INEXISTENCE = 0;
    private final static int AS_EXIST = 1;
    private int collectedEnergy = 0;
    private int helpCollect = 0;
    private Object curH5Fragment;
    private Object curH5PageImpl;
    private List<String> canCollectEnergyOrHelpCollectUserIdList = new ArrayList();
    private Activity h5Activity;
    private int pageCount = 0;
    private int activityStatus = AS_INEXISTENCE;
    private Map<String, Boolean> switchMap = new HashMap<>();
    private SwitchBroadcastReceiver switchBroadcastReceiver;
    private static ClassLoader rootClassLoader;
    private static String myUserId;

    public AliMobileAutoCollectEnergyManager() {
        XposedBridge.log("**************AliMobileAutoCollectEnergyManager" + hashCode());
    }

    private void initSwitchMap(Context context) {
        RemotePreferences remotePreferences = new RemotePreferences(context, Constant.MAIN_SETTING_AUTHORITIES, Constant.APP_PREFERENCES_NAME);
        fillSwitchMap(remotePreferences.getAll());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Ga.ACTION_PREF_SWITCH);
        intentFilter.addAction(Constant.Ga.ACTION_RESULT_ACTION_INFO);
        if (switchBroadcastReceiver == null) {
            switchBroadcastReceiver = new SwitchBroadcastReceiver();
        }
        context.registerReceiver(this.switchBroadcastReceiver, intentFilter);
        if (switchMap.isEmpty()) {
            switchBroadcastReceiver.addSendInfo(Constant.Ga.KEY_GET_PREFERENCES_DATA);
            switchBroadcastReceiver.sendMsg(context);
        }
    }

    private void fillSwitchMap(Map<String, ?> objectMap) {
        if (objectMap != null && !objectMap.isEmpty()) {
            for (Map.Entry<String, ?> entry : objectMap.entrySet()) {
                if (entry.getValue() != null && entry.getValue() instanceof Boolean) {
                    Boolean value = (Boolean) entry.getValue();
                    switchMap.put(entry.getKey(), value == null ? false : value);
                }
            }
        }
    }

    /**
     * 此处释放数据，界面已销毁
     */
    private void releaseData() {
        activityStatus = AS_INEXISTENCE;
    }

    /**
     * 根据key查找值Switch
     *
     * @param key
     * @return
     */
    private boolean findSwitchByKey(String key) {
        Boolean value = switchMap.get(key);
        return value == null ? false : value;
    }

    /**
     * 是否为好友列表
     *
     * @param str
     * @return
     */
    private boolean isRankList(String str) {
        try {
            if (new JSONObject(str).has("friendRanking")) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否是用户详情页
     *
     * @param str
     * @return
     */
    private boolean isUserDetail(String str) {
        try {
            if (new JSONObject(str).has("bizNo")) {
                activityStatus = AS_EXIST;
                if (TextUtils.isEmpty(myUserId)) {
                    JSONObject jSONObject = new JSONObject(str);
                    JSONObject userEnergy = jSONObject.optJSONObject("userEnergy");
                    myUserId = userEnergy.optString("userId");
                    XpLog.log("我的id：" + myUserId);
                }
                return true;
            }
        } catch (JSONException e) {
            throwableLog("解析用户能量失败", e);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 自动获取所有好友列表，获取完成后进入详情页
     *
     * @param classLoader
     * @param str
     */
    private void autoGetCanCollectUserIdList(final ClassLoader classLoader, String str) {
        if (parseFriendRankPageDataResponse(str)) {
            new Thread(() -> {
                rpcCall_FriendRankList(classLoader);
            }).start();
            return;
        }
        pageCount = 0;
        if (canCollectEnergyOrHelpCollectUserIdList.size() > 0) {
            for (String rpcCall_CanCollectEnergy : canCollectEnergyOrHelpCollectUserIdList) {
                rpcCall_CanCollectEnergy(classLoader, rpcCall_CanCollectEnergy);
            }
            canCollectEnergyOrHelpCollectUserIdList.clear();
            StringBuilder stringBuilder = new StringBuilder();
            if (collectedEnergy != 0) {
                stringBuilder.append("收集完成，本次共偷取能量：");
                stringBuilder.append(collectedEnergy);
                stringBuilder.append("克");
            }
            if (helpCollect != 0) {
                if (stringBuilder.length() == 0) {
                    stringBuilder.append("收集完成，本次共帮助收集能量：");
                } else {
                    stringBuilder.append(" 帮助收集能量：");
                }
                stringBuilder.append(helpCollect);
                stringBuilder.append("克");
            }
            if (collectedEnergy != 0 || helpCollect != 0) {
                showToast(stringBuilder.toString());
            }
            XposedBridge.log("結束");
            collectedEnergy = 0;
            helpCollect = 0;
            return;
        }
        XposedBridge.log("暂时没有可偷取能量的好友");
    }

    /**
     * 解析好友列表数据如果存在下一页返回true,不存在返回false
     *
     * @param str
     * @return
     */
    private boolean parseFriendRankPageDataResponse(String str) {
        try {
            JSONArray optJSONArray = new JSONObject(str).optJSONArray("friendRanking");
            int i = 0;
            if (optJSONArray == null || optJSONArray.length() == 0) {
                XposedBridge.log("已是最后一页");
                return false;
            }
            XposedBridge.log("好友数据" + str);
            HashMap<String, Long> canCollectLaterTimeMap = null;
            boolean collectEnergyNotificationSwitch = findSwitchByKey(Constant.PREF_COLLECT_ENERGY_NOTIFICATION);
            if (collectEnergyNotificationSwitch) {
                canCollectLaterTimeMap = new HashMap<>();
            }
            while (i < optJSONArray.length()) {
                JSONObject jSONObject = optJSONArray.getJSONObject(i);
                String userId = jSONObject.optString("userId");
                boolean canCollectEnergyOrHelpCollect = false;
                if (isAutoCollectEnergy()) {
                    canCollectEnergyOrHelpCollect = jSONObject.optBoolean("canCollectEnergy");
                }
                if (isAutoHelpCollect()) {
                    canCollectEnergyOrHelpCollect = canCollectEnergyOrHelpCollect || jSONObject.optBoolean("canHelpCollect");
                }
                if (canCollectEnergyOrHelpCollect && !canCollectEnergyOrHelpCollectUserIdList.contains(userId)) {
                    canCollectEnergyOrHelpCollectUserIdList.add(userId);
                }
                if (collectEnergyNotificationSwitch) {
                    Long canCollectLaterTime = jSONObject.optLong("canCollectLaterTime");
                    if (canCollectLaterTime > System.currentTimeMillis()) {
                        canCollectLaterTimeMap.put(userId, canCollectLaterTime);
                    }
                }
                i++;
            }
            if (collectEnergyNotificationSwitch && !canCollectLaterTimeMap.isEmpty())
                sendCollectLaterTime(h5Activity, canCollectLaterTimeMap);
            return true;
        } catch (Exception unused) {
            return true;
        }
    }

    /**
     * 发送下次能量收取时间
     *
     * @param canCollectLaterTimeMap
     */
    private void sendCollectLaterTime(Context context, HashMap<String, Long> canCollectLaterTimeMap) {
        Intent intent = new Intent(Constant.Ga.ACTION_COLLECT_LATER_TIME_NOTIFICATION);
        intent.putExtra(Constant.Ga.EXTRA_KEY, Constant.Ga.KEY_SEND_COLLECT_LATER_TIME);
        intent.putExtra(Constant.Ga.KEY_SEND_COLLECT_LATER_TIME, canCollectLaterTimeMap);
        context.sendBroadcast(intent);
    }

    /**
     * 好友列表下一页
     *
     * @param classLoader
     */
    private void rpcCall_FriendRankList(ClassLoader classLoader) {
        try {
            Method rpcCallMethod = getRpcCallMethod(classLoader);
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("av", "5");
            jSONObject.put("ct", "android");
            jSONObject.put("pageSize", 20);
            jSONObject.put("startPoint", String.valueOf((pageCount * 20) + 1));
            pageCount++;//TODO 此处计数会有问题
            jSONArray.put(jSONObject);
            rpcCallMethod.invoke(null, "alipay.antmember.forest.h5.queryEnergyRanking",
                    jSONArray.toString(), "", true, null, null, false, curH5PageImpl, 0, "", false, -1);
        } catch (Throwable e) {
            throwableLog("FriendRank发送失败：", e);
        }
    }


    /**
     * 开始进入用户详情
     *
     * @param classLoader
     * @param str
     */
    private void rpcCall_CanCollectEnergy(ClassLoader classLoader, String str) {
        try {
            Method rpcCallMethod = getRpcCallMethod(classLoader);
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("av", "5");
            jSONObject.put("ct", "android");
            jSONObject.put("pageSize", 3);
            jSONObject.put("startIndex", 0);
            jSONObject.put("userId", str);
            jSONArray.put(jSONObject);
            rpcCallMethod.invoke(null, "alipay.antmember.forest.h5.queryNextAction",
                    jSONArray.toString(), "", true, null, null, false, curH5PageImpl, 0, "", false, -1);
            rpcCallMethod.invoke(null, "alipay.antmember.forest.h5.pageQueryDynamics",
                    jSONArray.toString(), "", true, null, null, false, curH5PageImpl, 0, "", false, -1);
        } catch (Exception unused) {
        }
    }

    /**
     * 进入用户详情页，开始偷取和帮助好友收取
     *
     * @param classLoader
     * @param str
     */
    private void autoGetCanCollectBubbleIdList(ClassLoader classLoader, String str) {
        XposedBridge.log("个人中心" + str);
        if (!TextUtils.isEmpty(str) && str.contains("collectStatus")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                JSONArray optJSONArray = jSONObject.optJSONArray("bubbles");
                String displayName = jSONObject.getJSONObject("userEnergy").getString("displayName");
                if (optJSONArray != null && optJSONArray.length() > 0) {
                    for (int i = 0; i < optJSONArray.length(); i++) {
                        JSONObject jSONObject2 = optJSONArray.getJSONObject(i);
                        String userId = jSONObject2.optString("userId");
                        long bubblesId = jSONObject2.optLong("id");
                        if (isAutoCollectEnergy() && "AVAILABLE".equals(jSONObject2.optString("collectStatus"))) {
                            rpcCall_CollectEnergy(classLoader, userId, bubblesId, displayName);
                        }
                        if (isAutoHelpCollect() && jSONObject2.optBoolean("canHelpCollect")) {
                            rpcCall_CanHelpCollect(classLoader, userId, bubblesId, displayName);
                        }
                    }
                }
            } catch (Exception unused) {
            }
        } else {

        }
    }


    /**
     * 请求接口偷取能量
     *
     * @param classLoader
     * @param userId
     * @param bubbleIds
     * @param displayName
     */
    private void rpcCall_CollectEnergy(ClassLoader classLoader, String userId, Long bubbleIds, String displayName) {
        try {
            Method rpcCallMethod = getRpcCallMethod(classLoader);
            JSONArray jSONArray = new JSONArray();
            JSONArray jSONArray2 = new JSONArray();
            jSONArray2.put(bubbleIds);
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("av", "5");
            jSONObject.put("ct", "android");
            jSONObject.put("userId", userId);
            jSONObject.put("bubbleIds", jSONArray2);
            jSONArray.put(jSONObject);
            Object invoke = rpcCallMethod.invoke(null, "alipay.antmember.forest.h5.collectEnergy",
                    jSONArray.toString(), "", true, null, null, false, curH5PageImpl, 0, "", false, -1);
            if (parseCollectEnergyResponse(displayName, (String) invoke.getClass().getMethod("getResponse", new Class[0]).invoke(invoke))) {
                return;
            }
        } catch (Throwable e) {
            throwableLog("偷取能量失败异常：", e);
        }
    }

    /**
     * 解析收取能量结果
     *
     * @param str
     * @return
     */
    private boolean parseCollectEnergyResponse(String displayName, String str) {
        XposedBridge.log("收集结果：" + str);
        if (!TextUtils.isEmpty(str) && str.contains("failedBubbleIds")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                JSONArray optJSONArray = jSONObject.optJSONArray("bubbles");
                for (int i = 0; i < optJSONArray.length(); i++) {
                    int presentCollectedEnergy = optJSONArray.getJSONObject(i).optInt("collectedEnergy");
                    collectedEnergy += presentCollectedEnergy;
                    if (findSwitchByKey(Constant.PREF_SAVE_COLLECT_RECORD)) {
                        saveCollectRecord(displayName, presentCollectedEnergy, DaoConstant.CollectRecord.OPERATE_COLLECT_ENERGY);
                    }
                }
                if ("SUCCESS".equals(jSONObject.optString("resultCode"))) {
                    return true;
                }
            } catch (Exception unused) {
                return false;
            }
        }
        return false;
    }


    /**
     * 开始帮助收集
     *
     * @param classLoader
     * @param userId
     * @param bubbleIds
     * @param displayName
     */
    private void rpcCall_CanHelpCollect(ClassLoader classLoader, String userId, Long bubbleIds, String displayName) {
        try {
            Method rpcCallMethod = getRpcCallMethod(classLoader);
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject = new JSONObject();
            /*jSONObject.put("av", "5");
            jSONObject.put("ct", "android");*/
            jSONObject.put("bubbleIds", bubbleIds);
            jSONObject.put("targetUserId", userId);
            jSONArray.put(jSONObject);
            Object invoke = rpcCallMethod.invoke(null, "alipay.antmember.forest.h5.forFriendCollectEnergy",
                    jSONArray.toString(), "", true, null, null, false, curH5PageImpl, 0, "", false, -1);
            if (parseHelpCollectResponse(displayName, (String) invoke.getClass().getMethod("getResponse", new Class[0]).invoke(invoke))) {
                return;
            }
        } catch (Throwable e) {
            throwableLog("帮助好友收取失败异常", e);
        }
    }

    /**
     * 解析帮助收集的能量
     *
     * @param str
     * @return
     */
    private boolean parseHelpCollectResponse(String displayName, String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            if (jsonObject.getString("resultCode").equals("SUCCESS")) {
                JSONArray optJSONArray = jsonObject.getJSONArray("bubbles");
                for (int i = 0; i < optJSONArray.length(); i++) {
                    int presentCollectedEnergy = optJSONArray.getJSONObject(i).optInt("collectedEnergy");
                    helpCollect += presentCollectedEnergy;
                    if (findSwitchByKey(Constant.PREF_SAVE_COLLECT_RECORD)) {
                        saveCollectRecord(displayName, presentCollectedEnergy, DaoConstant.CollectRecord.OPERATE_HELP_COLLECT);
                    }
                }
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveCollectRecord(String name, int collect, int operateType) {
        saveCollectRecord(h5Activity, name, collect, operateType);
    }

    private void saveCollectRecord(Context context, String name, int collect, int operateType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("TIME", System.currentTimeMillis());
        contentValues.put("NAME", name);
        contentValues.put("COLLECT", collect);
        contentValues.put("OPERATE_TYPE", operateType);
        Intent intent = new Intent(Constant.Ga.ACTION_COLLECT_RECORD_NOTIFICATION);
        intent.putExtra(Constant.Ga.EXTRA_KEY, Constant.Ga.KEY_COLLECT_RECORD_DATA);
        intent.putExtra(Constant.Ga.KEY_COLLECT_RECORD_DATA, contentValues);
        context.sendBroadcast(intent);
    }


    private Method getRpcCallMethod(ClassLoader classLoader) {
        try {
            if (curH5Fragment == null) {
                XposedBridge.log("curH5Fragment为空");
            }
            Field declaredField = curH5Fragment.getClass().getDeclaredField("a");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(curH5Fragment);
            if (obj == null) {
                XposedBridge.log("viewHolder为空");
            }
            Field declaredField2 = obj.getClass().getDeclaredField("h");
            declaredField2.setAccessible(true);
            curH5PageImpl = declaredField2.get(obj);
            Class loadClass = classLoader.loadClass("com.alipay.mobile.h5container.api.H5Page");
            Class loadClass2 = classLoader.loadClass("com.alibaba.fastjson.JSONObject");
            Class loadClass3 = classLoader.loadClass("com.alipay.mobile.nebulabiz.rpc.H5RpcUtil");
            if (curH5PageImpl != null) {
                Method method = loadClass3.getMethod("rpcCall", new Class[]{String.class, String.class, String.class, Boolean.TYPE, loadClass2, String.class, Boolean.TYPE, loadClass, Integer.TYPE, String.class, Boolean.TYPE, Integer.TYPE});
                if (method == null) {
                    XposedBridge.log("callM为空");
                }
                return method;
            }
        } catch (Throwable e) {
            throwableLog("获取rpc方法失败", e);
        }
        return null;
    }

    private boolean isAutoCollectEnergy() {
        return findSwitchByKey(Constant.PREF_AUTO_SWITCH_COLLECT_ENERGY);
    }

    private boolean isAutoHelpCollect() {
        return findSwitchByKey(Constant.PREF_AUTO_SWITCH_HELP_COLLECT);
    }

    private void showToast(final String str) {
        if (h5Activity != null) {
            try {
                h5Activity.runOnUiThread(() -> {
                    MyToast.Short(h5Activity, str);
                });
            } catch (Exception unused) {
            }
        }
    }

    private void throwableLog(String tag, Throwable e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tag);
        stringBuilder.append(Log.getStackTraceString(e));
        XpLog.log(stringBuilder.toString());
    }

    private class SwitchBroadcastReceiver extends ImmediatelyBroadcastReceiver {
        private SwitchBroadcastReceiver() {
            super(Constant.Ga.ACTION_REQUEST_ACTION_INFO, Constant.Ga.ACTION_RESULT_ACTION_INFO);
            XpLog.log("***SwitchBroadcastReceiver" + hashCode());
        }

        @Override
        public boolean handlerMsg(Set<String> extraKeySet, Bundle extrasBundle) {
            for (String extraKey : extraKeySet) {
                if (Constant.Ga.KEY_GET_PREFERENCES_DATA.equals(extraKey)) {
                    fillSwitchMap((Map<String, ?>) extrasBundle.getSerializable(Constant.Ga.KEY_GET_PREFERENCES_DATA));
                } else if (Constant.Ga.KEY_BG_AUTO_COLLECT.equals(extraKey)) {
                    XpLog.log("开始接收收取信息");
                    boolean isExist = activityStatus == AS_EXIST;
                    addSendInfo(Constant.Ga.KEY_BG_AUTO_COLLECT, isExist);
                    addSendInfo(Constant.Ga.KEY_BG_COLLECT_TOKEN, extrasBundle.getString(Constant.Ga.KEY_BG_COLLECT_TOKEN));
                    if (isExist) {
                        new Thread(() -> {
                            XpLog.log("页面存在开始收取");
                            AliMobileAutoCollectEnergyManager.getInstance().rpcCall_CanCollectEnergy(rootClassLoader, extrasBundle.getString(Constant.Ga.KEY_BG_COLLECT_USER_ID));
                        }).start();
                    }
                    return true;
                } else if (Constant.Ga.KEY_BG_COLLECT_TIMING.equals(extraKey)) {
                    XpLog.log("定时任务");
                    if (activityStatus == AS_EXIST) {
                        pageCount = 0;
                        new Thread(() -> {
                            AliMobileAutoCollectEnergyManager.getInstance().rpcCall_FriendRankList(rootClassLoader);
                            AliMobileAutoCollectEnergyManager.getInstance().rpcCall_CanCollectEnergy(rootClassLoader, myUserId);
                        }).start();
                    }
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onOtherReceive(Context context, Intent intent) {
            if (Constant.Ga.ACTION_PREF_SWITCH.equals(intent.getAction())) {
                if (intent.getIntExtra(Constant.Ga.EXTRA_VALUE_TYPE, -1) == Constant.Ga.ACTION_VALUE_TYPE_BOOLEAN) {
                    switchMap.put(intent.getStringExtra(Constant.Ga.EXTRA_KEY), intent.getBooleanExtra(Constant.Ga.EXTRA_VALUE, false));
                }
            }
        }
    }

    private static class Holder {
        private static final AliMobileAutoCollectEnergyManager INSTANCE = new AliMobileAutoCollectEnergyManager();
    }

    static AliMobileAutoCollectEnergyManager getInstance() {
        return AliMobileAutoCollectEnergyManager.Holder.INSTANCE;
    }

    /**
     * hook方法的入口
     *
     * @param classLoader
     */
    static void hookDataEntrance(ClassLoader classLoader) throws Throwable {
        rootClassLoader = classLoader;
        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            /**
             * 是否为支付宝的主进程
             * @param application
             * @return
             */
            private boolean isMainProcess(Application application) {
                try {
                    int pid = android.os.Process.myPid();
                    String processName = "";
                    ActivityManager manager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
                    if (manager == null) {
                        return false;
                    }
                    for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
                        if (process.pid == pid) {
                            processName = process.processName;
                        }
                    }
                    return "com.eg.android.AlipayGphone".equals(processName);
                } catch (SecurityException e) {
                    return false;
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                super.afterHookedMethod(methodHookParam);
                Application application = (Application) methodHookParam.thisObject;
                if (!isMainProcess(application)) {
                    return;
                }
                int versionName = application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionCode;
                Class securityCILoadClass = null;
                if (versionName >= 137) {
                    securityCILoadClass = classLoader.loadClass("android.app.Dialog");
                    if (securityCILoadClass != null) {
                        XposedHelpers.findAndHookMethod(securityCILoadClass, "show", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                try {
                                    throw new NullPointerException();
                                } catch (Exception e) {

                                }
                            }
                        });
                    }
                } else {
                    classLoader.loadClass("com.alipay.mobile.base.security.CI");
                    XposedHelpers.findAndHookMethod(securityCILoadClass, "a", securityCILoadClass, Activity.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            return null;
                        }
                    });
                }
                XposedBridge.log(versionName + "***hookDataEntrance" + AliMobileAutoCollectEnergyManager.getInstance().hashCode());
                AliMobileAutoCollectEnergyManager.getInstance().initSwitchMap(application);
                final ClassLoader classLoader = application.getClassLoader();
                Class loadClass = classLoader.loadClass("com.alipay.mobile.nebulacore.ui.H5Activity");
                if (loadClass != null) {
                    XposedHelpers.findAndHookMethod(loadClass, "onResume", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            super.afterHookedMethod(methodHookParam);
                            AliMobileAutoCollectEnergyManager.getInstance().h5Activity = (Activity) methodHookParam.thisObject;
                        }
                    });
                    XposedHelpers.findAndHookMethod(loadClass, "onDestroy", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            AliMobileAutoCollectEnergyManager.getInstance().releaseData();
                            //TODO H5Activity 销毁方法
                        }
                    });
                }
                loadClass = classLoader.loadClass("com.alipay.mobile.nebulacore.ui.H5FragmentManager");
                if (!(loadClass == null || classLoader.loadClass("com.alipay.mobile.nebulacore.ui.H5Fragment") == null)) {
                    XposedHelpers.findAndHookMethod(loadClass, "pushFragment", classLoader.loadClass("com.alipay.mobile.nebulacore.ui.H5Fragment"),
                            Boolean.TYPE, Bundle.class, Boolean.TYPE, Boolean.TYPE, new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                                    super.afterHookedMethod(methodHookParam);
                                    AliMobileAutoCollectEnergyManager.getInstance().curH5Fragment = methodHookParam.args[0];
                                }
                            });
                }
                loadClass = classLoader.loadClass("com.alipay.mobile.nebulabiz.rpc.H5RpcUtil");
                if (loadClass != null) {
                    Class loadClass2 = classLoader.loadClass("com.alipay.mobile.h5container.api.H5Page");
                    Class loadClass3 = classLoader.loadClass("com.alibaba.fastjson.JSONObject");
                    if (loadClass2 != null && loadClass3 != null) {
                        XposedHelpers.findAndHookMethod(loadClass, "rpcCall", String.class,
                                String.class, String.class, Boolean.TYPE, loadClass3, String.class,
                                Boolean.TYPE, loadClass2, Integer.TYPE, String.class, Boolean.TYPE, Integer.TYPE, new XC_MethodHook() {

                                    @Override
                                    protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                                        super.afterHookedMethod(methodHookParam);
                                        if (getInstance().findSwitchByKey(Constant.PREF_MAIN_SWITCH) && (getInstance().isAutoCollectEnergy() || getInstance().isAutoHelpCollect())) {
                                            Object result = methodHookParam.getResult();
                                            if (result != null) {
                                                String str = (String) result.getClass().getMethod("getResponse", new Class[0]).invoke(result, new Object[0]);
                                                //TODO 是否是好友列表
                                                if (AliMobileAutoCollectEnergyManager.getInstance().isRankList(str)) {
                                                    AliMobileAutoCollectEnergyManager.getInstance().autoGetCanCollectUserIdList(classLoader, str);
                                                }
                                                //TODO 是否是个人详情
                                                if (AliMobileAutoCollectEnergyManager.getInstance().isUserDetail(str)) {
                                                    AliMobileAutoCollectEnergyManager.getInstance().autoGetCanCollectBubbleIdList(classLoader, str);
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }
}
