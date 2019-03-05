package com.slc.code.app;

import java.util.HashMap;

/**
 * Created by ontheway on 2017/10/17.
 */

public class Configurator {
    private static final HashMap<Object, Object> APP_CONFIGS = new HashMap<>();

    private Configurator() {
        APP_CONFIGS.put(ConfigKeys.CONFIG_READY.name(), false);
    }

    private static class Holder {
        private static final Configurator INSTANCE = new Configurator();
    }

    public static Configurator getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 获取配置器
     *
     * @return
     */
    public final HashMap<Object, Object> getAppConfigs() {
        return APP_CONFIGS;
    }

    /**
     * 设置配置
     */
    public final void configure() {
        APP_CONFIGS.put(ConfigKeys.CONFIG_READY.name(), true);
    }

    /**
     * 配置apiHost
     *
     * @param host
     * @return
     */
    public final Configurator withApiHost(String host) {
        APP_CONFIGS.put(ConfigKeys.API_HOST.name(), host);
        return this;
    }

    /**
     * 配置全局存储路径
     *
     * @param path
     * @return
     */
    public final Configurator withGlobalStoragePath(String path) {
        APP_CONFIGS.put(ConfigKeys.GLOBAL_STORAGE_PATH.name(), path);
        return this;
    }
    /**
     * 检查配置
     */
    private void checkConfiguration() {
        final boolean isReady = (boolean) APP_CONFIGS.get(ConfigKeys.CONFIG_READY.name());
        if (!isReady) {
            throw new RuntimeException("Configuration is not ready,call configure!");
        }
    }

    /**
     * 获取某个配置信息
     *
     * @param object
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    final <T> T getConfiguration(Object object) {
        checkConfiguration();
        final Object value = APP_CONFIGS.get(object);
    /*    if (value == null) {
            throw new NullPointerException(object.toString() + " IS NULL");
        }*/
        return (T) APP_CONFIGS.get(object);
    }
}
