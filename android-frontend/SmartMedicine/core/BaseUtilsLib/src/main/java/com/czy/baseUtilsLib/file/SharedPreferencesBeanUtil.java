package com.czy.baseUtilsLib.file;

import android.content.SharedPreferences;

import java.lang.reflect.Field;

public class SharedPreferencesBeanUtil {

    public static void saveToSharedPreferences(Object obj, SharedPreferences sp) throws Exception{
        if (obj == null) {
            throw new Exception("Object is Null");
        }
        if (sp == null) {
            throw new Exception("SharedPreferences is Null");
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // 允许访问私有字段
            Object value = field.get(obj);
            if (value instanceof String) {
                sp.edit().putString(field.getName(), (String) value).apply();
            } else if (value instanceof Integer) {
                sp.edit().putInt(field.getName(), (Integer) value).apply();
            } else if (value instanceof Boolean) {
                sp.edit().putBoolean(field.getName(), (Boolean) value).apply();
            } else if (value instanceof Float) {
                sp.edit().putFloat(field.getName(), (Float) value).apply();
            } else if (value instanceof Long) {
                sp.edit().putLong(field.getName(), (Long) value).apply();
            } else if (value instanceof Byte) {
                sp.edit().putInt(field.getName(), (Byte) value); // 保存为 int
            }// 可扩展其他基本类型
        }
    }

    public static void getFromSharedPreferences(Object obj, SharedPreferences sp) throws Exception{
        if (obj == null) {
            throw new Exception("Object is Null");
        }
        if (sp == null) {
            throw new Exception("SharedPreferences is Null");
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // 允许访问私有字段
            if (field.getType() == String.class) {
                field.set(obj, sp.getString(field.getName(), ""));
            } else if (field.getType() == Integer.class) {
                field.set(obj, sp.getInt(field.getName(), 0));
            } else if (field.getType() == Boolean.class) {
                field.set(obj, sp.getBoolean(field.getName(), false));
            } else if (field.getType() == Float.class) {
                field.set(obj, sp.getFloat(field.getName(), 0.0f));
            } else if (field.getType() == Long.class) {
                field.set(obj, sp.getLong(field.getName(), 0L));
            } else if (field.getType() == Byte.class) {
                field.set(obj, (byte) sp.getInt(field.getName(), 0)); // 从 int 转换为 byte
            }// 可扩展其他基本类型
        }
    }

}
