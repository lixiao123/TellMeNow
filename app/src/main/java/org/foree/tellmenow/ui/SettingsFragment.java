package org.foree.tellmenow.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import org.foree.tellmenow.PhoneListenerService;
import org.foree.tellmenow.R;
import org.foree.tellmenow.base.MyApplication;

/**
 * Created by foree on 15-4-27.
 */
public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化信息
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        final Intent lisentenerService = new Intent(getActivity(), PhoneListenerService.class);

        addPreferencesFromResource(R.xml.prefrence_settings);

        final SwitchPreference switchPreference = (SwitchPreference) findPreference(SettingsActivity.SWITCH_KEY);
        final ListPreference listPreference = (ListPreference) findPreference(SettingsActivity.DELAY_KEY);
        final EditTextPreference editTextPreference = (EditTextPreference) findPreference(SettingsActivity.TARGET_NUMBER_KEY);
        final EditTextPreference user_define_notify = (EditTextPreference) findPreference(SettingsActivity.USER_DEFINE_NOTIFY_KEY);

        findPreference(SettingsActivity.ABOUT_KEY).setTitle(MyApplication.myVersionName);

        //开关功能的设置
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(newValue.equals(true)){
                    getActivity().startService(lisentenerService);
                    Toast.makeText(getActivity(), R.string.function_on_notify, Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "start service");
                }else{
                    getActivity().stopService(lisentenerService);
                    Toast.makeText(getActivity(), R.string.function_off_notify, Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "stop service");
                }
                return true;
            }
        });

        //延迟发送的设置
        listPreference.setSummary(listPreference.getEntry());
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                listPreference.setSummary(newValue + "s");
                return true;
            }
        });

        //发送号码的设置
        editTextPreference.setSummary(editTextPreference.getText());
        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                editTextPreference.setSummary(newValue + "");
                return true;
            }
        });

        //自定义短信小尾巴
        user_define_notify.setSummary(user_define_notify.getText());
        user_define_notify.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                user_define_notify.setSummary(newValue + "");
                return true;
            }
        });
    }
}
