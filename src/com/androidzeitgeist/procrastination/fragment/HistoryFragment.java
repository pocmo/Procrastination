package com.androidzeitgeist.procrastination.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.adapter.HistoryCursorAdapter;
import com.androidzeitgeist.procrastination.database.TasksColumns;
import com.androidzeitgeist.procrastination.database.TasksContentProvider;

public class HistoryFragment extends ListFragment implements LoaderCallbacks<Cursor> {
    private static final int LOADER_HISTORY = 1;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        
        return view;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        setListAdapter(new HistoryCursorAdapter(activity));
        
        activity.getLoaderManager().initLoader(LOADER_HISTORY, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
            getActivity(),
            TasksContentProvider.TASKS_URI,
            TasksColumns._ALL,
            TasksColumns.DONE_AT + " > 0",
            null,
            TasksColumns.DONE_AT + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ((CursorAdapter) getListAdapter()).swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((CursorAdapter) getListAdapter()).swapCursor(null);
    }
}
