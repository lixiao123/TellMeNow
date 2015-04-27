package org.foree.tellmenow.ui;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.foree.tellmenow.PhoneListenerService;
import org.foree.tellmenow.R;
import org.foree.tellmenow.base.MyApplication;
import org.foree.tellmenow.db.MyDao;

import java.util.List;
import java.util.Map;

/**
 * 1.来电话15秒未接听发送短信
 * 2.15之前接听取消发送短信
 * 3.可以选择监听时间 5-20s
 * 4.读取通讯录来查看什么人打来的电话
 * 6.可以设置要传送的号码
 * 7.显示已经发送的漏接电话的短信数目
 * 8.开机自启动(每隔300秒检测service是否存在)
 */
public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    MyApplication myApplication;
    //所有漏接电话信息
    public List<Map<String, String>> tmnInfos;

    MyDao myDao;
    ListView listView;
    //adapter数据控制器
    public SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 如果开机自启动被屏蔽，则打开app时启动service
         * 并且如果关闭监控功能，则不启动服务
         */
        if( PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.SWITCH_KEY, true)) {
            Intent phoneService = new Intent(this, PhoneListenerService.class);
            startService(phoneService);
        }

        /**
         * 初始化应用程序信息
         */
        myApplication = new MyApplication(this);
        myApplication.initEnv();

        myDao = new MyDao(this);


        listView = (ListView) findViewById(R.id.lv_listener_info);

        Log.v(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        tmnInfos = myDao.getAllInfo();

        //将传来的数据显示到listview中，使用title_list_layout布局
        adapter = new SimpleAdapter(this,
                tmnInfos,
                R.layout.list_info,
                new String[]{"name", "number", "date"},
                new int[]{R.id.name, R.id.number, R.id.date});

        listView.setAdapter(adapter);
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
