package com.androidzeitgeist.procrastination.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.database.TasksColumns;
import com.androidzeitgeist.procrastination.database.TasksContentProvider;

public class HistoryActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.clear_tasks) {
            clearTasks();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearTasks() {
        getContentResolver().delete(
            TasksContentProvider.TASKS_URI,
            TasksColumns.DONE_AT + " > 0",
            null
        );

        Toast.makeText(this, R.string.toast_tasks_cleared, Toast.LENGTH_SHORT).show();

        finish();
    }
}
