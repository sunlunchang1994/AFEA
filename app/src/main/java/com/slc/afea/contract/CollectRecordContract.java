package com.slc.afea.contract;

import com.slc.afea.database.CollectRecord;
import com.slc.code.contract.MvpContract;

import java.util.Calendar;
import java.util.List;

/**
 * Created by achang on 2019/1/15.
 */

public interface CollectRecordContract {
    interface CollectRecordView extends MvpContract.BaseMvpView<CollectRecordPresenter> {
        void refresh();

        void upoDateTitle(String title);

        void upoDateSubTitle(String subTitle);
    }

    interface CollectRecordPresenter extends MvpContract.BasePresenter<CollectRecordView, CollectRecordModel>, CollectRecordContract.CollectRecordOnResultListener {
        List<CollectRecord> getDateList();

        void getCollectRecordDataByDate();

        Calendar getCalendar();

        String intToStringOfDibit(int intData);

        void sort(int sortId);
    }

    interface CollectRecordModel extends MvpContract.BaseModel {
        void getAllCollectRecord(long timestamp, int sortId, CollectRecordOnResultListener onResultListener);
    }

    interface CollectRecordOnResultListener extends OnResultListener<List<CollectRecord>> {
        void onSums(int collectEnergy, int helpCollect);
    }
}
