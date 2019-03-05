package com.slc.afea.database;

import android.database.sqlite.SQLiteDatabase;

import com.slc.code.app.AppData;


/**
 * 数据库管理
 * Created by on the way on 2017/11/17.
 */

public class DaoManager {

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static SQLiteDatabase sqliteDatabase;

    private DaoManager() {
        sqliteDatabase = new AfeaHelper(AppData.getApplicationContext(), DaoConstant.CollectRecord.AFEA_RECORD).getWritableDatabase();
        //sqliteDatabase = new DaoMaster.DevOpenHelper(AppData.getApplicationContext(), AFEA_RECORD, null).getWritableDatabase();
        daoMaster = new DaoMaster(sqliteDatabase);
        daoSession = daoMaster.newSession();
    }

    private static final class Holder {
        private static final DaoManager LEARN_DAO_MANAGER = new DaoManager();
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static final DaoManager getInstance() {
        return Holder.LEARN_DAO_MANAGER;
    }

    /**
     * 获取session
     *
     * @return
     */
    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 获取收集记录
     *
     * @return
     */
    public CollectRecordDao getCollectRecordDao() {
        return daoSession.getCollectRecordDao();
    }

    /**
     * 获取SqliteDatabase
     *
     * @return
     */
    public SQLiteDatabase getSqliteDatabase() {
        return sqliteDatabase;
    }

    /**
     * 关闭
     */
    public void close() {
        daoSession.clear();
        sqliteDatabase.close();
    }
}
