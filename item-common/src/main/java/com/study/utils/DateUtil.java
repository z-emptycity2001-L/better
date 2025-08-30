package com.study.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class DateUtil {
    // 常用日期时间格式
    public static final String PATTERN_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_COMPACT = "yyyyMMddHHmmss";
    public static final String PATTERN_CHINESE = "yyyy年MM月dd日 HH时mm分ss秒";

    // 线程安全的日期格式化器缓存
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new HashMap<>();
    private static final Map<String, ThreadLocal<SimpleDateFormat>> SDF_CACHE = new HashMap<>();

    static {
        // 初始化常用格式化器
        FORMATTER_CACHE.put(PATTERN_FULL, DateTimeFormatter.ofPattern(PATTERN_FULL));
        FORMATTER_CACHE.put(PATTERN_DATE, DateTimeFormatter.ofPattern(PATTERN_DATE));
        FORMATTER_CACHE.put(PATTERN_TIME, DateTimeFormatter.ofPattern(PATTERN_TIME));
        FORMATTER_CACHE.put(PATTERN_COMPACT, DateTimeFormatter.ofPattern(PATTERN_COMPACT));
        FORMATTER_CACHE.put(PATTERN_CHINESE, DateTimeFormatter.ofPattern(PATTERN_CHINESE));
    }

    /**
     * 获取指定格式的当前时间字符串
     * @param pattern 时间格式
     * @return 格式化后的时间字符串
     */
    public static String getCurrentTime(String pattern) {
        return LocalDateTime.now().format(getFormatter(pattern));
    }

    /**
     * 获取默认格式(yyyy-MM-dd HH:mm:ss)的当前时间
     * @return 格式化后的时间字符串
     */
    public static String getCurrentTime() {
        return getCurrentTime(PATTERN_FULL);
    }

    /**
     * 将LocalDateTime格式化为指定格式的字符串
     * @param dateTime  LocalDateTime对象
     * @param pattern   时间格式
     * @return 格式化后的时间字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(getFormatter(pattern));
    }

    /**
     * 将Date格式化为指定格式的字符串
     * @param date      Date对象
     * @param pattern   时间格式
     * @return 格式化后的时间字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return getSdf(pattern).get().format(date);
    }

    /**
     * 解析字符串为LocalDateTime
     * @param timeStr   时间字符串
     * @param pattern   时间格式
     * @return LocalDateTime对象
     */
    public static LocalDateTime parseLocalDateTime(String timeStr, String pattern) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(timeStr, getFormatter(pattern));
    }

    /**
     * 解析字符串为Date
     * @param timeStr   时间字符串
     * @param pattern   时间格式
     * @return Date对象
     * @throws ParseException 解析异常
     */
    public static Date parseDate(String timeStr, String pattern) throws ParseException {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        return getSdf(pattern).get().parse(timeStr);
    }

    /**
     * Date转LocalDateTime
     * @param date Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * LocalDateTime转Date
     * @param localDateTime LocalDateTime对象
     * @return Date对象
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取线程安全的DateTimeFormatter
     * @param pattern 时间格式
     * @return DateTimeFormatter
     */
    private static DateTimeFormatter getFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern, k -> DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取线程安全的SimpleDateFormat
     * @param pattern 时间格式
     * @return ThreadLocal<SimpleDateFormat>
     */
    private static ThreadLocal<SimpleDateFormat> getSdf(String pattern) {
        return SDF_CACHE.computeIfAbsent(pattern, k -> ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern)));
    }
}
