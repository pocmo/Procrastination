package com.androidzeitgeist.procrastination.database;

import android.provider.BaseColumns;

public interface TasksColumns extends BaseColumns {
    public static final String TABLE_NAME = "tasks";

    public static final String TITLE        = "title";
    public static final String DESCRIPTION  = "description";
    public static final String DUE_AT       = "due_at";
    public static final String DONE_AT      = "done_at";
    public static final String CREATED_AT   = "created_at";

    public static final String[] _ALL = {
        _ID,
        TITLE,
        DESCRIPTION,
        DUE_AT,
        DONE_AT,
        CREATED_AT
    };
}
