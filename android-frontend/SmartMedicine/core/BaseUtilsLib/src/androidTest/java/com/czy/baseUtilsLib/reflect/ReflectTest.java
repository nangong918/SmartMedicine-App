package com.czy.baseUtilsLib.reflect;


import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ReflectTest {

    @Test
    public void reflectTest() {
        B bInstance = new B();
        A aInstance = new A(bInstance);

        // 调用不同的方法
        System.out.println("Processed Value from B.doubleValue: " + aInstance.processMethod("doubleValue", 10)); // 输出: 40
        System.out.println("Processed String from B.prependZero: " + aInstance.processMethod("prependZero", "Hello")); // 输出: 0Hello
        System.out.println("Processed Double from B.incrementValue: " + aInstance.processMethod("incrementValue", 5.5)); // 输出: 7.5

        System.out.println("Processed Value from B.doubleValue: " + aInstance.processMethod2("doubleValue", 10)); // 输出: 40
        System.out.println("Processed String from B.prependZero: " + aInstance.processMethod2("prependZero", "Hello")); // 输出: 0Hello
        System.out.println("Processed Double from B.incrementValue: " + aInstance.processMethod2("incrementValue", 5.5)); // 输出: 7.5
    }

    @Test
    public void reflectInterfaceTest() {
        ReflectTestImpl reflectTestImpl = new ReflectTestImpl((fileName, clazz) -> {
            System.out.println("fileDownloadResource called with fileName: " + fileName);
            System.out.println("clazz: " + clazz);
            return null;
        });

        reflectTestImpl.processMethod("fileDownloadResource", "test.txt", String.class);
    }

}
