package com.slc.afea.contract;

import android.os.Bundle;

import com.slc.code.contract.MvpContract.BaseModel;
import com.slc.code.contract.MvpContract.BaseMvpView;
import com.slc.code.contract.MvpContract.BasePresenter;

public interface MainContract {

    interface MainPresenter extends BasePresenter<MainView, BaseModel> {
    }

    interface MainView extends BaseMvpView<MainPresenter> {
        void loadFragment(Bundle bundle);
    }

    abstract class LoadingSettingResponseTimeOutRunnable implements Runnable {
        protected Bundle bundle;
        private OnRunBeforeListener onRunBeforeListener;

        public LoadingSettingResponseTimeOutRunnable setBundle(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public LoadingSettingResponseTimeOutRunnable setOnRunBeforeListener(OnRunBeforeListener onRunBeforeListener) {
            this.onRunBeforeListener = onRunBeforeListener;
            return this;
        }

        @Override
        public void run() {
            if (onRunBeforeListener != null) {
                onRunBeforeListener.onRunBefore();
            }
        }

        public interface OnRunBeforeListener {
            void onRunBefore();
        }
    }

}
