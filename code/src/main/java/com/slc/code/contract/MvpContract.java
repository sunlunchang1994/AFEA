package com.slc.code.contract;

import com.slc.code.ui.activity.MvpActivity;

/**
 * Created by on the way on 2018/11/5.
 */

public interface MvpContract {
    interface BaseMvpView<P extends BasePresenter> {
        /**
         * 获取上下文环境mvp模式
         *
         * @return
         */
        MvpActivity getMvpContext();

        /**
         * 设置Presenter
         */
        void setPresenter(P presenter);
    }

    interface BasePresenter<V extends BaseMvpView, M extends BaseModel> {
        /**
         * 设置model
         *
         * @param model
         */
        void setModel(M model);

        /**
         * 设置model
         *
         * @param view
         */
        void setView(V view);

        /**
         * 开始方法，一般在activity或fragment初始化完试图之后调用
         */
        void start();

        /**
         * 销毁
         */
        void destroy();
    }

    interface BaseModel {
        /**
         * 销毁
         */
        void destroy();
    }
}
