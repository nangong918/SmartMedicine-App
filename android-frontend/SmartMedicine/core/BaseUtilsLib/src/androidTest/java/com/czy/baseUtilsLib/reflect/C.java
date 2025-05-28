package com.czy.baseUtilsLib.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public record C<T>(T value) {

    public Class<?> getValueType() {
        // 获取当前类的父类类型
        Type superclass = getClass().getGenericSuperclass();
        // 检查是否为 ParameterizedType
        if (superclass instanceof ParameterizedType) {
            // 获取泛型参数类型
            return (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        }
        return Object.class; // 默认返回 Object
    }
}
