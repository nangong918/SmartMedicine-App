package com.czy.springUtils;

/**
 * @author 13225
 * @date 2025/1/3 17:00
 */
public class TestUtil {
    public static int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
