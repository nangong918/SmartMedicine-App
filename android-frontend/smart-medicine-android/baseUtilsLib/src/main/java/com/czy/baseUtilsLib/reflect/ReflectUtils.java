package com.czy.baseUtilsLib.reflect;

import java.lang.reflect.Method;


/**
 * 反射工具
 * 反射需要提供函数的名称，无法自动生成对应的函数
 */
public class ReflectUtils {


    /**
     * 检查是否为object的方法
     * @param method 方法
     * @return       是否为Api的方法
     */
    public static boolean isApiMethod(Method method) {
        // 过滤掉 Object 类的方法
        String methodName = method.getName();
        return !"getClass".equals(methodName) &&
                !"hashCode".equals(methodName) &&
                !"equals".equals(methodName) &&
                !"toString".equals(methodName) &&
                !"notify".equals(methodName) &&
                !"notifyAll".equals(methodName) &&
                !"wait".equals(methodName) &&
                !"finalize".equals(methodName) &&
                !"clone".equals(methodName); // 其他需要过滤的方法
    }

    /**
     * 检查是否为 API 方法
     * @param method 方法
     * @param excludeClass 需要排除的方法所在的类
     * @return 是否为 API 方法
     */
    public static boolean isApiMethod(Method method, Class<?> excludeClass) {
        // 过滤掉指定类中的方法
        String methodName = method.getName();

        // 检查是否是 excludeClass 类的方法
        for (Method excludeMethod : excludeClass.getDeclaredMethods()) {
            if (excludeMethod.getName().equals(methodName)) {
                return false;
            }
        }

        // 继续过滤 Object 类的方法
        return !"getClass".equals(methodName) &&
                !"hashCode".equals(methodName) &&
                !"equals".equals(methodName) &&
                !"toString".equals(methodName) &&
                !"notify".equals(methodName) &&
                !"notifyAll".equals(methodName) &&
                !"wait".equals(methodName) &&
                !"finalize".equals(methodName) &&
                !"clone".equals(methodName); // 其他需要过滤的方法
    }
}
