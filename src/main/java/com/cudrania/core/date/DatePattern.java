package com.cudrania.core.date;

/**
 * 预定义的日期格式化字符串
 *
 * @author skyfalling
 */
public final class DatePattern {
    /**
     * 日期时间(24)星期
     */
    public static final String DateTime24Week = "yyyy-MM-dd HH:mm:ss E";
    /**
     * 日期时间(12)星期
     */
    public static final String DateTime12Week = "yyyy-MM-dd ah:mm:ss E";
    /**
     * 日期
     */
    public static final String Date = "yyyy-MM-dd";
    /**
     * 日期时间(24)
     */
    public static final String DateTime24 = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日期时间(12)
     */
    public static final String DateTime12 = "yyyy-MM-dd ah:mm:ss";
    /**
     * 24小时制
     */
    public static final String Time24 = "HH:mm:ss";
    /**
     * 12小时制
     */
    public static final String Time12 = "ah:mm:ss";

    /**
     * 默认日期格式
     */
    public static final String Default = DateTime24;
}
