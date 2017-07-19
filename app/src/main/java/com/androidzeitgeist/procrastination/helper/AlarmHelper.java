package com.androidzeitgeist.procrastination.helper;

import java.sql.Timestamp;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.androidzeitgeist.procrastination.receiver.BootAndAlarmReceiver;

public abstract class AlarmHelper {
    private static final String TAG = "Procrastination/AlarmHelper";

    private static final int ALARM_REQUEST_CODE = 1;

    public static void updateAlarm(Context context) {
        if (SettingsHelper.isNotificationEnabled(context)) {
            scheduleAlarm(context);
        } else {
            cancelAlarm(context);
        }
    }

    public static void scheduleAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 0);

        long triggetAtMillis = calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY;

        Log.d(TAG, "Scheduled alarm for " + new Timestamp(triggetAtMillis));

        getAlarmManager(context).setRepeating(
            AlarmManager.RTC,
            triggetAtMillis,
            AlarmManager.INTERVAL_DAY,
            createAlarmIntent(context)
        );
    }

    public static void cancelAlarm(Context context) {
        Log.d(TAG, "Cancelled alarm");

        getAlarmManager(context).cancel(createAlarmIntent(context));
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private static PendingIntent createAlarmIntent(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), BootAndAlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context.getApplicationContext(),
            ALARM_REQUEST_CODE,
            intent,
            0
        );

        return pendingIntent;
    }
}
