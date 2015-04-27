package org.foree.tellmenow.base;

/**
 * Created by foree on 15-4-27.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import org.foree.tellmenow.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by foree on 3/7/15.
 * 包含自己app的一些信息：
 * 1）当前网络状况
 * 2）应用程序目录和缓存目录
 * 3)Sdcard路径
 * <p/>
 * 以及启动时需要做的初始化
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private Context mContext;
    public static boolean mFirstRun;

    /**
     * 应用程序信息
     */
    //应用程序名称
    public static String myApplicationName;
    //应用程序包名
    public static String myApplicationPackageName;
    //应用程序版本名称
    public static String myVersionName;
    //应用程序版本序号(应用程序用来判断是否升级的,例如:17)
    public static int myVersionCode;
    //应用程序版本号(开发者自定义,例如:1.7.3
    public static String myApplicationVersion;
    //程序当前运行的最新日期
    public static String myDate;

    public MyApplication(Context context) {
        mContext = context;
    }

    //初始化环境(loading...时候使用)
    public void initEnv() {

        //初始化log打印级别,默认为VERBOSE
        //LogUtils.logLevel = Log.INFO;

        //初始化应用程序名
        myApplicationName = mContext.getString(R.string.app_name);
        //初始化应用程序的包名
        myApplicationPackageName = mContext.getPackageName();
        //初始化当前日期
        myDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date());
        // Log.v(TAG, myDate);

        //获取当前应用程序的版本号和版本名称
        initApplicationVersionInfo(mContext);

    }

    //获取应用程序的版本信息
    public void initApplicationVersionInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            //获取当前包的信息
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            //获取应用程序版本号
            myApplicationVersion = packageInfo.versionName;
            //获取版本序号
            myVersionCode = packageInfo.versionCode;
            //获取版本名称
            myVersionName = myApplicationName + " v" + packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}

