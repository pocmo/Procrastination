package com.androidzeitgeist.procrastination.helper;

import java.util.List;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.activity.StartupActivity;
import com.androidzeitgeist.procrastination.database.TaskAccessHelper;
import com.androidzeitgeist.procrastination.model.Task;

public abstract class NotificationHelper {
    private static final String TAG = "Procrastination/NotificationHelper";

    private static final int NOTIFICATION_ID = 1;

    private static final int NOTIFICATION_REQUEST_CODE = 1;

    public static void updateNotification(Context context) {
        if (SettingsHelper.isNotificationEnabled(context)) {
            setupNotification(context);
        } else {
            cancelNotification(context);
        }
    }

    public static void refreshNotification(Context context) {
        if (!SettingsHelper.isOngoingNotification(context)) {
            return;
        }

        setupNotification(context);
    }

    public static void setupNotification(Context context) {
        setupNotification(context, SettingsHelper.isOngoingNotification(context));
    }

    public static void setupNotification(Context context, boolean onGoing) {
        Log.d(TAG, "Setting up notification");

        List<Task> tasks = TaskAccessHelper.getNotificationTasks(context);
        if (tasks.size() == 0) {
            Log.d(TAG, "No tasks to notify about");
            getNotificationManager(context).cancel(NOTIFICATION_ID);
            return;
        }

        Notification notification = buildNotification(context, tasks, onGoing);

        getNotificationManager(context).notify(NOTIFICATION_ID, notification);
    }

    public static void cancelNotification(Context context) {
        Log.d(TAG, "Cancelling notification");

        getNotificationManager(context).cancel(NOTIFICATION_ID);
    }

    private static Notification buildNotification(Context context, List<Task> tasks, boolean onGoing) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        String title =  context.getResources().getQuantityString(R.plurals.notification_title, tasks.size(), tasks.size());

        builder.setNumber(tasks.size());
        builder.setContentTitle(title);
        builder.setContentText(context.getString(R.string.notification_text));

        Intent intent = new Intent(context, StartupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_REQUEST_CODE,
            intent,
            0
        );

        builder.setContentIntent(pendingIntent);

        builder.setOngoing(onGoing);
        builder.setSmallIcon(R.drawable.ic_stat_notification);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (Task task : tasks) {
            inboxStyle.addLine(task.getTitle());
        }
        inboxStyle.setBigContentTitle(title);
        builder.setStyle(inboxStyle);

        String[] motivations = context.getResources().getStringArray(R.array.motivations);
        Random random = new Random();
        int index = random.nextInt(motivations.length);

        builder.setSubText(motivations[index]);

        return builder.build();
    }

    private static NotificationManager getNotificationManager(Context context) {
        return(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
