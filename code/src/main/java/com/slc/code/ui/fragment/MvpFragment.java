package com.slc.code.ui.fragment;

import com.slc.code.contract.MvpContract;
import com.slc.code.exception.MvpNullPointerException;
import com.slc.code.exception.MvpUninitializedException;
import com.slc.code.ui.activity.MvpActivity;

/**
 * Created by on the way on 2018/11/5.
 */

public class MvpFragment<P extends MvpContract.BasePresenter> extends EnhanceFragment implements MvpContract.BaseMvpView<P> {
    /**
     * mvp模式的主持者
     */
    private P mPresenter;
    private boolean isInitPresenter;//是否初始化Presenter

    @Override
    public MvpActivity getMvpContext() {
        return (MvpActivity) getActivity();
    }

    /**
     * 设置presenter
     *
     * @param presenter
     */
    public void setPresenter(P presenter) {
        isInitPresenter = true;
        this.mPresenter = presenter;
    }

    /**
     * 获取presenter
     *
     * @return
     */
    protected P getPresenter() {
        if (this.mPresenter != null) {
            return this.mPresenter;
        } else if (isInitPresenter) {
            throw new MvpNullPointerException("mPresenter destroyed");
        } else {
            throw new MvpUninitializedException("mPresenter uninitialized");
        }
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }
        super.onDestroyView();
    }

}
