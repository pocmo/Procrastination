package com.androidzeitgeist.procrastination.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "procrastination";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TasksColumns.TABLE_NAME + " ("
            + TasksColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TasksColumns.TITLE + " TEXT NOT NULL, "
            + TasksColumns.DESCRIPTION + " TEXT, "
            + TasksColumns.DUE_AT + " INTEGER NOT NULL, "
            + TasksColumns.DONE_AT + " INTEGER NOT NULL, "
            + TasksColumns.CREATED_AT + " INTEGER NOT NULL"
            + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
