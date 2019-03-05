package com.slc.afea.provider;

import com.slc.afea.model.Constant;
import com.slc.code.provider.RemotePreferenceProvider;

public class MainSettingProvider extends RemotePreferenceProvider {
    public MainSettingProvider() {
        super(Constant.MAIN_SETTING_AUTHORITIES, Constant.APP_PREFERENCES_NAME);
    }
}