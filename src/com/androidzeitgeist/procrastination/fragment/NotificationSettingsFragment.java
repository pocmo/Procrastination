package com.androidzeitgeist.procrastination.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.helper.AlarmHelper;
import com.androidzeitgeist.procrastination.helper.NotificationHelper;
import com.androidzeitgeist.procrastination.helper.SettingsHelper;

public class NotificationSettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_notification);

        findPreference(SettingsHelper.KEY_NOTIFICATION).setOnPreferenceChangeListener(this);
        findPreference(SettingsHelper.KEY_ONGOING_NOTIFICATION).setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(SettingsHelper.KEY_NOTIFICATION)) {
            if ((Boolean) newValue) {
                NotificationHelper.setupNotification(getActivity());
                AlarmHelper.scheduleAlarm(getActivity());
            } else {
                NotificationHelper.cancelNotification(getActivity());
                AlarmHelper.cancelAlarm(getActivity());
            }
        }

        if (preference.getKey().equals(SettingsHelper.KEY_ONGOING_NOTIFICATION)) {
            NotificationHelper.setupNotification(getActivity(), (Boolean) newValue);
        }

        return true;
    }
}
