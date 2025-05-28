package com.czy.baseUtilsLib.database;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

// TODO 待测试
public abstract class BaseDatabase<D> extends RoomDatabase {
    private static volatile BaseDatabase instance;
    private final String dbName;

    protected BaseDatabase(String dbName) {
        this.dbName = dbName;
    }

    public static synchronized <D> BaseDatabase<D> getInstance(Context context, Class<? extends BaseDatabase<D>> clazz) {
        if (instance == null) {
            instance = createDatabase(context, clazz);
        }
        // TODO 检查泛型强转存在的问题
        return (BaseDatabase<D>) instance; // 强制转换回具体类型
    }

    private static <D> BaseDatabase<D> createDatabase(Context context, Class<? extends BaseDatabase<D>> clazz) {
        return Room.databaseBuilder(context, clazz, ((BaseDatabase<?>) instance).dbName)
                .addCallback(new RoomDatabase.Callback() {
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

    public abstract D getDao(); // 这里可以根据需求调整
}