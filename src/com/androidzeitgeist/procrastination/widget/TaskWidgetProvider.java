package com.androidzeitgeist.procrastination.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.androidzeitgeist.procrastination.R;

public class TaskWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
        final int N = widgetIds.length;

        for (int i = 0; i < N; i++) {
            int widgetId = widgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_tasks);
            views.setRemoteAdapter(R.id.tasks, new Intent(context, TaskWidgetService.class));

            Intent intent = new Intent(context, TaskWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            views.setRemoteAdapter(R.id.tasks, intent);

            views.setEmptyView(R.id.tasks, R.id.no_tasks);

            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }
}
