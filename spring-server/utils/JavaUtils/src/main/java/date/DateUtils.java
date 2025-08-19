package date;

import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 13225
 * @date 2025/8/19 10:05
 * Date             :Java 早期的日期和时间类，表示特定的时刻，精确到毫秒
 * LocalDate        :表示没有时间部分的日期（如年、月、日），不包含时区信息
 * LocalDateTime    :日期和时间
 * ZonedDateTime    :表示带有时区的日期和时间。它包含日期、时间、时区信息
 * OffsetDateTime   :表示带有时间偏移量（例如 UTC+8）的日期和时间
 */
public class DateUtils {

    @NonNull
    public static String yyyyMMddHHmmssToString(@NonNull LocalDateTime date){
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 转换 LocalDateTime 为格式化字符串
        return date.format(formatter);
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
    public static LocalDateTime getLocalDateTime(@NonNull Long timestamp) {
        return LocalDateTime.ofInstant(new java.util.Date(timestamp).toInstant(), ZoneId.systemDefault());
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
