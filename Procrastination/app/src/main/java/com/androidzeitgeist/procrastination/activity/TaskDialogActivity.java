package com.androidzeitgeist.procrastination.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.database.TaskAccessHelper;
import com.androidzeitgeist.procrastination.helper.Extra;

public class TaskDialogActivity extends Activity {
    private long taskId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskId = getIntent().getExtras().getLong(Extra.TASK_ID);

        setTitle(TaskAccessHelper.getTitle(this, taskId));

        setContentView(R.layout.dialog_task);
    }

    public void markTaskAsDone(View view) {
        TaskAccessHelper.markAsDone(this, taskId, true);

        finish();
    }

    public void moveTaskToTomorrow(View view) {
        TaskAccessHelper.doTomorrow(this, taskId);

        finish();
    }
}
