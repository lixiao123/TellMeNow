package org.foree.tellmenow.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.foree.tellmenow.base.MyApplication;

/**
 * Created by foree on 15-4-27.
 * 创建数据库保存监听到所有号码
 */
public class MySQLiteOpenHelper  extends SQLiteOpenHelper {

    //默认的构造方法,用来传入数据库的名字,游标和版本
    public MySQLiteOpenHelper(Context context) {
        super(context, MyApplication.dbName, null, MyApplication.myDataBaseVersion);
    }

    //在数据库第一次被创建的时候调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        //初始化数据库的表结构,表rss,字段id,name,url
        db.execSQL("create table tmn (id integer primary key autoincrement, name varchar(20), number varchar(20), date varchar(20)) ");
        }

    //在数据库版本号增加的时候调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
