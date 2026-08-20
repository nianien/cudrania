package com.nianien.cudrania.core.date;

import com.nianien.cudrania.core.exception.ExceptionChecker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对日期进行字符串格式化的工具类.
 * <p>
 * 公开 API 仍以 {@link java.util.Date} + 模式串为契约, 但内部改用不可变、线程安全的
 * {@link DateTimeFormatter}(按 模式+Locale 缓存), 不再依赖 {@code SimpleDateFormat}+{@code ThreadLocal}.
 * <p>
 * 模式字母遵循 {@link java.time.format.DateTimeFormatter} 约定(与 SimpleDateFormat 基本一致:
 * y 年, M 月, d 日, H/h 时(24/12), m 分, s 秒, S 毫秒, a 上/下午, E 星期, z/Z 时区)。
 *
 * @author skyfalling
 */
public class DateFormatter {

    /**
     * 全局 Locale, {@link #setLocale} 更新后对后续调用生效(作为格式器缓存键的一部分)
     */
    private static volatile Locale locale = Locale.getDefault();
    /**
     * 时区: 用于 Date(时间戳) 与本地日期时间之间的换算
     */
    private static volatile ZoneId zone = ZoneId.systemDefault();
    /**
     * 按 "模式@Locale" 缓存不可变、线程安全的格式器, 免去每次重建/applyPattern
     */
    private static final ConcurrentHashMap<String, DateTimeFormatter> CACHE = new ConcurrentHashMap<>();

    private static DateTimeFormatter formatter(String pattern) {
        return CACHE.computeIfAbsent(pattern + "@" + locale.toLanguageTag(),
                k -> DateTimeFormatter.ofPattern(pattern, locale));
    }

    /**
     * 格式当前日期的默认格式：yyyy-MM-dd HH:mm:ss
     *
     * @return 格式化后的字符串
     */
    public static String now() {
        return format(new Date());
    }

    /**
     * 按指定格式pattern对当前日期进行格式化
     *
     * @param pattern
     * @return 格式化后的字符串
     */
    public static String now(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 格式化指定日期的默认格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return 格式化后的字符串
     */
    public static String format(Date date) {
        return format(date, DatePattern.Default);
    }

    /**
     * 将给定的日期字符串进行格式化,指定日期的字符串必须包含格式化所必须的信息
     *
     * @param source
     * @param pattern
     * @return 格式化后的字符串
     */
    public static String format(String source, String pattern) {
        return format(parseDate(source), pattern);
    }

    /**
     * 按指定格式pattern对指定日期date进行格式化
     *
     * @param date
     * @param pattern
     * @return 格式化后的字符串
     */
    public static String format(Date date, String pattern) {
        return formatter(pattern).format(date.toInstant().atZone(zone));
    }

    /**
     * 根据日期字符串获取日期,采用默认日期格式
     *
     * @param source
     * @return Date对象
     */
    public static Date parseDate(String source) {
        return parseDate(source, DatePattern.Default);
    }

    /**
     * 根据日期字符串获取日期,采用指定日期格式
     *
     * @param source
     * @param pattern
     * @return
     */
    public static Date parseDate(String source, String pattern) {
        return parse(source, pattern, false);
    }

    /**
     * 根据日期字符串获取日期,依次尝试多个日期格式,返回首个成功解析的结果
     *
     * @param source
     * @param patterns
     * @return
     */
    public static Date parseDate(String source, String... patterns) {
        for (String pattern : patterns) {
            Date date = parse(source, pattern, true);
            if (date != null) {
                return date;
            }
        }
        throw new IllegalArgumentException("date [" + source + "] must comply with the following format:" + Arrays.toString(patterns));
    }

    /**
     * 解析日期: 用DateTimeFormatter解析后提取日期/时间部分, 缺失的补默认值(纯日期→午夜, 纯时间→今天),
     * 复现SimpleDateFormat对不完整模式的宽松行为
     *
     * @param source
     * @param pattern
     * @param silently 为true时解析失败返回null, 否则抛出异常
     * @return
     */
    private static Date parse(String source, String pattern, boolean silently) {
        try {
            TemporalAccessor ta = formatter(pattern).parse(source);
            LocalDate date = ta.query(TemporalQueries.localDate());
            LocalTime time = ta.query(TemporalQueries.localTime());
            if (date == null) {
                date = LocalDate.now(zone);
            }
            if (time == null) {
                time = LocalTime.MIDNIGHT;
            }
            return Date.from(LocalDateTime.of(date, time).atZone(zone).toInstant());
        } catch (Exception e) {
            if (silently) {
                return null;
            }
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 设置Locale属性(影响后续格式化/解析)
     *
     * @param locale
     */
    public static void setLocale(Locale locale) {
        DateFormatter.locale = locale;
    }

    /**
     * 设置时区(影响Date与本地日期时间的换算)
     *
     * @param zone
     */
    public static void setZone(ZoneId zone) {
        DateFormatter.zone = zone;
    }
}
