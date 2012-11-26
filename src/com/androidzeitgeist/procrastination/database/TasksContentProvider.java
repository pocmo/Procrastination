package com.androidzeitgeist.procrastination.database;

import com.androidzeitgeist.procrastination.helper.NotificationHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TasksContentProvider extends ContentProvider {
    private static final String MIME_TYPE = "vnd.android.cursor.dir/com.androidzeitgeist.procrastination.tasks";

    private static final String AUTHORITY = "com.androidzeitgeist.procrastination";
    private static final String TASKS_BASE_PATH = "tasks";

    public static final Uri TASKS_URI = Uri.parse("content://" + AUTHORITY + "/" + TASKS_BASE_PATH);


    private DatabaseHelper databaseHelper;

    public static Uri createTaskUri(long id) {
        Uri.Builder builder = TASKS_URI.buildUpon();

        builder.fragment(String.valueOf(id));

        return builder.build();
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());

        return true;
    }

    @Override
    public String getType(Uri uri) {
        return MIME_TYPE;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TasksColumns.TABLE_NAME);

        Cursor cursor = queryBuilder.query(
            databaseHelper.getReadableDatabase(),
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = databaseHelper.getWritableDatabase().insert(
            TasksColumns.TABLE_NAME, null, values
        );

        getContext().getContentResolver().notifyChange(uri, null);
        
        NotificationHelper.updateNotification(getContext());

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content");
        builder.authority(AUTHORITY);
        builder.path(TASKS_BASE_PATH);
        builder.fragment(String.valueOf(id));

        return builder.build();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int affectedRows = db.update(TasksColumns.TABLE_NAME, values, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        
        NotificationHelper.updateNotification(getContext());

        return affectedRows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int affectedRows = db.delete(TasksColumns.TABLE_NAME, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        
        NotificationHelper.updateNotification(getContext());

        return affectedRows;
    }
}
