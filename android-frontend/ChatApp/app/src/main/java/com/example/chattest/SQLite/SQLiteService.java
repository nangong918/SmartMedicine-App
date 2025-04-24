package com.example.chattest.SQLite;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.chattest.R;
import com.example.chattest.SQLite.Health.Health_Data;
import com.example.chattest.SQLite.Remind.Remind_Data;
import com.example.chattest.SQLite.Setting.Setting_Data;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.User.User;

import java.util.List;

public class SQLiteService extends Service {
    private static SQLiteService instance;
    private DB_Helper dbHelper;
    public static SQLiteDatabase db;

    public SQLiteService() {
        // 私有化构造方法,防止外部直接创建实例
    }

    public static SQLiteService getInstance() {
        if (instance == null) {
            synchronized (SQLiteService.class) {
                if (instance == null) {
                    instance = new SQLiteService();
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 使用 getApplicationContext() 获取 Context
        MyDebug.Print("Service的onCreate()被调用");
        Context context = getApplicationContext();
        try{
            dbHelper = new DB_Helper(context);
            db = dbHelper.getDatabase();
            //重置数据库
            //dbHelper.onUpgrade(db,1,1);
        }catch (Exception e){
            e.printStackTrace();
            context.deleteDatabase(DB_Helper.DATABASE_NAME);
        }

        //Test();
    }

    private void Test(){
        Remind_Data remindData = new Remind_Data();
        remindData.time = System.currentTimeMillis();
        remindData.userId = 1;
        remindData.id = 2;
        remindData.content = "2222";
        remindData.title = "1";
        Remind_Data.insert(remindData,db);
        List<Remind_Data> remindData2 = Remind_Data.queryByUserId(1,db);
        MyDebug.Print("remindData2:"+remindData2.size());
        MyDebug.Print("remindData2:"+remindData2.get(0).content);
        Remind_Data update_remind = new Remind_Data();
        update_remind.id = 2;
        update_remind.content = "司马";
        Remind_Data.update(update_remind,db);
        Remind_Data data = Remind_Data.queryById(2,db);
        assert data != null;
        MyDebug.Print("更新结果:"+data.content);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在 Service 销毁时释放资源
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //---------------------------Health---------------------------

    //------------添加的同时检查存在
    public boolean Add_Health(Health_Data healthData){
        boolean state = false;
        if(healthData != null && healthData.user_id != null){
            List<Health_Data> dataList = Health_Data.queryByUserId(healthData.user_id,db);
            if(dataList != null && dataList.size() > 0 && dataList.get(0).id != null){//存在再能修改
                healthData.id = dataList.get(0).id;
                Health_Data.update(healthData,db);
                MyDebug.Print("健康数据更新成功");
            }
            else{
                Health_Data.insert(healthData,db);
                MyDebug.Print("健康数据插入成功");
            }
            state = true;
        }
        return state;
    }

    //------------返回用户的健康信息，可能为null
    public Health_Data getUserHealthData(){
        Health_Data healthData = null;
        int user_id;
        if(User.Logged){
            user_id = User.user_id;
        }
        else {
            user_id = 0;
        }
        List<Health_Data> dataList = Health_Data.queryByUserId(user_id,db);
        if(dataList != null && dataList.size() > 0){
            healthData = dataList.get(0);
        }
        return healthData;
    }

    public static int[] changeHealthData_to_Array(Health_Data data){
        int[] Array = new int[19];
        Array[0] = data.hypertension;
        Array[1] = data.high_cholesterol;
        Array[2] = data.bmi;
        Array[3] = data.smoking;
        Array[4] = data.stroke;
        Array[5] = data.physical_exercise;
        Array[6] = data.fruits;
        Array[7] = data.vegetable;
        Array[8] = data.drinking_alcohol;
        Array[9] = data.medical_care;
        Array[10] = data.no_medical_expenses;
        Array[11] = data.health_condition;
        Array[12] = data.psychological_health;
        Array[13] = data.physical_health;
        Array[14] = data.difficulty_walking;
        Array[15] = data.sex;
        Array[16] = data.age;
        Array[17] = data.education_level;
        Array[18] = data.income_level;
        return Array;
    }

}