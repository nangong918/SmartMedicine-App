package com.czy.baseUtilsLib.file;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Android文件存储缓存数据
 */
public class SharePreferencesReadWriteUtil {

    // 增，改
    public static void saveData(String fileName, Map<String, String> data, Application appInstance) {
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, appInstance);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }

        editor.apply();
    }

    // 查
    public static Map<String,String> loadData(String fileName, Application appInstance){
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, appInstance);
        Map<String, String> data = new HashMap<>();

        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            data.put(entry.getKey(), entry.getValue().toString());
        }

        return data;
    }

    // 删（全部）
    public static void deleteAll(String fileName, Application appInstance) {
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, appInstance);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // 删（指定）
    public static void deleteData(String fileName, String key, Application appInstance) {
        SharedPreferences sharedPreferences = getSharedPreferences(fileName, appInstance);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    // 指定Shared Preferences的context
    private static SharedPreferences getSharedPreferences(String fileName, Application appInstance) {
        return appInstance.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }
}
