package com.czy.baseUtilsLib;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.czy.baseUtilsLib.reflect.A;
import com.czy.baseUtilsLib.reflect.B;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.czy.utilslib.test", appContext.getPackageName());
    }

    @Test
    public void reflectTest2() {
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
}