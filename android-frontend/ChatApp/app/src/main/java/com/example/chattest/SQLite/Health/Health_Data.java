package com.example.chattest.SQLite.Health;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.chattest.Test.MyDebug;

import java.util.ArrayList;
import java.util.List;


public class Health_Data {
    public Integer id;
    public Integer user_id;
    public Integer hypertension;
    public Integer high_cholesterol;
    public Integer bmi;
    public Integer smoking;
    public Integer stroke;
    public Integer physical_exercise;
    public Integer fruits;
    public Integer vegetable;
    public Integer drinking_alcohol;
    public Integer medical_care;
    public Integer no_medical_expenses;
    public Integer health_condition;
    public Integer psychological_health;
    public Integer physical_health;
    public Integer difficulty_walking;
    public Integer sex;
    public Integer age;
    public Integer education_level;
    public Integer income_level;

    public Health_Data() {
    }

    public Health_Data(Integer[] dataArray) {

        this.hypertension = dataArray[0];
        this.high_cholesterol = dataArray[1];
        this.bmi = dataArray[2];
        this.smoking = dataArray[3];
        this.stroke = dataArray[4];
        this.physical_exercise = dataArray[5];
        this.fruits = dataArray[6];
        this.vegetable = dataArray[7];
        this.drinking_alcohol = dataArray[8];
        this.medical_care = dataArray[9];
        this.no_medical_expenses = dataArray[10];
        this.health_condition = dataArray[11];
        this.psychological_health = dataArray[12];
        this.physical_health = dataArray[13];
        this.difficulty_walking = dataArray[14];
        this.sex = dataArray[15];
        this.age = dataArray[16];
        this.education_level = dataArray[17];
        this.income_level = dataArray[18];
    }

    //------------------------增------------------------

    public static long insert(Health_Data healthData, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("id", healthData.id);
        values.put("user_id", healthData.user_id);
        values.put("hypertension", healthData.hypertension);
        values.put("high_cholesterol", healthData.high_cholesterol);
        values.put("bmi", healthData.bmi);
        values.put("smoking", healthData.smoking);
        values.put("stroke", healthData.stroke);
        values.put("physical_exercise", healthData.physical_exercise);
        values.put("fruits", healthData.fruits);
        values.put("vegetable", healthData.vegetable);
        values.put("drinking_alcohol", healthData.drinking_alcohol);
        values.put("medical_care", healthData.medical_care);
        values.put("no_medical_expenses", healthData.no_medical_expenses);
        values.put("health_condition", healthData.health_condition);
        values.put("psychological_health", healthData.psychological_health);
        values.put("physical_health", healthData.physical_health);
        values.put("difficulty_walking", healthData.difficulty_walking);
        values.put("sex", healthData.sex);
        values.put("age", healthData.age);
        values.put("education_level", healthData.education_level);
        values.put("income_level", healthData.income_level);
        return db.insert("health", null, values);
    }

    //------------------------删------------------------

    //通过id删除
    public static int delete(long id, SQLiteDatabase db) {
        return db.delete("health", "id = ?", new String[]{String.valueOf(id)});
    }

    //------------------------改------------------------

    public static int update(Health_Data healthData, SQLiteDatabase db) {
        if(healthData.id == null){
            return -1;
        }
        ContentValues values = new ContentValues();

        if (healthData.user_id != null) {
            values.put("user_id", healthData.user_id);
        }
        if (healthData.hypertension != null) {
            values.put("hypertension", healthData.hypertension);
        }
        if (healthData.high_cholesterol != null) {
            values.put("high_cholesterol", healthData.high_cholesterol);
        }
        if (healthData.bmi != null) {
            values.put("bmi", healthData.bmi);
        }
        if (healthData.smoking != null) {
            values.put("smoking", healthData.smoking);
        }
        if (healthData.stroke != null) {
            values.put("stroke", healthData.stroke);
        }
        if (healthData.physical_exercise != null) {
            values.put("physical_exercise", healthData.physical_exercise);
        }
        if (healthData.fruits != null) {
            values.put("fruits", healthData.fruits);
        }
        if (healthData.vegetable != null) {
            values.put("vegetable", healthData.vegetable);
        }
        if (healthData.drinking_alcohol != null) {
            values.put("drinking_alcohol", healthData.drinking_alcohol);
        }
        if (healthData.medical_care != null) {
            values.put("medical_care", healthData.medical_care);
        }
        if (healthData.no_medical_expenses != null) {
            values.put("no_medical_expenses", healthData.no_medical_expenses);
        }
        if (healthData.health_condition != null) {
            values.put("health_condition", healthData.health_condition);
        }
        if (healthData.psychological_health != null) {
            values.put("psychological_health", healthData.psychological_health);
        }
        if (healthData.physical_health != null) {
            values.put("physical_health", healthData.physical_health);
        }
        if (healthData.difficulty_walking != null) {
            values.put("difficulty_walking", healthData.difficulty_walking);
        }
        if (healthData.sex != null) {
            values.put("sex", healthData.sex);
        }
        if (healthData.age != null) {
            values.put("age", healthData.age);
        }
        if (healthData.education_level != null) {
            values.put("education_level", healthData.education_level);
        }
        if (healthData.income_level != null) {
            values.put("income_level", healthData.income_level);
        }
        // 只更新非空的属性
        return db.update("health", values, "id = ?", new String[]{String.valueOf(healthData.id)});
    }

    //------------------------查------------------------

    // 根据 ID 查询提醒数据

    public static Health_Data queryById(Integer id, SQLiteDatabase db) {
        Cursor cursor = db.query("health", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            Health_Data healthData = buildHealthData(cursor);
            cursor.close();
            return healthData;
        }
        else {
            return null;
        }
    }

    // 根据 User-ID 查询提醒数据

    public static List<Health_Data> queryByUserId(int userId, SQLiteDatabase db) {
        List<Health_Data> healthDataList = new ArrayList<>();
        MyDebug.Print("db:"+db);
        Cursor cursor = db.query("health", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.getCount() == 0) {
            // 如果没有查询到数据,则返回 null
            cursor.close();
            return null;
        }
        while (cursor.moveToNext()) {
            Health_Data healthData = buildHealthData(cursor);
            healthDataList.add(healthData);
        }
        cursor.close();
        return healthDataList;
    }

    // 将 Cursor 转换为 Health_Data 对象
    private static Health_Data buildHealthData(Cursor cursor) {
        Health_Data healthData = new Health_Data();
        healthData.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        healthData.user_id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        healthData.hypertension = cursor.getInt(cursor.getColumnIndexOrThrow("hypertension"));
        healthData.high_cholesterol = cursor.getInt(cursor.getColumnIndexOrThrow("high_cholesterol"));
        healthData.bmi = cursor.getInt(cursor.getColumnIndexOrThrow("bmi"));
        healthData.smoking = cursor.getInt(cursor.getColumnIndexOrThrow("smoking"));
        healthData.stroke = cursor.getInt(cursor.getColumnIndexOrThrow("stroke"));
        healthData.physical_exercise = cursor.getInt(cursor.getColumnIndexOrThrow("physical_exercise"));
        healthData.fruits = cursor.getInt(cursor.getColumnIndexOrThrow("fruits"));
        healthData.vegetable = cursor.getInt(cursor.getColumnIndexOrThrow("vegetable"));
        healthData.drinking_alcohol = cursor.getInt(cursor.getColumnIndexOrThrow("drinking_alcohol"));
        healthData.medical_care = cursor.getInt(cursor.getColumnIndexOrThrow("medical_care"));
        healthData.no_medical_expenses = cursor.getInt(cursor.getColumnIndexOrThrow("no_medical_expenses"));
        healthData.health_condition = cursor.getInt(cursor.getColumnIndexOrThrow("health_condition"));
        healthData.psychological_health = cursor.getInt(cursor.getColumnIndexOrThrow("psychological_health"));
        healthData.physical_health = cursor.getInt(cursor.getColumnIndexOrThrow("physical_health"));
        healthData.difficulty_walking = cursor.getInt(cursor.getColumnIndexOrThrow("difficulty_walking"));
        healthData.sex = cursor.getInt(cursor.getColumnIndexOrThrow("sex"));
        healthData.age = cursor.getInt(cursor.getColumnIndexOrThrow("age"));
        healthData.education_level = cursor.getInt(cursor.getColumnIndexOrThrow("education_level"));
        healthData.income_level = cursor.getInt(cursor.getColumnIndexOrThrow("income_level"));
        return healthData;
    }

}
