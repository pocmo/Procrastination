package com.androidzeitgeist.procrastination.helper;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class ApplicationHelper {
    public static String getVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                context.getPackageName(), 0
            ).versionName;
        } catch (NameNotFoundException e) {
            throw new AssertionError("Should not happen: Can't get package info about myself");
        }
    }
}
