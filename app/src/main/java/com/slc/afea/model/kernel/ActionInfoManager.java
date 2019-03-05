package com.slc.afea.model.kernel;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.slc.afea.database.DaoConstant;
import com.slc.afea.model.Constant;
import com.slc.afea.utils.MyToast;
import com.slc.code.provider.RemotePreferences;
import com.slc.code.receiver.ImmediatelyBroadcastReceiver;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by achang on 2019/1/11.
 * 初始化设置，模拟服务作为返回请求设置的数据
 */

class ActionInfoManager extends XC_MethodHook {
    private boolean isOxygenOsRomOrH2OsRom = false;
    private boolean isLoadPreferences;//是否加载Preferences；
    private Map<String, ?> allData;
    private ContentResolver contentResolver;

    private static class Holder {
        private static final ActionInfoManager INSTANCE = new ActionInfoManager();
    }

    static ActionInfoManager getInstance() {
        return Holder.INSTANCE;
    }

    ActionInfoManager() {
        XposedBridge.log("**************ActionInfoManager" + hashCode());
    }

    void initSystemManager(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.android.server.am.ActivityManagerService", classLoader),
                "publishContentProviders", "android.app.IApplicationThread", List.class, this);
        Class mPhoneWindowManagerClass = XposedHelpers.findClass(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? "com.android.server.policy.PhoneWindowManager" :
                "com.android.internal.policy.impl.PhoneWindowManager", classLoader);
        String windowManagerFuncs = Build.VERSION.SDK_INT >= 28 ? "com.android.server.policy.WindowManagerPolicy.WindowManagerFuncs"
                : "android.view.WindowManagerPolicy.WindowManagerFuncs";
        XposedHelpers.findAndHookMethod(mPhoneWindowManagerClass, "init", Context.class,
                "android.view.IWindowManager", windowManagerFuncs, MethodHookPm.getInstance());
        initOxygenOsRomOrH2OsRom();
    }

    private void initOxygenOsRomOrH2OsRom() {
        if (Utils.isOxygenOsRom() || Utils.isH2OsRom()) {
            isOxygenOsRomOrH2OsRom = true;
        }
    }

    private void addOsOxygenOsRomOrH2OsRom() {
        mBroadcastReceiver.addSendInfo(Constant.Ga.KEY_IS_OXYGEN_OS_ROM_OR_H2OS_ROM, isOxygenOsRomOrH2OsRom);
    }

    /**
     * 初始化广播接收者
     *
     * @param context
     */
    final void initAction(Context context) {
        try {
            contentResolver = context.getContentResolver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constant.Ga.ACTION_REQUEST_ACTION_INFO);
            intentFilter.addAction(Constant.Ga.ACTION_COLLECT_RECORD_NOTIFICATION);
            context.registerReceiver(this.mBroadcastReceiver, intentFilter);
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        if (!isLoadPreferences) {
            Map<String, ?> allData = new RemotePreferences(MethodHookPm.getInstance().getContext(), Constant.MAIN_SETTING_AUTHORITIES, Constant.APP_PREFERENCES_NAME).getAll();
            if (!this.isLoadPreferences) {
                if (!allData.isEmpty()) {
                    this.isLoadPreferences = true;
                    this.allData = allData;
                }
            }
        }
    }

    private ImmediatelyBroadcastReceiver mBroadcastReceiver = new ImmediatelyBroadcastReceiver(Constant.Ga.ACTION_RESULT_ACTION_INFO,
            Constant.Ga.ACTION_REQUEST_ACTION_INFO) {

        @Override
        protected void onOtherReceive(Context context, Intent intent) {
            if (Constant.Ga.ACTION_COLLECT_RECORD_NOTIFICATION.equals(intent.getAction())) {
                if (Constant.Ga.KEY_COLLECT_RECORD_DATA.equals(intent.getStringExtra(Constant.Ga.EXTRA_KEY))) {
                    try {
                        contentResolver.insert(DaoConstant.CollectRecord.CONTENT_URI, intent.getParcelableExtra(Constant.Ga.KEY_COLLECT_RECORD_DATA));
                    } catch (Exception e) {
                        XposedBridge.log("存储收集信息异常" + Log.getStackTraceString(e));
                        try {
                            MyToast.Long(context, "保存记录失败 请将Xposed中的日志文件反馈到交流群");
                        } catch (Exception e1) {
                            XposedBridge.log("Toast异常" + Log.getStackTraceString(e1));
                        }
                    }
                }
            }
        }

        @Override
        public boolean handlerMsg(Set<String> extraKeySet, Bundle extrasBundle) {
            for (String extraKey : extraKeySet) {
                if (Constant.Ga.KEY_IS_OXYGEN_OS_ROM_OR_H2OS_ROM.equals(extraKey)) {
                    addOsOxygenOsRomOrH2OsRom();
                } else if (Constant.Ga.KEY_GET_PREFERENCES_DATA.equals(extraKey)) {
                    mBroadcastReceiver.addSendInfo(Constant.Ga.KEY_GET_PREFERENCES_DATA, (Serializable) allData);
                } else
                    return false;
            }
            return true;
        }
    };
}
