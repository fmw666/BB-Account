package cn.edu.sicnu.account.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SqliteOpenHelper(context: Context): SQLiteOpenHelper(context, "sqlite3.db", null, 1){

    private fun SQLiteDatabase.insert1(phone: String, password: String) {
        val contentValues = ContentValues()
        contentValues.put("phone", phone)
        contentValues.put("password", password)
        insert("user", null, contentValues)
    }

    private fun SQLiteDatabase.insert2(in_or_out: Int, money: Double, type: String, note: String, time: String) {
        val contentValues = ContentValues()
        contentValues.put("in_or_out", in_or_out)
        contentValues.put("money", money)
        contentValues.put("type", type)
        contentValues.put("note", note)
        contentValues.put("time", time)
        insert("bills", null, contentValues)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table user(_id integer primary key autoincrement, phone text, password text);")
        db?.execSQL("create table bills(_id integer primary key autoincrement, in_or_out int, money real, type text, note text, time text);")
        db?.insert1("18008145963", "fmw666")
        db?.insert1("15182613457", "fmw666")
        db?.insert2(0, 60.0, "水果", "测试用", "2020.6.17")
        db?.insert2(1, 399.9, "服饰", "测试用", "2020.6.17")
        db?.insert2(1, 666.6, "电脑", "测试用", "2019.12.23")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists user");
        db?.execSQL("drop table if exists account");
        onCreate(db);
    }
}