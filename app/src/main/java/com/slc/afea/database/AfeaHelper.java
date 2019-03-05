package com.slc.afea.database;


import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

/**
 * Created by zhangqie on 2016/3/26.
 */

public class AfeaHelper extends DaoMaster.OpenHelper {

    public AfeaHelper(Context context, String dbName) {
        super(context, dbName, null);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        Log.i("version", oldVersion + "---先前和更新之后的版本---" + newVersion);
        if (oldVersion < newVersion) {
            Log.i("version", oldVersion + "---先前和更新之后的版本---" + newVersion);
            /*MigrationHelper.getInstance().migrate(db, UserMessageDao.class);
            MigrationHelper.getInstance().migrate(db, ChatMessageDao.class);
            MigrationHelper.getInstance().migrate(db, StickDao.class);*/
        }
    }
}
