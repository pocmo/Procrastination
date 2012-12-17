package com.androidzeitgeist.procrastination.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.helper.ApplicationHelper;

public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        TextView versionView = (TextView) view.findViewById(R.id.version);
        versionView.setText(ApplicationHelper.getVersion(getActivity()));

        return view;
    }
}
