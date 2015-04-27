package org.foree.tellmenow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import org.foree.tellmenow.ui.SettingsActivity;

/**
 * Created by foree on 15-4-27.
 * receiver boot action to start application
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BootBroadcastReceiver";
    private static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        //如果系统监控功能开启，默认开机自启动
       if( PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.SWITCH_KEY, true)) {
           if (intent.getAction().equals(action_boot)) {
               Intent phoneService = new Intent(context, PhoneListenerService.class);
               context.startService(phoneService);
               Log.v(TAG, "start service");
               //Toast.makeText(context, "start service", Toast.LENGTH_LONG).show();
           }
       }
    }
}
