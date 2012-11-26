package com.androidzeitgeist.procrastination.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.androidzeitgeist.procrastination.helper.AlarmHelper;
import com.androidzeitgeist.procrastination.helper.NotificationHelper;

public class NotificationAlarmService extends IntentService {
    private static final String TAG = "Procrastination/NotificationAlarmService";

    private static WakeLock wakeLock;

    public NotificationAlarmService() {
        super(TAG);
    }

    public static void setWakeLock(WakeLock wakeLock) {
        NotificationAlarmService.wakeLock = wakeLock;
    }

    public static void releaseWakeLock() {
        NotificationAlarmService.wakeLock.release();
        NotificationAlarmService.wakeLock = null;

        Log.d(TAG, "Released wake lock");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            NotificationHelper.updateNotification(getApplicationContext());
            AlarmHelper.updateAlarm(getApplicationContext());
        } finally {
            NotificationAlarmService.releaseWakeLock();
        }
    }
}
