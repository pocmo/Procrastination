package com.androidzeitgeist.procrastination.billing;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Addons {
    private static final String PREFERENCE_NAME = "addons";

    private static final String SETTING_WIDGET_ENABLED = "widget_enabled";

    public static boolean isWidgetEnabled(Context context) {
        context = context.getApplicationContext();

        SharedPreferences preferences = context.getSharedPreferences(
            PREFERENCE_NAME, Context.MODE_PRIVATE
        );

        return preferences.getBoolean(SETTING_WIDGET_ENABLED, false);
    }

    public static void enableWidget(Context context) {
        context = context.getApplicationContext();

        SharedPreferences preferences = context.getSharedPreferences(
            PREFERENCE_NAME, Context.MODE_PRIVATE
        );

        Editor editor = preferences.edit();
        editor.putBoolean(SETTING_WIDGET_ENABLED, true);
        editor.commit();
    }
}
