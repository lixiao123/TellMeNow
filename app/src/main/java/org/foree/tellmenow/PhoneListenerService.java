package org.foree.tellmenow;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.foree.tellmenow.db.MyDao;
import org.foree.tellmenow.ui.SettingsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by foree on 15-4-27.
 * get phone state
 */
public class PhoneListenerService extends Service {
    private static final String TAG = "PhoneListenerService";
    SmsManager smsManager;
    SharedPreferences sp;
    ContentResolver resolver;
    MyDao myDao;

    //需要查询的phone表字段
    private static final String[] PHONE_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
    };

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

        //get MyDao
        myDao = new MyDao(this);

        //获取sharePreference对象
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        //初始化switch开关
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SettingsActivity.SWITCH_KEY, true);
        editor.putString(SettingsActivity.DELAY_KEY, "10");
        editor.apply();

    }

    //监听电话的状态
    private class MyPhoneStateListener extends PhoneStateListener {
        private static final String TAG = "MyPhoneStateListener";

        private static final int PHONE_CONTACT_NAME_INDEX = 0;
        private static final int PHONE_CONTACT_NUMBER_INDEX = 1;

        //send message using targetContent to targetPhoneNumber
        String targetContent;
        String addContent ;
        String targetPhoneNumber;
        String callerName;
        int delay_time;
        Cursor phoneCursor;
        Timer timer;
        TimerTask task;

        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if (sp.getBoolean(SettingsActivity.SWITCH_KEY, true)) {
                timer = new Timer();
                addContent = sp.getString(SettingsActivity.USER_DEFINE_NOTIFY_KEY,"");
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

                        //获取来电人的联系人信息
                        callerName = getCallerInfo(incomingNumber);
                        targetContent = addContent + "\n"
                                + "来电人: " + callerName + "\n"
                                + "来电号码: " + incomingNumber;

                        //接收监听信息的电话号码
                        targetPhoneNumber = sp.getString(SettingsActivity.TARGET_NUMBER_KEY, getResources().getString(R.string.targetNumber_default));
                        //15秒之后未接听，发送短信
                        delay_time = Integer.parseInt(sp.getString(SettingsActivity.DELAY_KEY, getResources().getString(R.string.delay_default)));
                        Log.i(TAG, delay_time + "s");
                        //get a timer

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
                                if (sp.getBoolean(SettingsActivity.INSERT_SYSTEM_DB_KEY, false)) {
                                    //是否插入系统信箱
                                    insertSystemSmsDb();
                                }
                                insertDb(incomingNumber);
                            }
                        };
                        timer.schedule(task, delay_time * 1000);
                }
            }
        }

        /**
         * 获取来电人的信息
         */
        private String getCallerInfo(String incomingNumber) {
            //contact info from system contacts

            String phoneContactName = getResources().getString(R.string.phone_Number_Name);

            //get contact name by incomingNumber
            phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PHONE_PROJECTION, null, null, null);
            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    if (phoneCursor.getString(MyPhoneStateListener.PHONE_CONTACT_NUMBER_INDEX).equals(incomingNumber)) {
                        //如果未存储联系人，则显示默认
                        if (!phoneCursor.getString(MyPhoneStateListener.PHONE_CONTACT_NAME_INDEX).isEmpty())
                            phoneContactName = phoneCursor.getString(MyPhoneStateListener.PHONE_CONTACT_NAME_INDEX);
                        Log.v(TAG, "call from " + phoneContactName);
                        phoneCursor.close();
                        break;
                    }
                }

            }
            return phoneContactName;
        }

        /**
         * 将发送的短信插入系统信箱
         */
        private void insertSystemSmsDb() {
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
            getContentResolver().insert(Uri.parse("content://sms"), values);
            Log.v(TAG, "send message");
        }

        /**
         * 将记录插入到数据库
         */
        private void insertDb(String number){
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date());
            myDao.add(callerName, number, currentDate);
        }
    }
}
