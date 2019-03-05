package com.slc.afea.contract;

import android.os.Bundle;

import com.slc.code.contract.MvpContract;
import com.slc.code.ui.fragment.preference.PreferenceFragment;

/**
 * Created by achang on 2019/1/11.
 */

public interface MainSettingContract {
    interface MainSettingView extends MvpContract.BaseMvpView<MainSettingPresenter> {
        PreferenceFragment getPreferenceFragment();
    }

    interface MainSettingPresenter extends MvpContract.BasePresenter<MainSettingView, MvpContract.BaseModel> {
        void init(Bundle bundle);

        void sendPreferenceChange(String str, String str2, Object obj);
        void startDonateUi();
    }
}
