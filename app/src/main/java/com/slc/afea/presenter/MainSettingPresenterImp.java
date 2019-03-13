package com.slc.afea.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.slc.afea.contract.MainSettingContract;
import com.slc.afea.model.Constant;
import com.slc.code.contract.MvpContract;
import com.slc.code.presenter.MvpPresenterImp;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static com.slc.code.receiver.ImmediatelyBroadcastReceiver.EXTRA_KEY_LIST;
import static com.slc.code.receiver.ImmediatelyBroadcastReceiver.EXTRA_VALUE_BUNDLE;

/**
 * Created by achang on 2019/1/11.
 */

public class MainSettingPresenterImp extends MvpPresenterImp<MainSettingContract.MainSettingView, MvpContract.BaseModel>
        implements MainSettingContract.MainSettingPresenter {
    private SharedPreferences sharedPreferences;

    public static void initialize(MainSettingContract.MainSettingView view) {
        new MainSettingPresenterImp(view).start();
    }

    private MainSettingPresenterImp(MainSettingContract.MainSettingView view) {
        super(view);
    }

    @Override
    public void init(Bundle bundle) {
        this.sharedPreferences = getContext().getSharedPreferences(Constant.APP_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void sendPreferenceChange(String action, String key, Object value) {
        Intent intent = new Intent(action);
        intent.putExtra(Constant.Ga.EXTRA_KEY, key);
        if (value instanceof String) {
            intent.putExtra(Constant.Ga.EXTRA_VALUE, value.toString());
            intent.putExtra(Constant.Ga.EXTRA_VALUE_TYPE, Constant.Ga.ACTION_VALUE_TYPE_STRING);
        } else if (value instanceof Integer) {
            intent.putExtra(Constant.Ga.EXTRA_VALUE, ((Integer) value).intValue());
            intent.putExtra(Constant.Ga.EXTRA_VALUE_TYPE, Constant.Ga.ACTION_VALUE_TYPE_INT);
        } else if (value instanceof Boolean) {
            intent.putExtra(Constant.Ga.EXTRA_VALUE, ((Boolean) value).booleanValue());
            intent.putExtra(Constant.Ga.EXTRA_VALUE_TYPE, Constant.Ga.ACTION_VALUE_TYPE_BOOLEAN);
        } else {
            throw new ClassCastException("请对value的类型做判断处理");
        }
        getContext().sendBroadcast(intent);
    }

    @Override
    public void startDonateUi() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String payUrl = "https://qr.alipay.com/a6x00741uv1j6tjq3m2dm27";
        intent.setData(Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + payUrl));
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(intent);
        } else {
            intent.setData(Uri.parse(payUrl));
        }
        getContext().startActivity(intent);
    }

    @Override
    public Object getValueByKey(String key, Object defValue) {
        Object value = this.sharedPreferences.getAll().get(key);
        return value == null ? defValue : value;
    }

    @Override
    public void sendAllData() {
        getContext().post(new Runnable() {
            @Override
            public void run() {
                Set<String> extraKeySet = new HashSet<>();
                extraKeySet.add(Constant.Ga.KEY_GET_PREFERENCES_DATA);
                Bundle extrasBundle = new Bundle();
                extrasBundle.putSerializable(Constant.Ga.KEY_GET_PREFERENCES_DATA, (Serializable) sharedPreferences.getAll());
                Intent intent = new Intent(Constant.Ga.ACTION_RESULT_ACTION_INFO);
                intent.putExtra(EXTRA_KEY_LIST, (Serializable) extraKeySet);
                intent.putExtras(extrasBundle);
                getContext().sendBroadcast(intent);
            }
        });

    }
}
