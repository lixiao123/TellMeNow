package org.foree.tellmenow;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by foree on 15-4-27.
 * get phone state
 */
public class PhoneListenerService extends Service{
    private static final String TAG = "PhoneListenerService";
    Timer timer;
    TimerTask task;

    //需要查询的phone表字段
    private static final String[] PHONE_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
    };
    Cursor phoneCursor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //get Phone Service
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);

        //get contacts info
        ContentResolver resolver = getContentResolver();
        phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PHONE_PROJECTION, null, null, null);


        //get a timer
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                //send a sms;
                Log.v(TAG, "send message");
            }
        };
    }

    //监听电话的状态
    private class MyPhoneStateListener extends PhoneStateListener{
        private static final String TAG = "MyPhoneStateListener";
        private static final int PHONE_CONTACT_NAME_INDEX = 0;
        private static final int PHONE_CONTACT_NUMBER_INDEX = 1;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.v(TAG, "idle");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.v(TAG, "offHook: " + incomingNumber);
                    timer.cancel();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.v(TAG, "ringing: " + incomingNumber);
                    //get contact name by incomingNumber
                    if( phoneCursor != null){
                        while(phoneCursor.moveToNext()){
                            if(phoneCursor.getString(PHONE_CONTACT_NUMBER_INDEX).equals(incomingNumber)){
                                Log.v(TAG, "call from " + phoneCursor.getString(PHONE_CONTACT_NAME_INDEX));
                            }
                        }
                    }
                    //15秒之后未接听，发送短信
                    timer.schedule(task, 15000);
            }
        }
    }
}
