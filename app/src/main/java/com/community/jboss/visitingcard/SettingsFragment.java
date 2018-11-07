package com.community.jboss.visitingcard;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import com.community.jboss.visitingcard.VisitingCard.VisitingCardActivity;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);

        final Activity activity = getActivity();
        if (activity != null) {
            CheckBoxPreference themeToggle = (CheckBoxPreference) findPreference(getString(R.string.PREF_DARK_THEME));
            themeToggle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    activity.startActivity(new Intent(activity, VisitingCardActivity.class));
                    activity.finish();
                    return true;
                }
            });
        }

    }
}
