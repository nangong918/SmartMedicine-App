package com.example.chattest.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.chattest.Test.MyDebug;


public class DB_Helper extends SQLiteOpenHelper {

    //--------------------------------Database Data--------------------------------

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "location_db";
    private static final String[] TABLES = {"health","remind"};
    public SQLiteDatabase getDatabase(){
        return getWritableDatabase();
    }


    //--------------------------------Constructor--------------------------------

    public DB_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        MyDebug.Print("DB_Helper");
    }

    //--------------------------------onCreate--------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
        MyDebug.Print("成功创建数据库实例并连接");
        //检查创建表
        //checkTableCreate(db);
        createTables(db);
    }
    //检查表的存在情况
    private void checkTableCreate(SQLiteDatabase db){
        for (int i = 0; i < TABLES.length ;i++){
            String tableName = TABLES[i];
            String createTable = TABLES_CREATE[i];
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
            if (cursor != null && cursor.getCount() == 0) {
                MyDebug.Print("正在创建表");
                db.execSQL(createTable);
            }
            if (cursor != null) {
                MyDebug.Print("表："+tableName+"已经存在了");
                cursor.close();
            }
        }
    }
    private void createTables(SQLiteDatabase db){
        for (int i = 0; i < TABLES.length ;i++){
            String tableName = TABLES[i];
            String createTable = TABLES_CREATE[i];
            MyDebug.Print("正在创建表:"+tableName);
            db.execSQL(createTable);
        }
    }

    //--------------------------------onUpgrade--------------------------------

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        for(String tableName : TABLES){
            String sql = "DROP TABLE IF EXISTS " + tableName;
            db.execSQL(sql);
        }
        onCreate(db);
    }

    //--------------------------------Table--------------------------------

    private static final String CREATE_TABLE_HEALTH = "CREATE TABLE health (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_id INTEGER," +
            "hypertension INTEGER," +
            "high_cholesterol INTEGER," +
            "bmi INTEGER," +
            "smoking INTEGER," +
            "stroke INTEGER," +
            "physical_exercise INTEGER," +
            "fruits INTEGER," +
            "vegetable INTEGER," +
            "drinking_alcohol INTEGER," +
            "medical_care INTEGER," +
            "no_medical_expenses INTEGER," +
            "health_condition INTEGER," +
            "psychological_health INTEGER," +
            "physical_health INTEGER," +
            "difficulty_walking INTEGER," +
            "sex INTEGER," +
            "age INTEGER," +
            "education_level INTEGER," +
            "income_level INTEGER" +
            ");";

    private static final String CREATE_TABLE_REMIND = "CREATE TABLE remind (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title TEXT," +
            "content TEXT," +
            "user_id INTEGER," +
            "time INTEGER" +
            ");";

    private static final String[] TABLES_CREATE = { CREATE_TABLE_HEALTH , CREATE_TABLE_REMIND };
}
