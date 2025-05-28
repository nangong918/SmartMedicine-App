package com.czy.baseUtilsLib.logic;

import android.annotation.SuppressLint;
import android.util.Log;

import com.czy.baseUtilsLib.debug.DebugMyUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateInfoUtils {

    protected static final String TAG = DebugMyUtil.class.getSimpleName();

    /**
     * 获取时间戳
     *
     * @return 获取时间戳
     */
    public static long getDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    /**
     * 时间戳转换为字符串
     *
     * @param time:时间戳
     * @return  时间戳对应的字符串
     */
    public static String getDateToString(long time) {
        if (time == 0) {
            return "2009-01-01 01:00:00";
        }
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }


    public static String getCurFormatDate2() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(new Date());
    }

    public static Date parseDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "日期解析异常: " + dateString + "，原因: " + e.getMessage(), e);
        }
        return date;
    }

    public static LocalDate getTargetTime(Integer duration, int y, int m, int d) {

        // 创建开始时间的 LocalDate 对象
        LocalDate startDate = LocalDate.of(y, m, d);

        // 根据持续时间计算出结束时间
        return startDate.plusDays(duration == null ? 1 : duration);
    }

    public static LocalDate getTargetTime(Integer duration, LocalDate startDate) {
        // 根据持续时间计算出结束时间
        return startDate.plusDays(duration == null ? 1 : duration);
    }

    public static LocalDate parseDate(String dateString, LocalDate defaultData) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            return defaultData;
        }
    }

    public static int parseDayString(String dayString) {
        if (dayString == null || dayString.isEmpty()) {
            return 0;
        }

        // 去掉字符串末尾的"天"
        dayString = dayString.replaceAll("天", "");

        try {
            return Integer.parseInt(dayString);
        } catch (NumberFormatException e) {
            // 如果转换失败,返回0
            return 0;
        }
    }

    public static String extractDateString(String input) {
        int lastColonIndex = input.lastIndexOf("：");
        if (lastColonIndex == -1) {
            return "";
        }

        int dateStartIndex = lastColonIndex + 1;
        int dateEndIndex = input.length();

        return input.substring(dateStartIndex, dateEndIndex);
    }

}
