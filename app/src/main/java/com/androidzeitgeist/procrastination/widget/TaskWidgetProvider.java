package com.androidzeitgeist.procrastination.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.activity.StartupActivity;
import com.androidzeitgeist.procrastination.activity.TaskDialogActivity;

public class TaskWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
        final int N = widgetIds.length;

        for (int i = 0; i < N; i++) {
            int widgetId = widgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_tasks);

            Intent intent = new Intent(context, TaskWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            views.setRemoteAdapter(R.id.tasks, intent);

            PendingIntent taskIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, TaskDialogActivity.class), 0
            );
            views.setPendingIntentTemplate(R.id.tasks, taskIntent);
            views.setEmptyView(R.id.tasks, R.id.no_tasks);

            PendingIntent launchIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, StartupActivity.class), 0
            );
            views.setOnClickPendingIntent(R.id.icon, launchIntent);
            views.setOnClickPendingIntent(R.id.title, launchIntent);

            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }
}
