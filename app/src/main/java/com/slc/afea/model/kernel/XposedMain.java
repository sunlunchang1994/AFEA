package com.slc.afea.model.kernel;

import android.app.PendingIntent;
import android.content.res.XModuleResources;

import com.slc.afea.R;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * Created by achang on 2019/1/10.
 */

public class XposedMain implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {
    private static String MODULE_PATH = null;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("android") && loadPackageParam.processName.equals("android")) {
            ActionInfoManager.getInstance().initSystemManager(loadPackageParam.classLoader);
        } else if (loadPackageParam.packageName.equals("com.slc.afea")) {
            XposedHelpers.findAndHookMethod("com.slc.afea.model.Constant",
                    loadPackageParam.classLoader, "isModuleCheck",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            param.setResult(true);
                        }
                    });
            XposedHelpers.findAndHookMethod("com.slc.afea.model.Constant",
                    loadPackageParam.classLoader, "isOxygenOsRomOrH2osRom",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                        }
                    });
        } else if (loadPackageParam.packageName.equals("com.eg.android.AlipayGphone")) {
            AliMobileAutoCollectEnergyManager.hookDataEntrance(loadPackageParam.classLoader);
        } /*else if (loadPackageParam.packageName.equals("com.android.systemui") && loadPackageParam.processName.equals("com.android.systemui")) {
            SystemUIManager.initHook(loadPackageParam.classLoader);
        }*/
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (resparam.packageName.equals("com.android.systemui")) {
            XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
            //SystemUIManager.getInstance().notificationIconRes = resparam.res.addResource(modRes, R.mipmap.ic_system_ui);
        }
    }

}
