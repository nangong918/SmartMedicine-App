package com.czy.baseUtilsLib;

import org.junit.Test;

import static org.junit.Assert.*;

import com.czy.baseUtilsLib.date.DateUtils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void data_test() {
        String today = DateUtils.getCurrentDate();
        System.out.println("today:"+today);
        String todayTime = DateUtils.getCurrentDateTime();
        System.out.println("todayTime:"+todayTime);
    }
}