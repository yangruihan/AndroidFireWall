package com.yrh.firewall.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yrh.firewall.model.PhoneInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yrh on 2015/12/13.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;

    public static final String BLACK_LIST_TABLE = "t_blacklist";

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * 创建黑名单表
         * 字段：
         *      编号：              id                   INTEGER       PRIMARY KEY AUTOINCREMENT
         *      手机号码：          phoneNum             VARCHAR(11)
         *      是否在来电黑名单：   inIncommingBlack     INTEGER        1 表示在里面 0 表示不在里面
         *      是否在短信黑名单：   inSMSBlack           INTEGER        1 表示在里面 0 表示不在里面
         */
        String createBlackListTable = "CREATE TABLE IF NOT EXISTS " + BLACK_LIST_TABLE + " ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " phoneNum VARCHAR(11), "
                + " inIncommingBlack INTEGER, "
                + " inSMSBlack INTEGER " + ");";
        db.execSQL(createBlackListTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 删除已存在的所有表
        db.execSQL("DROP TABLE IF EXISTS " + BLACK_LIST_TABLE);

        // 重新创建表
        onCreate(db);
    }

    public List<PhoneInfo> getAllPhoneInfo() {
        List<PhoneInfo> result = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + BLACK_LIST_TABLE + " ORDER BY id DESC;";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String phoneNum = cursor.getString(1);
                int inIncommingBlack = cursor.getInt(2);
                int inSMSBlack = cursor.getInt(3);
                PhoneInfo phoneInfo = new PhoneInfo(phoneNum, inIncommingBlack, inSMSBlack);

                result.add(phoneInfo);
            } while (cursor.moveToNext());
        }
        db.close();
        return result;
    }

    /**
     * 增加一条手机信息
     *
     * @param phoneInfo
     */
    public boolean addPhoneInfo(PhoneInfo phoneInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (phoneInfo != null) {
            db.execSQL("INSERT INTO " + BLACK_LIST_TABLE + " VALUES(NULL, ?, ?, ?);",
                    new Object[]{phoneInfo.getPhoneNum(),
                            phoneInfo.getInIncommingBlack(),
                            phoneInfo.getInSMSBlack()});
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }
    }

    /**
     * 判断一个手机号是否在来电黑名单里
     */
    public boolean isInIncommingBlackList(String phoneNum) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT * FROM " + BLACK_LIST_TABLE + " WHERE phoneNum='" + phoneNum +"'  AND inIncommingBlack=1;";
        try {
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                db.close();
                return true;
            }
        } catch (Exception e) {
            db.close();
            return false;
        }
        db.close();
        return false;
    }
}
