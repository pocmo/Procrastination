package com.androidzeitgeist.procrastination.listener;

import com.androidzeitgeist.procrastination.adapter.DayFragmentPagerAdapter;
import com.androidzeitgeist.procrastination.fragment.DayFragment;

import android.app.ActionBar;
import android.support.v4.view.ViewPager;

public class TabViewPagerListener extends ViewPager.SimpleOnPageChangeListener {
    private ActionBar actionBar;
    private DayFragmentPagerAdapter adapter;
    
    public TabViewPagerListener(ActionBar actionBar, DayFragmentPagerAdapter adapter) {
        this.actionBar = actionBar;
        this.adapter = adapter;
    }
    
    @Override
    public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);
        
        DayFragment fragment = (DayFragment) adapter.getItem(position == 0 ? 1 : 0);
        fragment.onHide();
    }
}
