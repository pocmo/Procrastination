package com.androidzeitgeist.procrastination.helper;

import android.widget.SectionIndexer;

public class DateIndexer implements SectionIndexer {
    @Override
    public int getPositionForSection(int section) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
