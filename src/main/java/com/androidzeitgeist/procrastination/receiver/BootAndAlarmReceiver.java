package com.androidzeitgeist.procrastination.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.androidzeitgeist.procrastination.service.NotificationAlarmService;

public class BootAndAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "Procrastination/BootAndAlarmReceiver";

    private static final String WAKE_LOCK_NAME = "com.androidzeitgeist.procrastination.wakelock";

    @SuppressLint("Wakelock")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            String path = intent.getData().toString();
            if (!path.endsWith(context.getPackageName())) {
                return;
            }
        }

        Log.d(TAG, "Acquiring wake lock");

        WakeLock wakeLock = getPowerManager(context).newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            WAKE_LOCK_NAME
        );

        wakeLock.acquire();

        NotificationAlarmService.setWakeLock(wakeLock);

        Intent serviceIntent = new Intent(context, NotificationAlarmService.class);
        context.startService(serviceIntent);
    }

    private PowerManager getPowerManager(Context context) {
        return (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }
}
