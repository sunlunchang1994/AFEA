package com.slc.afea;

import android.app.Application;
import android.os.Environment;

import com.slc.afea.database.DaoManager;
import com.slc.code.app.AppData;

import java.io.Externalizable;
import java.io.File;

/**
 * Created by achang on 2019/1/14.
 */

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppData.init(this).withGlobalStoragePath(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "afea").configure();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DaoManager.getInstance().close();
    }
}
