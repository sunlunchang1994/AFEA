package com.slc.afea.database;

import android.net.Uri;

import com.slc.afea.model.Constant;

/**
 * Created by achang on 2019/1/15.
 */

public class DaoConstant {
    public static class CollectRecord {
        public final static String AFEA_RECORD = "afea_record.db";
        public static final String TABLE_NAME = CollectRecordDao.TABLENAME;
        public static final int SCHEMA_VERSION = 1;
        public static final int DB_TABLE_AFEA_RECORD = 1;
        public static final String COLLECT_RECORD_AUTHORITIES = Constant.PACKAGE_NAME + ".provider.CollectRecordProvider";
        public static final int OPERATE_COLLECT_ENERGY = 0;
        public static final int OPERATE_HELP_COLLECT = 1;
        public static final Uri CONTENT_URI = Uri.parse("content://" + DaoConstant.CollectRecord.COLLECT_RECORD_AUTHORITIES + "/" + DaoConstant.CollectRecord.AFEA_RECORD);
    }
}
