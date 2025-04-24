package com.example.chattest;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.chattest.SQLite.DB_Helper;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public void test1(){
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formattedDateTime = dateFormat.format(currentDate);

        System.out.println("当前时间: " + formattedDateTime);
    }


    @Test
    public void getDB_Test() {

    }
}