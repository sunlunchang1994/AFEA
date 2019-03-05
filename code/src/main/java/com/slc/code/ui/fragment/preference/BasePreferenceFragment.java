package com.slc.code.ui.fragment.preference;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.slc.code.contract.MvpContract.BaseMvpView;
import com.slc.code.contract.MvpContract.BasePresenter;
import com.slc.code.exception.MvpNullPointerException;
import com.slc.code.exception.MvpUninitializedException;
import com.slc.code.ui.activity.MvpActivity;

public abstract class BasePreferenceFragment<P extends BasePresenter> extends PreferenceFragment
        implements BaseMvpView<P> {
    private boolean isInitPresenter;
    private P mPresenter;

    protected abstract int getPreferenceFromResource();

    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        fromResourceBefore();
        addPreferencesFromResource(getPreferenceFromResource());
        fromResourceLater();
    }

    public void fromResourceBefore() {
    }

    public void fromResourceLater() {
    }

    public MvpActivity getMvpContext() {
        return (MvpActivity) getActivity();
    }

    public void setPresenter(P presenter) {

        this.isInitPresenter = true;
        this.mPresenter = presenter;
    }

    protected P getPresenter() {
        if (this.mPresenter != null) {
            return this.mPresenter;
        }
        if (this.isInitPresenter) {
            throw new MvpNullPointerException("mPresenter destroyed");
        }
        throw new MvpUninitializedException("mPresenter uninitialized");
    }

    public void onDestroyView() {
        if (this.mPresenter != null) {
            this.mPresenter.destroy();
            this.mPresenter = null;
        }
        super.onDestroyView();
    }
}
