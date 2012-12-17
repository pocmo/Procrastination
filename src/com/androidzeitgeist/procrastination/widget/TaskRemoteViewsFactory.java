package com.androidzeitgeist.procrastination.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.activity.AboutActivity;
import com.androidzeitgeist.procrastination.activity.TaskDialogActivity;
import com.androidzeitgeist.procrastination.database.TaskAccessHelper;
import com.androidzeitgeist.procrastination.database.TasksColumns;
import com.androidzeitgeist.procrastination.database.TasksContentProvider;
import com.androidzeitgeist.procrastination.helper.Extra;
import com.androidzeitgeist.procrastination.model.Task;

public class TaskRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private Cursor cursor;

    public TaskRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        cursor = context.getContentResolver().query(
            TasksContentProvider.TASKS_URI,
            TasksColumns._ALL,
            TaskAccessHelper.createTodaySelection() + " AND done_at = 0",
            TaskAccessHelper.createTodaySelectionArguments(),
            TasksColumns.CREATED_AT + " DESC"
        );
        cursor.registerContentObserver(new ContentObserver(null) {
            @SuppressLint("NewApi")
            @Override
            public void onChange(boolean selfChange) {
                onChange(selfChange, null);
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                AppWidgetManager manager = AppWidgetManager.getInstance(context);

                ComponentName component = new ComponentName(context, TaskWidgetProvider.class);

                manager.notifyAppWidgetViewDataChanged(
                    manager.getAppWidgetIds(component),
                    R.id.tasks
                );
            }
        });
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    public Task getTask(int position) {
        cursor.moveToPosition(position);
        return new Task(cursor);
    }

    @Override
    public long getItemId(int position) {
        return getTask(position).getId();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_item_task);

        Task task = getTask(position);

        Intent intent = new Intent(context.getApplicationContext(), TaskDialogActivity.class);
        intent.putExtra(Extra.TASK_ID, task.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        views.setOnClickFillInIntent(R.id.task, intent);
        views.setTextViewText(R.id.title, task.getTitle());

        return views;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        cursor.requery();
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }
}
