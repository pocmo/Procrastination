package com.androidzeitgeist.procrastination.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.database.TaskAccessHelper;
import com.androidzeitgeist.procrastination.database.TasksColumns;
import com.androidzeitgeist.procrastination.database.TasksContentProvider;

public class EditTaskDialogFragment extends DialogFragment implements OnClickListener {
    private static final String ARGUMENT_TASK_ID = "task_id";
    private static final String ARGUMENT_TASK_TITLE = "task_title";

    private EditText titleView;

    public static EditTaskDialogFragment newInstance(long taskId) {
        EditTaskDialogFragment fragment = new EditTaskDialogFragment();

        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_TASK_ID, taskId);
        fragment.setArguments(arguments);

        return fragment;
    }

    private long getTaskId() {
        return getArguments().getLong(ARGUMENT_TASK_ID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.edit_task_dialog_title);
        builder.setPositiveButton(R.string.dialog_button_ok, this);
        builder.setNegativeButton(R.string.dialog_button_cancel, null);

        titleView = new EditText(getActivity());
        builder.setView(titleView);

        if (savedInstanceState != null) {
            titleView.setText(savedInstanceState.getString(ARGUMENT_TASK_TITLE));
        } else {
            titleView.setText(TaskAccessHelper.getTitle(getActivity(), getTaskId()));
        }

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        savedState.putString(ARGUMENT_TASK_TITLE, titleView.getText().toString());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String title = titleView.getText().toString();

        ContentValues values = new ContentValues();
        values.put(TasksColumns.TITLE, title);

        getActivity().getContentResolver().update(
            TasksContentProvider.TASKS_URI,
            values,
            TasksColumns._ID + " = ?",
            new String[] { String.valueOf(getTaskId()) }
        );

        dismiss();
    }
}
