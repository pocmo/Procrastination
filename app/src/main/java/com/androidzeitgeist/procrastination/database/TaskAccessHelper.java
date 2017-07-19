package com.androidzeitgeist.procrastination.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.androidzeitgeist.procrastination.model.Task;

public abstract class TaskAccessHelper {
    public static List<Task> getNotificationTasks(Context context) {
        List<Task> tasks = new ArrayList<Task>();

        SQLiteDatabase database = new DatabaseHelper(context).getReadableDatabase();

        Cursor cursor = database.query(
            TasksColumns.TABLE_NAME,
            TasksColumns._ALL,
            createOpenTasksSelection(),
            createTodaySelectionArguments(),
            null,
            null,
            TasksColumns.CREATED_AT + " DESC"
        );

        try {
            while (cursor.moveToNext()) {
                tasks.add(new Task(cursor));
            }
        } finally {
            cursor.close();
        }

        return tasks;
    }

    public static String createOpenTasksSelection() {
        return createTodaySelection() + " AND " + TasksColumns.DONE_AT + " = 0";
    }

    public static String createTodaySelection() {
        return TasksColumns.DUE_AT + " <= ? AND "
            + "(" + TasksColumns.DONE_AT + " >= ? OR " + TasksColumns.DONE_AT + " == 0)";
    }

    public static String[] createTodaySelectionArguments() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayStart = calendar.getTime().getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long todayEnd = calendar.getTime().getTime();

        return new String[] { String.valueOf(todayEnd), String.valueOf(todayStart) };
    }

    public static String[] createTomorrowSelectionArguments() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long todayEnd = calendar.getTime().getTime();

        return new String[] { String.valueOf(todayEnd) };
    }

    public static String getTitle(Context context, long taskId) {
        SQLiteDatabase database = new DatabaseHelper(context).getReadableDatabase();

        Cursor cursor = database.query(
            TasksColumns.TABLE_NAME,
            TasksColumns._ALL,
            TasksColumns._ID + " = ?",
            new String[] { String.valueOf(taskId) },
            null,
            null,
            null
        );

        cursor.moveToFirst();
        Task task = new Task(cursor);
        cursor.close();

        return task.getTitle();
    }

    public static void doTomorrow(Context context, long taskId) {
        long dayMs = 1000 * 60 * 60 * 24;
        long today = System.currentTimeMillis();
        long tomorrow = today + dayMs;

        doAt(context, taskId, tomorrow);
    }

    public static void doToday(Context context, long taskId) {
        long today = System.currentTimeMillis();

        doAt(context, taskId, today);
    }

    private static void doAt(Context context, long taskId, long dueAt) {
        ContentValues values = new ContentValues();
        values.put(TasksColumns.DUE_AT, dueAt);

        String selection = TasksColumns._ID + " = ?";
        String[] selectionArgs = new String[] { String.valueOf(taskId) };

        context.getContentResolver().update(
            TasksContentProvider.TASKS_URI,
            values,
            selection,
            selectionArgs
        );
    }

    public static void markAsDone(Context context, long taskId, boolean isDone) {
        ContentValues values = new ContentValues();

        values.put(TasksColumns.DONE_AT, isDone ? System.currentTimeMillis() : 0);

        String selection = TasksColumns._ID + " = ?";
        String[] selectionArgs = new String[] { String.valueOf(taskId) };

        context.getContentResolver().update(
            TasksContentProvider.createTaskUri(taskId),
            values,
            selection,
            selectionArgs
        );
    }
}
