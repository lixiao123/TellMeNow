package org.foree.tellmenow.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.foree.tellmenow.R;
import org.foree.tellmenow.base.MyApplication;

/**
 * Created by foree on 15-4-27.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化信息
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        addPreferencesFromResource(R.xml.prefrence_settings);

        final ListPreference listPreference = (ListPreference) findPreference(SettingsActivity.DELAY_KEY);
        final EditTextPreference editTextPreference = (EditTextPreference) findPreference(SettingsActivity.TARGET_NUMBER_KEY);


        findPreference(SettingsActivity.ABOUT_KEY).setTitle(MyApplication.myVersionName);

        listPreference.setSummary(listPreference.getEntry());
        editTextPreference.setSummary(editTextPreference.getText());

        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                listPreference.setSummary(newValue + "s");
                return true;
            }
        });
        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                editTextPreference.setSummary(newValue + "");
                return true;
            }
        });


    }
}
