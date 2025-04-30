package com.example.chattest.SQLite.Remind;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Remind_Data {
    public Integer id;
    public String title;
    public String content;
    public Integer userId;
    public Long time;

    //------------------------增------------------------
    public static long insert(Remind_Data remindData, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("title", remindData.title);
        values.put("content", remindData.content);
        values.put("user_id", remindData.userId);
        values.put("time", remindData.time);
        return db.insert("remind", null, values);
    }

    //------------------------删------------------------

    //通过id删除
    public static int delete(long id, SQLiteDatabase db) {
        return db.delete("remind", "id = ?", new String[]{String.valueOf(id)});
    }

    //------------------------改------------------------

    public static int update(Remind_Data remindData, SQLiteDatabase db) {
        if(remindData.id == null){
            return -1;
        }
        ContentValues values = new ContentValues();

        if (remindData.title != null) {
            values.put("title", remindData.title);
        }
        if (remindData.content != null) {
            values.put("content", remindData.content);
        }
        if (remindData.userId != null) {
            values.put("user_id", remindData.userId);
        }
        if (remindData.time != null) {
            values.put("time", remindData.time);
        }

        // 只更新非空的属性
        return db.update("remind", values, "id = ?", new String[]{String.valueOf(remindData.id)});
    }

    //------------------------查------------------------

    // 根据 ID 查询提醒数据
    public static Remind_Data queryById(Integer id, SQLiteDatabase db) {
        Cursor cursor = db.query("remind", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            Remind_Data remindData = buildRemindData(cursor);
            cursor.close();
            return remindData;
        }
        else {
            return null;
        }
    }

    // 根据 User-ID 查询提醒数据
    public static List<Remind_Data> queryByUserId(int userId, SQLiteDatabase db) {
        List<Remind_Data> remindDataList = new ArrayList<>();
        Cursor cursor = db.query("remind", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);
        while (cursor.moveToNext()) {
            Remind_Data remindData = buildRemindData(cursor);
            remindDataList.add(remindData);
        }
        cursor.close();
        return remindDataList;
    }

    // 将 Cursor 转换为 Remind_Data 对象
    private static Remind_Data buildRemindData(Cursor cursor) {
        Remind_Data remindData = new Remind_Data();
        remindData.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        remindData.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        remindData.content = (cursor.getString(cursor.getColumnIndexOrThrow("content")));
        remindData.userId = (cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
        remindData.time = (cursor.getLong(cursor.getColumnIndexOrThrow("time")));
        return remindData;
    }
}
