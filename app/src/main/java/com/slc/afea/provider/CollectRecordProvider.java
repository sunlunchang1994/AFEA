package com.slc.afea.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.slc.afea.database.DaoConstant;

/**
 * Created by achang on 2019/1/15.
 */

public class CollectRecordProvider extends ContentProvider {
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private SQLiteDatabase sqLiteDatabase;

    static {
        uriMatcher.addURI(DaoConstant.CollectRecord.COLLECT_RECORD_AUTHORITIES, DaoConstant.CollectRecord.AFEA_RECORD, DaoConstant.CollectRecord.DB_TABLE_AFEA_RECORD);
    }

    @Override
    public boolean onCreate() {
        CollectRecordOpenHelper openHelper = new CollectRecordOpenHelper(getContext());
        sqLiteDatabase = openHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) != DaoConstant.CollectRecord.DB_TABLE_AFEA_RECORD) {
            throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        Long rawId = sqLiteDatabase.insert(DaoConstant.CollectRecord.TABLE_NAME, null, values);
        Uri returnUri = ContentUris.withAppendedId(DaoConstant.CollectRecord.CONTENT_URI, rawId);
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
