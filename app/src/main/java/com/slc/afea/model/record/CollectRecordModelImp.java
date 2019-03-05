package com.slc.afea.model.record;

import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.slc.afea.contract.CollectRecordContract;
import com.slc.afea.contract.OnResultListener;
import com.slc.afea.database.CollectRecord;
import com.slc.afea.database.CollectRecordDao;
import com.slc.afea.database.DaoConstant;
import com.slc.afea.database.DaoManager;
import com.slc.code.model.BaseModelImp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by achang on 2019/1/15.
 */

public class CollectRecordModelImp extends BaseModelImp implements CollectRecordContract.CollectRecordModel {
    public static final int SORT_DATE_POSITIVE = 0;
    public static final int SORT_DATE_NEGATIVE = 1;
    public static final int SORT_NUMBER_FEW_MORE = 2;
    public static final int SORT_NUMBER_MORE_FEW = 3;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void destroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.destroy();
    }

    @Override
    public void getAllCollectRecord(long timestamp, int sortId, CollectRecordContract.CollectRecordOnResultListener onResultListener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                CollectRecordDao collectRecordDao = DaoManager.getInstance().getCollectRecordDao();
                final List<CollectRecord> collectRecordList = new ArrayList<>();
                switch (sortId) {
                    case SORT_DATE_NEGATIVE:
                        collectRecordList.addAll(collectRecordDao.queryBuilder()
                                .where(CollectRecordDao.Properties.Time.between(timestamp, timestamp + 86400000)).orderDesc(CollectRecordDao.Properties.Time).list());
                        break;
                    case SORT_NUMBER_FEW_MORE:
                        collectRecordList.addAll(collectRecordDao.queryBuilder()
                                .where(CollectRecordDao.Properties.Time.between(timestamp, timestamp + 86400000)).orderAsc(CollectRecordDao.Properties.Collect).list());
                        break;
                    case SORT_NUMBER_MORE_FEW:
                        collectRecordList.addAll(collectRecordDao.queryBuilder()
                                .where(CollectRecordDao.Properties.Time.between(timestamp, timestamp + 86400000)).orderDesc(CollectRecordDao.Properties.Collect).list());
                        break;
                    default:
                        collectRecordList.addAll(collectRecordDao.queryBuilder()
                                .where(CollectRecordDao.Properties.Time.between(timestamp, timestamp + 86400000)).list());
                        break;
                }
                Cursor cursor = DaoManager.getInstance().getDaoSession().getDatabase().rawQuery("select sum(" + CollectRecordDao.Properties.Collect.columnName +
                        ") from " + DaoConstant.CollectRecord.TABLE_NAME + " where " +
                        CollectRecordDao.Properties.OperateType.columnName + "=" +
                        DaoConstant.CollectRecord.OPERATE_COLLECT_ENERGY + " and " +
                        CollectRecordDao.Properties.Time.columnName + " between " +
                        timestamp + " and " + (timestamp + 86400000), null);
                cursor.moveToFirst();
                final int collectEnergy = cursor.getInt(0);
                cursor.close();
                cursor = DaoManager.getInstance().getDaoSession().getDatabase().rawQuery("select sum(" + CollectRecordDao.Properties.Collect.columnName +
                        ") from " + DaoConstant.CollectRecord.TABLE_NAME + " where " +
                        CollectRecordDao.Properties.OperateType.columnName + "=" +
                        DaoConstant.CollectRecord.OPERATE_HELP_COLLECT + " and " +
                        CollectRecordDao.Properties.Time.columnName + " between " +
                        timestamp + " and " + (timestamp + 86400000), null);
                cursor.moveToFirst();
                final int helpCollect = cursor.getInt(0);
                cursor.close();
                mHandler.post(() -> {
                    onResultListener.onSums(collectEnergy, helpCollect);
                    if (collectRecordList == null || collectRecordList.isEmpty()) {
                        onResultListener.onError("0001", "data is null");
                        return;
                    }
                    onResultListener.onSucceed(collectRecordList);
                });
            }
        }.start();
    }
}
