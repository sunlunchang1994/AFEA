package com.slc.afea.model.kernel;

import android.os.Build;

import de.robv.android.xposed.XposedHelpers;

public class Utils {
    private static String TAG = "Utils";

    private static Boolean mIsOxygenOsRom;
    private static Boolean mIsH2OsRom;

    public static boolean isOxygenOsRom() {
        if (mIsOxygenOsRom == null) {
            String version = SystemProp.get("ro.oxygen.version", "0");
            mIsOxygenOsRom = version != null && !version.isEmpty() && !"0".equals(version);
        }
        return mIsOxygenOsRom;
    }

    static boolean isH2OsRom() {
        if (mIsH2OsRom == null) {
            String version = SystemProp.get("ro.rom.version", "0");
            mIsH2OsRom = version != null && !version.isEmpty() && version.contains("H2OS");
        }
        return mIsH2OsRom;
    }

    public static boolean isParanoidRom() {
        return (Build.DISPLAY != null && Build.DISPLAY.startsWith("pa_"));
    }

    static class SystemProp extends Utils {

        private SystemProp() {

        }

        // Get the value for the given key
        // @param key: key to lookup
        // @return null if the key isn't found
        public static String get(String key) {
            String ret;

            try {
                Class<?> classSystemProperties = XposedHelpers.findClass("android.os.SystemProperties", null);
                ret = (String) XposedHelpers.callStaticMethod(classSystemProperties, "get", key);
            } catch (Throwable t) {
                ret = null;
            }
            return ret;
        }

        // Get the value for the given key
        // @param key: key to lookup
        // @param def: default value to return
        // @return if the key isn't found, return def if it isn't null, or an empty string otherwise
        public static String get(String key, String def) {
            String ret = def;

            try {
                Class<?> classSystemProperties = XposedHelpers.findClass("android.os.SystemProperties", null);
                ret = (String) XposedHelpers.callStaticMethod(classSystemProperties, "get", key, def);
            } catch (Throwable t) {
                ret = def;
            }
            return ret;
        }

        // Get the value for the given key, and return as an integer
        // @param key: key to lookup
        // @param def: default value to return
        // @return the key parsed as an integer, or def if the key isn't found or cannot be parsed
        public static Integer getInt(String key, Integer def) {
            Integer ret = def;

            try {
                Class<?> classSystemProperties = XposedHelpers.findClass("android.os.SystemProperties", null);
                ret = (Integer) XposedHelpers.callStaticMethod(classSystemProperties, "getInt", key, def);
            } catch (Throwable t) {
                ret = def;
            }
            return ret;
        }

        // Get the value for the given key, and return as a long
        // @param key: key to lookup
        // @param def: default value to return
        // @return the key parsed as a long, or def if the key isn't found or cannot be parsed
        public static Long getLong(String key, Long def) {
            Long ret = def;

            try {
                Class<?> classSystemProperties = XposedHelpers.findClass("android.os.SystemProperties", null);
                ret = (Long) XposedHelpers.callStaticMethod(classSystemProperties, "getLong", key, def);
            } catch (Throwable t) {
                ret = def;
            }
            return ret;
        }

        // Get the value (case insensitive) for the given key, returned as a boolean
        // Values 'n', 'no', '0', 'false' or 'off' are considered false
        // Values 'y', 'yes', '1', 'true' or 'on' are considered true
        // If the key does not exist, or has any other value, then the default result is returned
        // @param key: key to lookup
        // @param def: default value to return
        // @return the key parsed as a boolean, or def if the key isn't found or cannot be parsed
        public static Boolean getBoolean(String key, boolean def) {
            Boolean ret = def;

            try {
                Class<?> classSystemProperties = XposedHelpers.findClass("android.os.SystemProperties", null);
                ret = (Boolean) XposedHelpers.callStaticMethod(classSystemProperties, "getBoolean", key, def);
            } catch (Throwable t) {
                ret = def;
            }
            return ret;
        }

        // Set the value for the given key
        public static void set(String key, String val) {
            try {
                Class<?> classSystemProperties = XposedHelpers.findClass("android.os.SystemProperties", null);
                XposedHelpers.callStaticMethod(classSystemProperties, "set", key, val);
            } catch (Throwable t) {
            }
        }
    }
}
