package com.czy.appcore.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyUtil {

    // String -> BigDecimal (入参表明保留几位小数)
    public static BigDecimal stringToBigDecimal(String str, int scale) {
        BigDecimal bigDecimal = BigDecimal.ZERO;
        if (str == null) {
            return bigDecimal;
        }
        bigDecimal = new BigDecimal(str).setScale(scale, RoundingMode.HALF_UP);
        return bigDecimal;
    }

    // 格式化为带国家符号的字符串
    public static String formatToCurrency(BigDecimal amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.CHINA);
        return currencyFormat.format(amount);
    }

}
