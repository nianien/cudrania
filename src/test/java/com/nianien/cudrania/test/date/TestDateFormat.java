package com.nianien.cudrania.test.date;

import com.nianien.cudrania.core.date.DatePattern;
import com.nianien.cudrania.core.date.DateFormatter;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证 DateFormatter(内部已迁 java.time.DateTimeFormatter)对外 Date API 的行为。
 *
 * @author skyfalling
 */
public class TestDateFormat {

    @Test
    public void testFormatParseRoundTrip() {
        Date d = DateFormatter.parseDate("2012-12-12 13:45:30");
        // 默认格式 round-trip
        assertEquals("2012-12-12 13:45:30", DateFormatter.format(d));
        // 指定模式格式化
        assertEquals("2012-12-12", DateFormatter.format(d, "yyyy-MM-dd"));
        assertEquals("13:45:30", DateFormatter.format(d, "HH:mm:ss"));
    }

    @Test
    public void testPartialPatternDefaults() {
        // 纯日期解析 → 时间补午夜(复现 SimpleDateFormat 宽松行为)
        assertEquals("2012-12-12 00:00:00",
                DateFormatter.format(DateFormatter.parseDate("2012-12-12", "yyyy-MM-dd")));
    }

    @Test
    public void testMultiPatternFallback() {
        // 首个模式失败, 回退到第二个
        Date d = DateFormatter.parseDate("2013-01-02 08:09", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm");
        assertEquals("2013-01-02 08:09:00", DateFormatter.format(d));
    }

    @Test
    public void testTwelveHourPatternRoundTrip() {
        Date d = DateFormatter.parseDate("2012-12-12 13:45:30");
        // 经 12 小时制(含 a 上/下午)格式化再解析, 应还原(与 Locale 的 am/pm 文本无关)
        String s = DateFormatter.format(d, DatePattern.DateTime12);
        assertEquals("2012-12-12 13:45:30",
                DateFormatter.format(DateFormatter.parseDate(s, DatePattern.DateTime12)));
    }
}
