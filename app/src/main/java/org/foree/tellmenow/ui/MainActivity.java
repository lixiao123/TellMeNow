package org.foree.tellmenow.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.foree.tellmenow.PhoneListenerService;
import org.foree.tellmenow.R;
import org.xml.sax.InputSource;

/**
 * 1.来电话15秒未接听发送短信
 * 2.15之前接听取消发送短信
 * 3.可以选择监听时间 5-20s
 * 4.读取通讯录来查看什么人打来的电话
 * 5.实现归属地显示（本地数据库）
 * 6.可以设置要传送的号码
 * 7.显示已经发送的漏接电话的短信数目
 * 8.开机自启动(每隔300秒检测service是否存在)
 */
public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //如果开机自启动被屏蔽，则打开app时启动service
        Intent phoneService = new Intent(this, PhoneListenerService.class);
        startService(phoneService);

        Log.v(TAG, "onCreate");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent open_settings = new Intent(this, SettingsActivity.class);
            startActivity(open_settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
