package org.foree.tellmenow.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;

import org.foree.tellmenow.R;

/**
 * Created by foree on 15-4-27.
 */
public class SettingsActivity extends ActionBarActivity{

    //preference Key
    public static final String SWITCH_KEY = "st_function";
    public static final String USER_DEFINE_NOTIFY_KEY = "ep_define_notify";
    public static final String DELAY_KEY = "lp_delay";
    public static final String ABOUT_KEY = "pf_about";
    public static final String TARGET_NUMBER_KEY = "ep_targetNumber";
    public static final String INSERT_SYSTEM_DB_KEY = "cb_insert_system_db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_settings, new SettingsFragment())
                .commit();
    }
}
