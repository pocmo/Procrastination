package com.androidzeitgeist.procrastination.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.adapter.DayFragmentPagerAdapter;
import com.androidzeitgeist.procrastination.listener.TabViewPagerListener;
import com.androidzeitgeist.procrastination.listener.ViewPagerTabListener;

public class TasksActivity extends FragmentActivity {
    private DayFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        adapter = new DayFragmentPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabViewPagerListener(actionBar, adapter));

        ViewPagerTabListener listener = new ViewPagerTabListener(viewPager);

        actionBar.addTab(
            actionBar.newTab().setText(R.string.today).setTabListener(listener)
        );

        actionBar.addTab(
            actionBar.newTab().setText(R.string.tomorrow).setTabListener(listener)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tasks, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.completed_tasks) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
