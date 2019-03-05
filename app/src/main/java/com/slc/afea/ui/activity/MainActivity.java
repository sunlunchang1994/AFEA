package com.slc.afea.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.slc.afea.R;
import com.slc.afea.contract.MainContract;
import com.slc.afea.presenter.MainPresenterImp;
import com.slc.afea.ui.fragment.MainSettingFragment;
import com.slc.code.app.AppData;
import com.slc.code.ui.activity.NativeToolBarActivity;

/**
 * Created by achang on 2019/1/10.
 */

public class MainActivity extends NativeToolBarActivity<MainContract.MainPresenter> implements MainContract.MainView {
    @Override
    protected void onBindView(@Nullable Bundle savedInstanceState) {
        setToolBarTitle(R.string.app_name);
    }

    @Override
    public Object setDeveloperView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        MainPresenterImp.initialize(this);
    }

    @Override
    public void loadFragment(Bundle bundle) {
        MainSettingFragment mainSettingFragment = new MainSettingFragment();
        mainSettingFragment.setArguments(bundle);
        loadRootFragment(R.id.contentView, mainSettingFragment);
        Log.i("loadFragment", AppData.getGlobalStoragePath());
    }
}

