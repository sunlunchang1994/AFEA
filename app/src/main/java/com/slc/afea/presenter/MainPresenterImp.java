package com.slc.afea.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.slc.afea.R;
import com.slc.afea.contract.MainContract;
import com.slc.afea.model.Constant;
import com.slc.code.contract.MvpContract.BaseModel;
import com.slc.code.easypermissions.EasyPermissions;
import com.slc.code.easypermissions.RationaleDialog;
import com.slc.code.easypermissions.helper.Rationale;
import com.slc.code.presenter.MvpPresenterImp;

import java.util.List;

public class MainPresenterImp extends MvpPresenterImp<MainContract.MainView, BaseModel> implements MainContract.MainPresenter {

    public static void initialize(MainContract.MainView view) {
        new MainPresenterImp(view).start();
    }

    private MainPresenterImp(MainContract.MainView view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.Ga.KEY_IS_OXYGEN_OS_ROM_OR_H2OS_ROM, Constant.isOxygenOsRomOrH2osRom());
        bundle.putBoolean(Constant.Ga.KEY_IS_ALLOW_OPEN, Constant.isModuleCheck());
        getView().loadFragment(bundle);
    }
}
