package com.androidzeitgeist.procrastination.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class TaskWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TaskRemoteViewsFactory(getApplicationContext(), intent);
    }
}
