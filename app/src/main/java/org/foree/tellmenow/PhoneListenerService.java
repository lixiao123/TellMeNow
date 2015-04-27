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

/**
 * Created by foree on 15-4-27.
 * get phone state
 */
public class PhoneListenerService extends Service{
    private static final String TAG = "PhoneListenerService";

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


    }

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
            }
        }
    }
}
