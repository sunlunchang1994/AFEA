package com.slc.afea.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.slc.afea.R;
import com.slc.afea.contract.CollectRecordContract;
import com.slc.afea.contract.OnResultListener;
import com.slc.afea.database.CollectRecord;
import com.slc.afea.model.Constant;
import com.slc.afea.model.record.CollectRecordModelImp;
import com.slc.code.presenter.MvpPresenterImp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by achang on 2019/1/15.
 */

public class CollectRecordPresenterImp extends MvpPresenterImp<CollectRecordContract.CollectRecordView,
        CollectRecordContract.CollectRecordModel> implements CollectRecordContract.CollectRecordPresenter {
    private List<CollectRecord> collectRecordList = new ArrayList<>();
    private Calendar calendar;
    private int sort=CollectRecordModelImp.SORT_DATE_POSITIVE;
    private SharedPreferences collectRecordPreferences;

    public CollectRecordPresenterImp(CollectRecordContract.CollectRecordView view) {
        super(view);
        setModel(new CollectRecordModelImp());
    }

    @Override
    public void start() {
        super.start();
        collectRecordPreferences = getContext().getSharedPreferences(Constant.COLLECT_RECORD_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sort=collectRecordPreferences.getInt(Constant.PREF_KEY_SORT,CollectRecordModelImp.SORT_DATE_POSITIVE);
        calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0);
        getView().upoDateSubTitle(calendar.get(Calendar.YEAR) + "-" +
                intToStringOfDibit(calendar.get(Calendar.MONTH) + 1) + "-" +
                intToStringOfDibit(calendar.get(Calendar.DAY_OF_MONTH)));
        getCollectRecordDataByDate();
    }

    @Override
    public void getCollectRecordDataByDate() {
        collectRecordList.clear();
        getModel().getAllCollectRecord(calendar.getTimeInMillis(), sort, this);
    }

    @Override
    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public String intToStringOfDibit(int intData) {
        return intData < 10 ? "0" + String.valueOf(intData) : String.valueOf(intData);
    }

    @Override
    public void sort(int sortId) {
        if (sort != sortId) {
            sort = sortId;
            collectRecordPreferences.edit().putInt(Constant.PREF_KEY_SORT,sort).apply();
            getCollectRecordDataByDate();
        }
    }

    @Override
    public void onSucceed(List<CollectRecord> data) {
        collectRecordList.addAll(data);
        getView().refresh();
    }

    @Override
    public void onError(String errorCode, String errorMsg) {
        Toast.makeText(getContext(), R.string.label_no_collect_record, Toast.LENGTH_SHORT).show();
        getView().refresh();
    }

    @Override
    public void onSums(int collectEnergy, int helpCollect) {
        getView().upoDateTitle(getContext().getResources().getString(R.string.label_collect_record_of_title,
                String.valueOf(collectEnergy), String.valueOf(helpCollect)));
    }

    @Override
    public List<CollectRecord> getDateList() {
        return collectRecordList;
    }

}
