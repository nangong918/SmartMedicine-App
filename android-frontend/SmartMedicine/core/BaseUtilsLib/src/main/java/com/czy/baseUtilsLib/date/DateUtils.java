package com.czy.baseUtilsLib.date;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

// TODO 完善时间工具类
public class DateUtils {

    public static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return currentDate.format(formatter);
    }

    /**
     * 获取当前的年月日时分秒
     * @return 格式化的日期时间字符串，例如 "2024-11-27 14:30:45"
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime(Date data){
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(data);
    }

    public static String timestampToDate(long timestamp){
        Date date = new Date(timestamp);
        return getTime(date);
    }

}
