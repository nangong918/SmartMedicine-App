package com.czy.baseUtilsLib.database;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

// TODO 待测试
public abstract class BaseDatabase2 extends RoomDatabase {
    private static volatile BaseDatabase2 instance;
    private final String dbName;

    protected BaseDatabase2(String dbName) {
        this.dbName = dbName;
    }

    public static synchronized BaseDatabase2 getInstance(Context context, Class<? extends BaseDatabase2> clazz) {
        if (instance == null) {
            instance = createDatabase(context, clazz);
        }
        return instance; // 强制转换回具体类型
    }

    private static BaseDatabase2 createDatabase(Context context, Class<? extends BaseDatabase2> clazz) {
        return Room.databaseBuilder(context, clazz, instance.dbName)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                    }
                })
                .addMigrations() // 添加迁移策略
                .build();
    }
    
    public abstract Class<?> getDao();

}