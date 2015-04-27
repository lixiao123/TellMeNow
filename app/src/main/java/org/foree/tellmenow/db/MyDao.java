package org.foree.tellmenow.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.foree.tellmenow.base.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by foree on 15-4-27.
 * 数据库增删改查的实现类
 */
public class MyDao {
    private static final String TAG = "MyDao";
    private MySQLiteOpenHelper mySQLiteOpenHelper;

    public MyDao(Context context) {
        mySQLiteOpenHelper = new MySQLiteOpenHelper(context);
    }

    /**
     * 用户个人数据库增加操作
     */
    public void add(String name, String number, String date) {
        SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("number", number);
        values.put("date", date);
        db.insert(MyApplication.userTable, null, values);
        db.close();
    }

    /**
     * 数据库删除操作
     */
    public int delete(String name) {
        SQLiteDatabase db = mySQLiteOpenHelper.getReadableDatabase();
        int result = db.delete(MyApplication.userTable, "name=?", new String[]{name});
        db.close();
        return result;
    }

    /**
     * 数据库全部删除
     */
    public void deleteAll(String tableName) {
        SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }

    /**
     * 数据库查找操作
     */
    public boolean find(String name) {
        SQLiteDatabase db = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(MyApplication.userTable, null, "name=?", new String[]{name}, null, null, null);
        boolean result = cursor.moveToNext();
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 数据库查找操作,查找feedlist的type,填充到groupList<Map<String, String>>
     */
    public List<Map<String, String>> getAllInfo() {
        List<Map<String, String>> dataList = new ArrayList<>();
        SQLiteDatabase db = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(MyApplication.userTable, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", cursor.getString(cursor.getColumnIndex("name")));
            map.put("number", cursor.getString(cursor.getColumnIndex("number")));
            map.put("date", cursor.getString(cursor.getColumnIndex("date")));
            dataList.add(map);
        }
        cursor.close();
        db.close();
        return dataList;
    }

}
