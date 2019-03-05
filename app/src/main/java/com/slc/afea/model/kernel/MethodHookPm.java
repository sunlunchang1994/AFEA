package com.slc.afea.model.kernel;

import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class MethodHookPm extends XC_MethodHook {
    private Context mContext;
    private Object mPhoneWindowManager;

    private static class Holder {
        private static final MethodHookPm INSTANCE = new MethodHookPm();
    }

    static MethodHookPm getInstance() {
        return Holder.INSTANCE;
    }

    Context getContext() {
        return this.mContext;
    }

    Object getPhoneWindowManager() {
        return this.mPhoneWindowManager;
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        this.mPhoneWindowManager = param.thisObject;
        this.mContext = (Context) XposedHelpers.getObjectField(this.mPhoneWindowManager, "mContext");
        ActionInfoManager.getInstance().initAction(this.mContext);
        XposedBridge.log("初始化phoneWindowManager成功");
    }
}
