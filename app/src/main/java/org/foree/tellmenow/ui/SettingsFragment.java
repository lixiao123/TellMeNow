package org.foree.tellmenow.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.foree.tellmenow.R;

/**
 * Created by foree on 15-4-27.
 */
public class SettingsFragment extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefrence_settings);
    }
}