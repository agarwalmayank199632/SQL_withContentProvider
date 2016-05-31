package com.mayank.newsqltry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "tutorial_data";

    public static final String TABLE_TUTORIALS = "tutorials2";
    public static final String ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_URL = "url";

    private static final String CREATE_TABLE_TUTORIALS = "create table " + TABLE_TUTORIALS
            + " (" + ID + " integer primary key autoincrement, " + COL_TITLE
            + " text not null, " + COL_URL + " text not null);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TUTORIALS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TUTORIALS);
        onCreate(db);
    }
}