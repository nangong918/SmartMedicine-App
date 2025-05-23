package com.czy.baseUtilsLib.reflect;

import java.lang.reflect.Method;

public class ReflectTestImpl {

    private final ReflectTestInterface reflectTestInterface;

    public ReflectTestImpl(ReflectTestInterface reflectTestInterface) {
        this.reflectTestInterface = reflectTestInterface;
    }

    public Object processMethod(String methodName, Object... args){
        try {
            // 获取参数类型
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }

            // 获取方法
            Method method = reflectTestInterface.getClass().getMethod(methodName, paramTypes);
            Object result = method.invoke(reflectTestInterface, args); // 调用方法

            // 获取返回类型
            Class<?> returnType = method.getReturnType();

            // 获取返回泛型类型
            Class<?> genericReturnType = paramTypes[paramTypes.length - 1];

            // 根据返回值类型处理
            return handleReturnValue(result, genericReturnType);
        } catch (Exception e) {
            return null;
        }
    }

    // 获取C内部的具体值
    private Object handleReturnValue(Object result, Class<?> returnType) {
        System.out.println("returnType: " + returnType);

        if (result == null) {
            return null;
        }

        if (result instanceof C<?>) {
            return ((C<?>) result).value(); // 获取 C 中的具体值
        }

        return result; // 直接返回其他类型的结果
    }

}
