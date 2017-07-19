package com.androidzeitgeist.procrastination.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.androidzeitgeist.procrastination.database.TasksColumns;
import com.androidzeitgeist.procrastination.database.TasksContentProvider;

public class Task {
    private long id;
    private String title;
    private long doneAt;
    private long dueAt;

    public Task(Cursor cursor) {
        title = cursor.getString(cursor.getColumnIndex(TasksColumns.TITLE));
        id = cursor.getLong(cursor.getColumnIndex(TasksColumns._ID));
        doneAt = cursor.getLong(cursor.getColumnIndex(TasksColumns.DONE_AT));
        dueAt = cursor.getLong(cursor.getColumnIndex(TasksColumns.DUE_AT));
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public boolean isDone() {
        return doneAt > 0;
    }
    
    public long getDueAt() {
        return dueAt;
    }
    
    public long getDoneAt() {
        return doneAt;
    }

    public Uri getUri() {
        Log.d("Procrastination", "Task URI: " + TasksContentProvider.createTaskUri(getId()).toString());
        
        return TasksContentProvider.createTaskUri(getId());
    }
    
    public String getHistoryHeader() {
        return SimpleDateFormat.getDateInstance().format(
            new Date(getDoneAt())
        );
    }
}
