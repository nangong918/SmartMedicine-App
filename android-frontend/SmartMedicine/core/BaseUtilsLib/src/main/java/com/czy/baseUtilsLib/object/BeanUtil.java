package com.czy.baseUtilsLib.object;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanUtil {

    private static final String TAG = BeanUtil.class.getSimpleName();

    /**
     * 使用反射将Map中的值赋值给对象的属性
     *
     * @param dataMap 数据源Map
     * @param target  目标对象
     */
    public static void copyPropertiesFromMap(Map<String, String> dataMap, Object target) {
        if (dataMap == null || target == null) {
            return;
        }
        // 获取目标类及其所有父类的字段
        Class<?> clazz = target.getClass();
        while (clazz != null){
            // 获取当前类的所有字段
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                try {
                    // 设置字段可访问（即使是私有字段）
                    field.setAccessible(true);

                    // 获取字段名
                    String fieldName = field.getName();

                    // 如果Map中包含该字段名
                    if (dataMap.containsKey(fieldName)) {
                        // 获取Map中的值
                        String value = dataMap.get(fieldName);

                        // 根据字段类型将String转换为对应的类型
                        Object convertedValue = convertStringToFieldType(value, field.getType());

                        // 将值赋值给目标对象的字段
                        if (convertedValue != null) {
                            field.set(target, convertedValue);
                        }
                    }
                } catch (IllegalAccessException e) {
                    Log.e(TAG,"字段赋值失败: " + field.getName(), e);
                }
            }

            // 继续处理父类的字段
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * 将String转换为字段的实际类型
     *
     * @param value     字符串值
     * @param fieldType 字段类型
     * @return 转换后的值
     */
    private static Object convertStringToFieldType(String value, Class<?> fieldType) {
        if (value == null) {
            return null;
        }

        try {
            if (fieldType == String.class) {
                return value;
            } else if (fieldType == int.class || fieldType == Integer.class) {
                return Integer.parseInt(value);
            } else if (fieldType == long.class || fieldType == Long.class) {
                return Long.parseLong(value);
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                return Boolean.parseBoolean(value);
            } else if (fieldType == double.class || fieldType == Double.class) {
                return Double.parseDouble(value);
            } else if (fieldType == float.class || fieldType == Float.class) {
                return Float.parseFloat(value);
            } else {
                Log.w(TAG, "不支持的类型: " + fieldType.getName());
                return null;
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "类型转换失败: " + value + " -> " + fieldType.getName(), e);
            return null;
        }
    }

    /**
     * 将对象转换为 Map<String, Object>
     *
     * @param bean 目标对象
     * @return Map<String, Object>
     */
    public static Map<String, Object> beanToMap(Object bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean == null) {
            return map;
        }

        Class<?> clazz = bean.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(bean)); // 将属性名和属性值放入Map
                } catch (IllegalAccessException e) {
                    Log.e(TAG,"字段访问失败: " + field.getName(), e);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return map;
    }

    /**
     * 将对象转换为 Map<String, String>
     *
     * @param bean 目标对象
     * @return Map<String, String>
     */
    public static Map<String, String> beanToStrMap(Object bean) {
        Map<String, String> map = new HashMap<>();
        if (bean == null) {
            return map;
        }

        Class<?> clazz = bean.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(bean);
                    if (value != null) {
                        map.put(field.getName(), value.toString()); // 将属性名和属性值(字符串形式)放入Map
                    }
                } catch (IllegalAccessException e) {
                    Log.e(TAG,"字段访问失败: " + field.getName(), e);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return map;
    }

}
