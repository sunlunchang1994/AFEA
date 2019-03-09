package com.slc.afea.model;

import android.Manifest;
import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.slc.code.app.AppData;
import com.slc.code.easypermissions.EasyPermissions;
import com.slc.code.file.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Created by achang on 2019/1/10.
 */

public class Constant {
    public static final String PACKAGE_NAME = "com.slc.afea";
    public static final String MAIN_SETTING_AUTHORITIES = PACKAGE_NAME + ".provider.MainSettingProvider";

    public static final String APP_PREFERENCES_NAME = PACKAGE_NAME + "_preferences";
    public static final String COLLECT_RECORD_PREFERENCES_NAME = "collectRecord";
    public static final String SWITCH_CONFIG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "afea";
    public static final String PREFS_FILE_PROT = "/data/user_de/0/com.slc.afea/shared_prefs/com.slc.afea_preferences.xml";
    public static final String PREF_MAIN_SWITCH = "pref_main_switch";
    public static final String PREF_AUTO_SWITCH_COLLECT_ENERGY = "pref_auto_switch_collect_energy";
    public static final String PREF_AUTO_SWITCH_HELP_COLLECT = "pref_auto_switch_help_collect";
    public static final String PREF_COLLECT_ENERGY_NOTIFICATION = "pref_collect_energy_notification";
    public static final String PREF_SAVE_COLLECT_RECORD = "pref_save_collect_record";
    public static final String PREF_LOOK_COLLECT_RECORD = "pref_look_collect_record";
    public static final String PREF_KEY_DONATE = "pref_key_donate";
    public static final String PREF_BG_COLLECT_INTERVAL = "pref_bg_collect_interval";
    public static final String PREF_KEY_SORT = "sort";

    public static class Ga {
        public static final int ACTION_VALUE_TYPE_STRING = 1;
        public static final int ACTION_VALUE_TYPE_INT = 2;
        public static final int ACTION_VALUE_TYPE_LONG = 3;
        public static final int ACTION_VALUE_TYPE_FLOAT = 4;
        public static final int ACTION_VALUE_TYPE_BOOLEAN = 5;
        public static final int ACTION_VALUE_TYPE_STRING_SET = 6;
        public static final String EXTRA_KEY = "actionKey";
        public static final String EXTRA_VALUE = "actionValue";
        public static final String EXTRA_VALUE_TYPE = "valueType";
        public static final String KEY_IS_OXYGEN_OS_ROM_OR_H2OS_ROM = "isOxygenOsRomOrH2OsRom";
        public static final String KEY_GET_PREFERENCES_DATA = "getPreferencesData";
        public static final String KEY_BG_AUTO_COLLECT = "bgAutoCollect";
        public static final String KEY_BG_COLLECT_TIMING = "bgCollectTiming";
        public static final String KEY_BG_COLLECT_TOKEN = "bgCollectToken";
        public static final String KEY_BG_COLLECT_USER_ID = "bgCollectUserId";
        public static final String KEY_COLLECT_RECORD_DATA = "collectRecordData";
        public static final String KEY_SEND_COLLECT_LATER_TIME = "sendCollectLaterTime";
        public static final String KEY_IS_ALLOW_OPEN = "isAllowOpen";
        //public static final String ACTION_PREF_CHANGED = PACKAGE_NAME + ".ACTION_PREF_CHANGED";
        public static final String ACTION_REQUEST_ACTION_INFO = PACKAGE_NAME + ".ACTION_REQUEST_ACTION_INFO";
        public static final String ACTION_RESULT_ACTION_INFO = PACKAGE_NAME + ".ACTION_RESULT_ACTION_INFO";
        public static final String ACTION_PREF_SWITCH = PACKAGE_NAME + ".ACTION_PREF_SWITCH";
        public static final String ACTION_COLLECT_RECORD_NOTIFICATION = PACKAGE_NAME + ".COLLECT_RECORD_NOTIFICATION";
        public static final String ACTION_COLLECT_LATER_TIME_NOTIFICATION = PACKAGE_NAME + ".COLLECT_LATER_TIME_NOTIFICATION";
        public static final String ACTION_COLLECT_ENERGY_NOTIFICATION_CLICK = PACKAGE_NAME + ".COLLECT_ENERGY_NOTIFICATION_CLICK";
        public static final String ACTION_START_ANT_UI = PACKAGE_NAME + ".START_ANT_UI";
        public static final String KEY_ID_ANT = "60000002";
    }

    public static class Ep {
        public static final int REQUEST_CODE_STORAGE = 1001;

        public static boolean hasStoragePermissions(Activity activity) {
            boolean hasStoragePermissions = EasyPermissions.hasPermissions(activity, Manifest.permission_group.STORAGE);
            if (hasStoragePermissions) {
                FileUtils.createOrExistsDir(AppData.getGlobalStoragePath());
            }
            return hasStoragePermissions;
        }

        public static void requestStoragePermissions(Activity activity, EasyPermissions.PermissionCallbacks permissionCallbacks,
                                                     EasyPermissions.RationaleCallbacks rationaleCallbacks) {
            EasyPermissions.requestPermissions(activity, new EasyPermissions.PermissionCallbacks() {
                        @Override
                        public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
                            if (requestCode == REQUEST_CODE_STORAGE) {
                                FileUtils.createOrExistsDir(AppData.getGlobalStoragePath());
                            }
                            permissionCallbacks.onPermissionsGranted(requestCode, perms);
                        }

                        @Override
                        public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
                            permissionCallbacks.onPermissionsDenied(requestCode, perms);
                        }
                    }, rationaleCallbacks,
                    Constant.Ep.REQUEST_CODE_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * 是否加载了模块
     *
     * @return
     */
    public static Boolean isModuleCheck() {
        return false;
    }

    /**
     * 是否是氢OS或氧OS
     *
     * @return
     */
    public static Boolean isOxygenOsRomOrH2osRom() {
        return false;
    }
}
