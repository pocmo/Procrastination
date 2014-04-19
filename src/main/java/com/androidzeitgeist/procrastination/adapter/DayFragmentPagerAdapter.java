package com.androidzeitgeist.procrastination.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.androidzeitgeist.procrastination.fragment.DayFragment;

public class DayFragmentPagerAdapter extends FragmentPagerAdapter {
    private DayFragment todayFragment;
    private DayFragment tomorrowFragment;

    public DayFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

        todayFragment = DayFragment.newInstanceForToday();
        tomorrowFragment = DayFragment.newInstanceForTomorrow();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return todayFragment;
        } else {
            return tomorrowFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
