package com.slc.afea.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.slc.afea.database.DaoConstant;

/**
 * Created by achang on 2019/1/15.
 */

public class CollectRecordOpenHelper extends SQLiteOpenHelper {

    public CollectRecordOpenHelper(Context context) {
        super(context, DaoConstant.CollectRecord.AFEA_RECORD, null, DaoConstant.CollectRecord.SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DaoConstant.CollectRecord.TABLE_NAME +
                " ( _id INTEGER PRIMARY KEY NOT NULL , TIME INTEGER NOT NULL , NAME TEXT, COLLECT INTEGER NOT NULL , OPERATE_TYPE INTEGER NOT NULL );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
