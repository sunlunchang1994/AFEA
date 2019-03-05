package com.slc.afea.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.slc.afea.model.Constant;
import com.slc.afea.utils.Intents;

/**
 * Created by achang on 2019/1/31.
 */

public class StartAntUiActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intents.startApp(this, Constant.Ga.KEY_ID_ANT);
        finish();
    }
}
