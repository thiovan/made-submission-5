package thiovan.submission5.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import thiovan.submission5.R;
import thiovan.submission5.receivers.AlarmReceiver;

public class PreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String RELEASE_REMINDER = "release_reminder";
    private final String DAILY_REMINDER = "daily_reminder";

    private Context mContext;
    private SwitchPreference mReleaseReminder, mDailyReminder;
    private AlarmReceiver alarmReceiver;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);

        alarmReceiver = new AlarmReceiver();

        initView();
        setValues();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initView() {
        mReleaseReminder = findPreference(RELEASE_REMINDER);
        mDailyReminder = findPreference(DAILY_REMINDER);
    }

    private void setValues() {
        if (alarmReceiver.isAlarmSet(mContext, AlarmReceiver.TYPE_RELEASE_REMINDER)) {
            mReleaseReminder.setChecked(true);
        } else {
            mReleaseReminder.setChecked(false);
        }

        if (alarmReceiver.isAlarmSet(mContext, AlarmReceiver.TYPE_DAILY_REMINDER)) {
            mDailyReminder.setChecked(true);
        } else {
            mDailyReminder.setChecked(false);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(RELEASE_REMINDER)) {
            if (sharedPreferences.getBoolean(RELEASE_REMINDER, false)) {
                alarmReceiver.setRepeatingAlarm(
                        mContext,
                        "Release Reminder",
                        "08:00",
                        "Release Reminder",
                        AlarmReceiver.TYPE_RELEASE_REMINDER);
                mReleaseReminder.setChecked(true);
            } else {
                alarmReceiver.cancelAlarm(mContext, AlarmReceiver.TYPE_RELEASE_REMINDER);
                mReleaseReminder.setChecked(false);
            }
        }

        if (key.equals(DAILY_REMINDER)) {
            if (sharedPreferences.getBoolean(DAILY_REMINDER, false)) {
                alarmReceiver.setRepeatingAlarm(
                        mContext,
                        getResources().getString(R.string.app_name),
                        "07:00",
                        getResources().getString(R.string.app_name) + getResources().getString(R.string.missing_you),
                        AlarmReceiver.TYPE_DAILY_REMINDER);
                mReleaseReminder.setChecked(true);
            } else {
                alarmReceiver.cancelAlarm(mContext, AlarmReceiver.TYPE_DAILY_REMINDER);
                mDailyReminder.setChecked(false);
            }
        }
    }
}
