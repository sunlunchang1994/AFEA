package com.slc.code.presenter;

import com.slc.code.contract.MvpContract;
import com.slc.code.exception.MvpNullPointerException;
import com.slc.code.exception.MvpUninitializedException;
import com.slc.code.ui.activity.MvpActivity;

/**
 * Created by on the way on 2017/12/19.
 */

public class MvpPresenterImp<V extends MvpContract.BaseMvpView, M extends MvpContract.BaseModel> implements MvpContract.BasePresenter<V, M> {
    public final String TAG = getClass().getSimpleName();
    private MvpActivity mContext;
    private M mModel;
    private V mView;
    private boolean isInitModel, isInitView;//是否初始化Model

    public MvpPresenterImp(V view) {
        setView(view);
    }

    /**
     * 获取mvp模式的上下文环境
     *
     * @return
     */
    protected MvpActivity getContext() {
        if (mContext != null) {
            return mContext;
        } else {
            throw new MvpNullPointerException("mContext destroyed");
        }
    }

    @Override
    public void start() {
        //TODO 开始方法
    }

    /**
     * 设置model
     *
     * @param model
     */
    public void setModel(M model) {
        this.mModel = model;
        isInitModel = true;
    }

    /**
     * 设置视图
     *
     * @param view
     */
    @Override
    public void setView(V view) {
        this.mView = view;
        mContext = mView.getMvpContext();
        mView.setPresenter(this);
        isInitView = true;
    }

    /**
     * 获取view
     *
     * @return
     */
    protected V getView() {
        if (mView != null) {
            return mView;
        } else if (isInitView) {
            throw new MvpNullPointerException("mView destroyed");
        } else {
            throw new MvpUninitializedException("mView uninitialized");
        }
    }

    /**
     * 获取model
     *
     * @return
     */
    protected M getModel() {
        if (this.mModel != null) {
            return this.mModel;
        } else if (isInitModel) {
            throw new MvpNullPointerException("mModel destroyed");
        } else {
            throw new MvpUninitializedException("mModel uninitialized");
        }
    }

    @Override
    public void destroy() {
        if (mModel != null) {
            mModel.destroy();
        }
        mView = null;
    }
}
