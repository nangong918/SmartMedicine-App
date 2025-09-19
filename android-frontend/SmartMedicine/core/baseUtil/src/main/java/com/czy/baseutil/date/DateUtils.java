package com.czy.baseutil.date;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


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

    public static final DateTimeFormatter yyyyMMddHHmmss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @NonNull
    public static String yyyyMMddHHmmssToString(@NonNull LocalDateTime date){
        // 转换 LocalDateTime 为格式化字符串
        return date.format(yyyyMMddHHmmss);
    }

    @NonNull
    public static LocalDateTime getLocalDateTime(@NonNull String date, @NonNull DateTimeFormatter formatter) throws Exception {
        try {
            return LocalDateTime.parse(date, formatter);
        } catch (Exception e) {
            throw new Exception("Invalid date format: " + date, e);
        }
    }

    @NonNull
    public static LocalDateTime getLocalDateTimeByTimestamp(@NonNull Long timestamp) {
        return LocalDateTime.ofInstant(new java.util.Date(timestamp).toInstant(), ZoneId.systemDefault());
    }

    @NonNull
    public static String getDateStringByTimestamp(@NonNull Long timestamp) {
        LocalDateTime localDateTime = getLocalDateTimeByTimestamp(timestamp);
        return yyyyMMddHHmmssToString(localDateTime);
    }

    @NonNull
    public static Long getTimestamp(@NonNull LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @NonNull
    public static LocalDateTime getLocalDateTime(@NonNull Long timestamp, @NonNull String zoneId) {
        ZoneId zone = ZoneId.of(zoneId);
        return LocalDateTime.ofInstant(new java.util.Date(timestamp).toInstant(), zone);
    }

    @NonNull
    public static Long getTimestamp(@NonNull LocalDateTime localDateTime, @NonNull String zoneId) {
        ZoneId zone = ZoneId.of(zoneId);
        return localDateTime.atZone(zone).toInstant().toEpochMilli();
    }

    @NonNull
    public static ZonedDateTime getZonedDateTime(@NonNull Long timestamp) {
        return ZonedDateTime.ofInstant(new java.util.Date(timestamp).toInstant(), ZoneId.systemDefault());
    }

    @NonNull
    public static Long getTimestamp(@NonNull ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli();
    }

    @NonNull
    public static ZonedDateTime getZonedDateTime(@NonNull Long timestamp, @NonNull String zoneId) {
        ZoneId zone = ZoneId.of(zoneId);
        return ZonedDateTime.ofInstant(new java.util.Date(timestamp).toInstant(), zone);
    }


    public static void main(String[] args) {
        LocalDateTime localDateTime = getLocalDateTime(System.currentTimeMillis(), "Asia/Shanghai");
        System.out.println(yyyyMMddHHmmssToString(localDateTime));
    }
}
