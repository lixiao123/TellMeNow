package org.foree.tellmenow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by foree on 15-4-27.
 * receiver boot action to start application
 */
public class BootBroadcastReceiver extends BroadcastReceiver{
    private static final String TAG = "BootBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent phoneService = new Intent(context, PhoneListenerService.class);
        context.startService(phoneService);
        Log.v(TAG, "start service");
    }
}
