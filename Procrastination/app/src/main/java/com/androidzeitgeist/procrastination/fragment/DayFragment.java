package com.androidzeitgeist.procrastination.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.adapter.TaskCursorAdapter;
import com.androidzeitgeist.procrastination.database.TaskAccessHelper;
import com.androidzeitgeist.procrastination.database.TasksColumns;
import com.androidzeitgeist.procrastination.database.TasksContentProvider;
import com.androidzeitgeist.procrastination.listener.SwipeDismissListViewTouchListener;
import com.androidzeitgeist.procrastination.listener.SwipeDismissListViewTouchListener.OnDismissCallback;
import com.androidzeitgeist.procrastination.model.Task;

public class DayFragment extends ListFragment implements LoaderCallbacks<Cursor>, OnDismissCallback, ActionMode.Callback {
    private static final int TYPE_TODAY    = 1;
    private static final int TYPE_TOMORROW = 2;

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;

    private static final String ARGUMENT_TYPE = "type";

    private TaskCursorAdapter adapter;
    private SwipeDismissListViewTouchListener dismissListener;
    private EditText taskView;

    private ActionMode actionMode;

    public static DayFragment newInstanceForToday() {
        return newInstance(TYPE_TODAY);
    }

    public static DayFragment newInstanceForTomorrow() {
        return newInstance(TYPE_TOMORROW);
    }

    private static DayFragment newInstance(int type) {
        DayFragment fragment = new DayFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_TYPE, type);
        fragment.setArguments(arguments);

        return fragment;
    }

    public boolean isShowingToday() {
        return getArguments().getInt(ARGUMENT_TYPE) == TYPE_TODAY;
    }

    public boolean isShowingTomorrow() {
        return getArguments().getInt(ARGUMENT_TYPE) == TYPE_TOMORROW;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        adapter = new TaskCursorAdapter(activity);
        setListAdapter(adapter);

        activity.getLoaderManager().initLoader(getArguments().getInt(ARGUMENT_TYPE), null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        taskView = (EditText) view.findViewById(R.id.task);
        taskView.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    addTask(taskView.getText().toString());
                    TextKeyListener.clear(taskView.getText());

                    return true;
                }
                return false;
            }
        });

        View recordView = view.findViewById(R.id.record);
        recordView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });
        recordView.setVisibility(isVoiceRecognitionAvailable() ? View.VISIBLE : View.GONE);

        ListView listView = (ListView) view.findViewById(android.R.id.list);

        dismissListener = new SwipeDismissListViewTouchListener(listView, this);
        dismissListener.setDirection(
            isShowingToday() ? SwipeDismissListViewTouchListener.DIRECTION_RIGHT : SwipeDismissListViewTouchListener.DIRECTION_LEFT
        );

        listView.setOnTouchListener(dismissListener);
        listView.setOnScrollListener(dismissListener.makeScrollListener());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || requestCode != VOICE_RECOGNITION_REQUEST_CODE) {
            return;
        }

        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        if (!matches.isEmpty()) {
            taskView.append(matches.get(0));
        }
    }

    private boolean isVoiceRecognitionAvailable() {
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
            new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), VOICE_RECOGNITION_REQUEST_CODE
        );

        return activities.size() > 0;
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    public void addTask(String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }

        long dueAt = System.currentTimeMillis() + (isShowingTomorrow() ? 1000 * 60 * 60 * 24 : 0);

        ContentValues values = new ContentValues();
        values.put(TasksColumns.TITLE, title);
        values.put(TasksColumns.DUE_AT, dueAt);
        values.put(TasksColumns.CREATED_AT, System.currentTimeMillis());
        values.put(TasksColumns.DONE_AT, 0);

        getActivity().getContentResolver().insert(TasksContentProvider.TASKS_URI, values);
    }

    public void onDismiss(ListView listView, int position) {
        TaskCursorAdapter adapter = (TaskCursorAdapter) getListAdapter();
        Task task = adapter.getTaskAtPosition(position);

        if (isShowingToday()) {
            TaskAccessHelper.doTomorrow(getActivity(), task.getId());
        } else {
            TaskAccessHelper.doToday(getActivity(), task.getId());
        }
    }

    @Override
    public void onLongPress(ListView listView, int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(this);
        }

        onSelected(listView, position);
    }

    public void onHide() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void onSelected(ListView listView, int position) {
        if (actionMode == null) {
            return;
        }

        listView.playSoundEffect(SoundEffectConstants.CLICK);

        adapter.toggleSelection(position);

        int count = adapter.getSelectedCount();

        if (count > 0) {
            actionMode.setTitle(getString(R.string.selected_tasks, count));
            actionMode.invalidate();
        } else {
            actionMode.finish();
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.context_tasks, menu);

        dismissListener.setEnabled(false);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.edit).setVisible(
            adapter.getSelectedCount() <= 1
        );

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete) {
            deleteSelectedTasks();
            return true;
        }

        if (id == R.id.edit) {
            editSelectedTask();
            return true;
        }

        return false;
    }

    private void deleteSelectedTasks() {
        adapter.getSelectedCount();

        List<Long> selectedIds = adapter.getSelectedIds();

        for (long id : selectedIds) {
            getActivity().getContentResolver().delete(
                TasksContentProvider.TASKS_URI,
                TasksColumns._ID + " = ?",
                new String[] { String.valueOf(id) }
            );
        }

        actionMode.finish();
    };

    private void editSelectedTask() {
        DialogFragment fragment = EditTaskDialogFragment.newInstance(
            adapter.getSelectedIds().get(0)
        );
        fragment.show(getFragmentManager(), null);
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        dismissListener.setEnabled(true);

        adapter.clearSelections();
        actionMode = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
            getActivity(),
            TasksContentProvider.TASKS_URI,
            TasksColumns._ALL,
            getQuerySelection(),
            getQuerySelectionArguments(),
            TasksColumns.CREATED_AT + " DESC"
        );
    }

    private String getQuerySelection() {
        if (isShowingToday()) {
            return TaskAccessHelper.createTodaySelection();
        }

        if (isShowingTomorrow()) {
            return TasksColumns.DUE_AT + " > ?";
        }

        throw new IllegalStateException("Fragment is neither showing today nor tomorrow");
    }

    private String[] getQuerySelectionArguments() {
        if (isShowingToday()) {
            return TaskAccessHelper.createTodaySelectionArguments();
        }

        if (isShowingTomorrow()) {
            return TaskAccessHelper.createTomorrowSelectionArguments();
        }

        throw new IllegalStateException("Fragment is neither showing today nor tomorrow");
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
