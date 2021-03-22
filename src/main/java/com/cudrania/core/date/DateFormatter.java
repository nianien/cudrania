package com.cudrania.core.date;

import com.cudrania.core.exception.ExceptionChecker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * 定义了以下模式字母（所有其他字符 'A' 到 'Z' 和 'a' 到 'z' 都被保留）：
 * <p>
 * //	字母  日期或时间元素  表示  示例
 * //	G  Era 标志符  Text  AD
 * //	y  年  Year  1996; 96
 * //	M  年中的月份  Month  July; Jul; 07
 * //	w  年中的周数  Number  27
 * //	W  月份中的周数  Number  2
 * //	D  年中的天数  Number  189
 * //	d  月份中的天数  Number  10
 * //	F  月份中的星期  Number  2
 * //	E  星期中的天数  Text  Tuesday; Tue
 * //	a  Am/pm 标记  Text  PM
 * //	H  一天中的小时数（0-23）  Number  0
 * //	k  一天中的小时数（1-24）  Number  24
 * //	K  am/pm 中的小时数（0-11）  Number  0
 * //	high  am/pm 中的小时数（1-12）  Number  12
 * //	m  小时中的分钟数  Number  30
 * //	s  分钟中的秒数  Number  55
 * //	S  毫秒数  Number  978
 * //	z  时区  General time zone  Pacific Standard Time; PST; GMT-08:00
 * //	Z  时区  RFC 822 time zone  -0800
 **/

/**
 * 对日期进行字符串格式化的工具类
 *
 * @author skyfalling
 */
public class DateFormatter {

    private static ThreadLocal<DateFormat> local = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            return new SimpleDateFormat();
        }
    };

    static {
        setLocale(Locale.getDefault());
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
        return format(date, "yyyy-MM-dd HH:mm:ss");
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
        SimpleDateFormat dateFormat = (SimpleDateFormat) local.get();
        dateFormat.applyPattern(pattern);
        return dateFormat.format(date);
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
     * 根据日期字符串获取日期,采用指定日期格式
     *
     * @param source
     * @param patterns
     * @return
     */
    public static Date parseDate(String source, String... patterns) {
        for (String pattern : patterns) {
            SimpleDateFormat dateFormat = (SimpleDateFormat) local.get();
            dateFormat.applyPattern(pattern);
            Date date = parse(source, pattern, true);
            if (date != null) {
                return date;
            }
        }
        throw new IllegalArgumentException("date [" + source + "] must comply with the following format:" + Arrays.toString(patterns));
    }


    /**
     * 解析日期
     *
     * @param source
     * @param pattern
     * @param silently
     * @return
     */
    private static Date parse(String source, String pattern, boolean silently) {
        try {
            SimpleDateFormat dateFormat = (SimpleDateFormat) local.get();
            dateFormat.applyPattern(pattern);
            return dateFormat.parse(source);
        } catch (ParseException e) {
            if (silently) {
                return null;
            }
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 设置Locale属性
     *
     * @param locale
     */
    public static void setLocale(Locale locale) {
        SimpleDateFormat dateFormat = (SimpleDateFormat) local.get();
        local.set(new SimpleDateFormat(dateFormat.toPattern(), locale));
    }
}
