package com.androidzeitgeist.procrastination.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.model.Task;

public class HistoryCursorAdapter extends CursorAdapter {
    public HistoryCursorAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Task task = new Task(cursor);

        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(task.getTitle());
        
        int position = cursor.getPosition();
        
        TextView headerView = (TextView) view.findViewById(R.id.header);

        if (shouldShowHeader(position)) {
            headerView.setText(getHeader(position));
            headerView.setVisibility(View.VISIBLE);
        } else {
            headerView.setVisibility(View.GONE);
        }
        
        cursor.moveToPosition(position);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);

        bindView(view, context, cursor);

        return view;
    }

    public boolean shouldShowHeader(int position) {
        if (position == 0) {
            return true;
        } else {
            return !getHeader(position).equals(getHeader(position - 1));
        }
    }

    public String getHeader(int position) {
        Cursor cursor = (Cursor) getItem(position);
        Task task = new Task(cursor);
        return task.getHistoryHeader();
    }
}
