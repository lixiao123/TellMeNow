package org.foree.tellmenow;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by foree on 15-4-27.
 * get phone state
 */
public class PhoneListenerService extends Service {
    private static final String TAG = "PhoneListenerService";
    //contact info from system contacts
    String phoneContactName;
    String phoneContactNumber;
    //send message using targetContent to targetPhoneNumber
    String targetContent;
    String targetPhoneNumber;

    ContentResolver resolver;
    SmsManager smsManager;
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
        resolver = getContentResolver();

        //get smsManger
        smsManager = SmsManager.getDefault();
        //初始化信息
        phoneContactName = getResources().getString(R.string.phone_Number_Name);
        targetPhoneNumber = "13676090644";
        targetContent = phoneContactName + " " + phoneContactNumber;

        //get a timer
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                //send a sms;
                if (targetContent.length() > 70) {
                    List<String> divideSms = smsManager.divideMessage(targetContent);
                    for (String sms : divideSms) {
                        smsManager.sendTextMessage(targetPhoneNumber, null, sms, null, null);
                    }
                } else {
                    smsManager.sendTextMessage(targetPhoneNumber, null, targetContent, null, null);
                }

                /**将发送的短信插入数据库**/
                ContentValues values = new ContentValues();
                //发送时间
                values.put("date", System.currentTimeMillis());
                //阅读状态
                values.put("read", 0);
                //1为收 2为发
                values.put("type", 2);
                //送达号码
                values.put("address", targetPhoneNumber);
                //送达内容
                values.put("body", targetContent);
                //插入短信库
                getContentResolver().insert(Uri.parse("content://sms"),values);
                Log.v(TAG, "send message");
            }
        };
    }

    //监听电话的状态
    private class MyPhoneStateListener extends PhoneStateListener {
        private static final String TAG = "MyPhoneStateListener";
        private static final int PHONE_CONTACT_NAME_INDEX = 0;
        private static final int PHONE_CONTACT_NUMBER_INDEX = 1;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
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
                    phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            PHONE_PROJECTION, null, null, null);
                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            if (phoneCursor.getString(PHONE_CONTACT_NUMBER_INDEX).equals(incomingNumber)) {
                                phoneContactNumber = incomingNumber;
                                //如果未存储联系人，则显示默认
                                if (!phoneCursor.getString(PHONE_CONTACT_NAME_INDEX).isEmpty())
                                    phoneContactName = phoneCursor.getString(PHONE_CONTACT_NAME_INDEX);
                                Log.v(TAG, "call from " + phoneContactName);
                                targetContent = phoneContactName + " " + phoneContactNumber;
                                phoneCursor.close();
                                break;
                            }
                        }
                        //15秒之后未接听，发送短信
                        timer.schedule(task, 5000);
                    }
            }
        }
    }
}
