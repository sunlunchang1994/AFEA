package com.slc.code.model;

import android.util.Log;

import com.slc.code.contract.MvpContract;

/**
 * Created by on the way on 2017/12/18.
 */

public abstract class BaseModelImp implements MvpContract.BaseModel {
    protected String TAG = getClass().getSimpleName();

    @Override
    public void destroy() {
        Log.i("destroy", "关闭");
    }

}
