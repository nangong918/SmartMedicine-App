package com.czy.baseUtilsLib.reflect;

import android.util.Log;


import java.lang.reflect.Method;

public class A {
    private final Object b;

    public A(Object b) {
        this.b = b;
    }

    public Object processMethod(String methodName, Object... args) {
        try {
            // 获取参数类型
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }

            // 获取方法
            Method method = b.getClass().getMethod(methodName, paramTypes);
            Object result = method.invoke(b, args); // 调用方法

            // 获取返回类型
            Class<?> returnType = method.getReturnType();

            // 根据返回值类型处理
            return handleReturnValue(result, returnType);
        } catch (Exception e) {
            Log.e(A.class.getSimpleName(),"A::处理方法错误", e);
            return null;
        }
    }

    public Object processMethod2(String methodName, Object... args) {
        try {
            // 获取参数类型
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }

            // 获取方法
            Method method = b.getClass().getMethod(methodName, paramTypes);
            Object result = method.invoke(b, args); // 调用方法

            // 获取返回类型
            Class<?> returnType = method.getReturnType();

            // 根据返回值类型处理
            return handleReturnValue(result);
        } catch (Exception e) {
            Log.e(A.class.getSimpleName(), "A::processMethod2反射异常：" + e.getMessage(), e);
            return null;
        }
    }

    private Object handleReturnValue(Object result, Class<?> returnType) {
        if (result == null) {
            return null;
        }

        // 检查是否为 C 的实例
/*        if (C.class.isAssignableFrom(returnType)) {
            // 获取泛型类型
            ParameterizedType parameterizedType = (ParameterizedType) returnType.getGenericSuperclass();
            Class<?> genericType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            System.out.println("Generic type: " + genericType.getName()); // 打印泛型类型

            // 返回 C 中的具体值
            return ((C<?>) result).getValue();
        }*/

        return (returnType).cast(result);
    }

    // 获取C内部的具体值
    private Object handleReturnValue(Object result) {
        if (result == null) {
            return null;
        }

        if (result instanceof C<?>) {
            System.out.println("Generic type: " + ((C<?>) result).getValueType()); // 打印泛型类型
            return ((C<?>) result).value(); // 获取 C 中的具体值
        }

        return result; // 直接返回其他类型的结果
    }
}