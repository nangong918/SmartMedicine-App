package com.czy.baseUtilsLib.number;


public class NumberUtils {

    // 泛型方法，将字符串转换为指定类型
    @SuppressWarnings("unchecked")
    public static <T> T parseNumber(String str, Class<T> clazz) {
        try {
            if (clazz == Integer.class) {
                return clazz.cast(Integer.parseInt(str));
            } else if (clazz == Double.class) {
                return clazz.cast(Double.parseDouble(str));
            } else if (clazz == Long.class) {
                return clazz.cast(Long.parseLong(str));
            } else if (clazz == Float.class) {
                return clazz.cast(Float.parseFloat(str));
            } else if (clazz == int.class) {
                return (T) Integer.valueOf(Integer.parseInt(str));
            } else if (clazz == double.class) {
                return (T) Double.valueOf(Double.parseDouble(str));
            } else if (clazz == long.class) {
                return (T) Long.valueOf(Long.parseLong(str));
            } else if (clazz == float.class) {
                return (T) Float.valueOf(Float.parseFloat(str));
            }
        } catch (NumberFormatException e) {
            // 转换失败，返回默认值
            if (clazz == Integer.class) {
                return clazz.cast(0);
            } else if (clazz == int.class) {
                return (T) Integer.valueOf(0);
            } else if (clazz == Double.class) {
                return clazz.cast(0.0);
            } else if (clazz == double.class) {
                return (T) Double.valueOf(0.0);
            } else if (clazz == Long.class) {
                return clazz.cast(0L);
            } else if (clazz == long.class) {
                return (T) Long.valueOf(0L);
            } else if (clazz == Float.class) {
                return clazz.cast(0.0f);
            } else if (clazz == float.class) {
                return (T) Float.valueOf(0.0f);
            } else {
                return null;
            }
        } catch (Exception e){
            return null;
        }
        return null; // 如果类型不匹配，返回 null
    }
}
