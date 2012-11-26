package com.androidzeitgeist.procrastination.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.database.TasksColumns;
import com.androidzeitgeist.procrastination.model.Task;

public class TaskCursorAdapter extends CursorAdapter {
    private Context context;

    private int defaultFlags = 0;
    
    private List<Long> selectedTasks;

    public TaskCursorAdapter(Context context) {
        super(context, null, 0);

        this.context = context;
        this.selectedTasks = new ArrayList<Long>();
    }

    public Task getTaskAtPosition(int position) {
        return new Task((Cursor) getItem(position));
    }
    
    public void toggleSelection(int position) {
        Long id = getItemId(position);
        
        if (selectedTasks.contains(id)) {
            selectedTasks.remove(id);
        } else {
            selectedTasks.add(id);
        }

        notifyDataSetChanged();
    }
    
    public int getSelectedCount() {
        return selectedTasks.size();
    }
    
    public void clearSelections() {
        selectedTasks.clear();
        
        notifyDataSetChanged();
    }
    
    public List<Long> getSelectedIds() {
        return selectedTasks;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final Task task = new Task(cursor);
        
        if (selectedTasks.contains(task.getId())) {
            view.setBackgroundResource(R.drawable.selected_bg);
        } else {
            view.setBackgroundColor(0xffffffff);
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(task.getTitle());

        if (task.isDone()) {
            titleView.setPaintFlags(defaultFlags | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            titleView.setPaintFlags(defaultFlags);
        }

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(task.isDone());
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TaskCursorAdapter.this.onCheckedChanged(task, isChecked);
            }
        });

        view.setAlpha(1f);
        view.setTranslationX(0);
    }

    public void onCheckedChanged(Task task, boolean isChecked) {
        ContentValues values = new ContentValues();

        values.put(TasksColumns.DONE_AT, isChecked ? System.currentTimeMillis() : 0);

        String selection = TasksColumns._ID + " = ?";
        String[] selectionArgs = new String[] { String.valueOf(task.getId()) };

        context.getContentResolver().update(task.getUri(), values, selection, selectionArgs);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, null);

        defaultFlags = ((TextView) view.findViewById(R.id.title)).getPaintFlags();

        bindView(view, context, cursor);

        return view;
    }
}
