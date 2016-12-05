package com.weimc.history.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/12/6.
 */

public class HistoryOpenHelper extends SQLiteOpenHelper {
    public HistoryOpenHelper(Context context) {
        super(context, HistoryDBSchema.HistoryTable.TABLE_NAME, null, HistoryDBSchema.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + HistoryDBSchema.HistoryTable.TABLE_NAME
                + " ( " + "_id primary key autoincrement, "
                + HistoryDBSchema.HistoryTable.Cols.title + " ,"
                + HistoryDBSchema.HistoryTable.Cols.year + " ,"
                + HistoryDBSchema.HistoryTable.Cols.month + " ,"
                + HistoryDBSchema.HistoryTable.Cols.day + " ,"
                + HistoryDBSchema.HistoryTable.Cols.content + " ,"
                + HistoryDBSchema.HistoryTable.Cols.des + " ,"
                + HistoryDBSchema.HistoryTable.Cols.id + " ,"
                + HistoryDBSchema.HistoryTable.Cols.pic + " ,"
                + HistoryDBSchema.HistoryTable.Cols.lunar + " ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
