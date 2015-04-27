package org.foree.tellmenow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by foree on 15-4-27.
 * receiver boot action to start application
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BootBroadcastReceiver";
    private static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action_boot)) {
            Intent phoneService = new Intent(context, PhoneListenerService.class);
            context.startService(phoneService);
            Log.v(TAG, "start service");
            //Toast.makeText(context, "start service", Toast.LENGTH_LONG).show();
        }

    }
}
