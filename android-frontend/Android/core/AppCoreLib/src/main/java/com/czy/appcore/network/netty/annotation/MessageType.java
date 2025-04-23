package com.czy.appcore.network.netty.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 该注解可以用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时可访问
public @interface MessageType {
    String value(); // 用于注解类型
    // 描述
    String desc() default ""; // 用于描述
}
