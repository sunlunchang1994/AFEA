package com.slc.afea.ui.fragment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.text.method.DigitsKeyListener;
import android.widget.Toast;

import com.slc.afea.R;
import com.slc.afea.contract.MainSettingContract;
import com.slc.afea.model.Constant;
import com.slc.afea.presenter.MainSettingPresenterImp;
import com.slc.code.ui.fragment.preference.BasePreferenceFragment;
import com.slc.code.ui.fragment.preference.PreferenceFragment;

/**
 * Created by achang on 2019/1/10.
 */

public class MainSettingFragment extends BasePreferenceFragment<MainSettingContract.MainSettingPresenter>
        implements MainSettingContract.MainSettingView, Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private SwitchPreference preference_main_switch;
    private boolean isAllowOpen;

    @Override
    protected int getPreferenceFromResource() {
        return R.xml.key_system_preference;
    }

    @Override
    public void fromResourceBefore() {
        super.fromResourceBefore();
        Bundle bundle = getArguments();
        this.isAllowOpen = bundle.getBoolean(Constant.Ga.KEY_IS_ALLOW_OPEN);
        MainSettingPresenterImp.initialize(this);
        getPresenter().init(bundle);
    }

    @Override
    public void fromResourceLater() {
        super.fromResourceLater();
        this.preference_main_switch = (SwitchPreference) findPreference(Constant.PREF_MAIN_SWITCH);
        this.preference_main_switch.setOnPreferenceChangeListener(this);
        findPreference(Constant.PREF_AUTO_SWITCH_COLLECT_ENERGY).setOnPreferenceChangeListener(this);
        findPreference(Constant.PREF_AUTO_SWITCH_HELP_COLLECT).setOnPreferenceChangeListener(this);
        findPreference(Constant.PREF_COLLECT_ENERGY_NOTIFICATION).setOnPreferenceChangeListener(this);
        findPreference(Constant.PREF_SAVE_COLLECT_RECORD).setOnPreferenceChangeListener(this);
        findPreference(Constant.PREF_KEY_DONATE).setOnPreferenceClickListener(this);
        EditTextPreference bgCollectInterval = (EditTextPreference) findPreference(Constant.PREF_BG_COLLECT_INTERVAL);
        bgCollectInterval.getEditText().setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        bgCollectInterval.setOnPreferenceChangeListener(this);
        bgCollectInterval.setSummary(getString(R.string.label_x_min, getPresenter().getValueByKey(Constant.PREF_BG_COLLECT_INTERVAL, "3").toString()));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (Constant.PREF_MAIN_SWITCH.equals(key)) {
            if (this.isAllowOpen) {
                getPresenter().sendPreferenceChange(Constant.Ga.ACTION_PREF_SWITCH, key, newValue);
            } else {
                Toast.makeText(getMvpContext(), R.string.label_main_switch_summary, Toast.LENGTH_SHORT).show();
            }
            return this.isAllowOpen;
        } else if (preference instanceof SwitchPreference) {
            getPresenter().sendPreferenceChange(Constant.Ga.ACTION_PREF_SWITCH, key, newValue);
        } else if (preference instanceof EditTextPreference) {
            if (preference.getKey().equals(Constant.PREF_BG_COLLECT_INTERVAL)) {
                getPresenter().sendPreferenceChange(Constant.Ga.ACTION_PREF_SWITCH, key, newValue);
                preference.setSummary(getString(R.string.label_x_min, newValue));
            }
        }
        return true;
    }

    @Override
    public PreferenceFragment getPreferenceFragment() {
        return this;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(Constant.PREF_KEY_DONATE)) {
            getPresenter().startDonateUi();
        }
        return false;
    }
}
