package com.androidzeitgeist.procrastination.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class SettingsHelper {
    public static final String KEY_NOTIFICATION = "show_notification";
    public static final String KEY_ONGOING_NOTIFICATION = "ongoing_notification";

    public static boolean isNotificationEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_NOTIFICATION, false);
    }

    public static boolean isOngoingNotification(Context context) {
        return getPreferences(context).getBoolean(KEY_ONGOING_NOTIFICATION, false);
    }

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
