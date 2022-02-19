package com.cudrania.core.utils;

import com.cudrania.core.arrays.ArrayUtils;
import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.text.TextAnalyzer;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Locale.ENGLISH;

/**
 * 字符串工具类,支持针对字符串的多种扩展操作
 *
 * @author skyfalling
 */
public class StringUtils {

    /**
     * 判断参数parameters是否全部为空
     *
     * @param parameters
     * @return 如果参数parameters全部为空返回true, 否则返回false
     */
    public static boolean isAllEmpty(String... parameters) {
        for (String s : parameters) {
            if (isNotEmpty(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断parameters参数是否有为空的参数
     *
     * @param parameters
     * @return 如果参数parameters至少有一个为空, 返回true, 否则返回false
     */
    public static boolean isAnyEmpty(String... parameters) {
        for (String s : parameters) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 以指定的连接符进行字符串进行合并,被连接的位置如果有重复的连接符则只保留一个
     *
     * @param connector
     * @param parameters
     * @return 转换后的字符串
     */
    public static String combine(char connector, String... parameters) {
        return combine(connector + "", parameters);
    }

    /**
     * 以指定的连接符进行字符串合并,被连接的位置如果有重复的连接符则只保留一个
     *
     * @param connector
     * @param parameters
     * @return 转换后的字符串
     */
    public static String combine(String connector, String... parameters) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String param : parameters) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(connector);
            }
            sb.append(param);
        }
        return sb.toString().replaceAll(
                "(" + Pattern.quote(connector) + "){2,}", connector);
    }

    /**
     * 判断字符串source是否包含字符ch<br>
     *
     * @param source
     * @param ch
     * @return 如果source包含ch返回true, 否则返回false<br>
     */
    public static boolean contains(String source, char ch) {
        return source.indexOf(ch) > -1;
    }

    /**
     * 判断字符串source是否包含输入字符串input
     *
     * @param source
     * @param target
     * @param ignoreCase 是否忽略大小写
     * @return
     */
    public static boolean contains(String source, String target,
                                   boolean ignoreCase) {
        return ignoreCase ? source.toLowerCase().contains(target.toLowerCase())
                : source.contains(target);
    }

    /**
     * 判断字符串source是否包含字符数组array中的所有元素<br>
     *
     * @param source
     * @param array
     * @return 如果source包含字符数组array中的所有元素返回true, 否则返回false<br>
     */
    public static boolean containsAll(String source, char[] array) {
        for (char ch : array) {
            if (!contains(source, ch))
                return false;
        }
        return true;
    }

    /**
     * 判断字符串source是否包含参数中的所有值<br>
     *
     * @param source
     * @param parameters
     * @return 如果source包含参数parameters所有值返回true, 否则返回false<br>
     */
    public static boolean containsAll(String source, String... parameters) {
        for (String str : parameters) {
            if (!source.contains(str))
                return false;
        }
        return true;
    }

    /**
     * 判断字符串source是否包含字符数组array中的至少一个元素<br>
     *
     * @param source
     * @param array
     * @return 如果source包含array中的至少一个元素返回true, 否则返回false<br>
     */
    public static boolean containsAny(String source, char[] array) {
        for (char ch : array) {
            if (contains(source, ch))
                return true;
        }
        return false;
    }

    /**
     * 判断字符串source是否包含参数parameters中的至少一个值<br>
     *
     * @param source
     * @param parameters
     * @return 如果source包含参数parameters中的至少一个值返回true, 否则返回false<br>
     */
    public static boolean containsAny(String source, String... parameters) {
        for (String str : parameters) {
            if (source.contains(str))
                return true;
        }
        return false;
    }

    /**
     * 统计字符串source中字符ch出现的次数
     *
     * @param source
     * @param ch
     * @return 返回ch在source中的个数
     */
    public static int count(String source, char ch) {
        int num = 0;
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == ch)
                num++;
        }
        return num;
    }

    /**
     * 统计字符串source中字符串target出现的次数<br>
     *
     * @param source
     * @param target
     * @return 返回target在source中的个数<br>
     */
    public static int count(String source, String target) {
        int num = 0;
        while (true) {
            int index = source.indexOf(target);
            if (index == -1)
                break;
            num++;
            source = source.substring(index + target.length());
        }
        return num;
    }

    /**
     * 清除字符串source中出现的字符数组array中的元素<br>
     * 如果source为空或者字符数组元素为空则返回源字符串
     *
     * @param source
     * @param array
     * @return 处理后的字符串
     */
    public static String eliminate(String source, char[] array) {
        if (isEmpty(source) || array.length == 0)
            return source;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            if (!ArrayUtils.contains(array, source.charAt(i)))
                sb.append(source.charAt(i));
        }
        return sb.toString();
    }

    /**
     * 清除字符串source中出现的target字符串<br>
     * 如果source或者target为空则返回源字符串
     *
     * @param source
     * @param target
     * @return 处理后的字符串
     */
    public static String eliminate(String source, String target) {
        if (isAnyEmpty(source, target))
            return source;
        StringBuilder sb = new StringBuilder();
        while (true) {
            int index = source.indexOf(target);
            if (index == -1) {
                sb.append(source);
                break;
            }
            sb.append(source.substring(0, index));
            source = source.substring(index + target.length());
        }
        return sb.toString();
    }

    /**
     * 将字符target按在source中出现的顺序依次用参数数组parameters的元素填充,直到target不再出现或者参数用完<br/>
     *
     * @param source
     * @param target
     * @param parameters
     * @return 处理后的字符串
     */
    public static String fill(String source, char target, String... parameters) {
        if (parameters.length == 0)
            return source;
        StringBuilder sb = new StringBuilder();
        int n = 0;
        int i = 0;
        for (; i < source.length() && n < parameters.length; i++) {
            char ch = source.charAt(i);
            if (ch == target) {
                sb.append(parameters[n++]);
            } else {
                sb.append(ch);
            }
        }
        return sb.append(source.substring(i)).toString();
    }

    /**
     * 将字符串target按在source中出现的顺序依次用参数数组parameters的元素填充,直到target不再出现或者参数用完<br/>
     *
     * @param source
     * @param target
     * @param parameters
     * @return 处理后的字符串
     */
    public static String fill(String source, String target, String... parameters) {
        if (parameters.length == 0)
            return source;
        StringBuilder sb = new StringBuilder();
        int lastIndex = 0;
        int n = 0;
        while (lastIndex < source.length() && n < parameters.length) {
            int index = source.indexOf(target, lastIndex);
            if (index != -1) {
                sb.append(source.substring(lastIndex, index)).append(parameters[n]);
                lastIndex = index + target.length();
                n++;
            } else {
                sb.append(source.substring(lastIndex));
                lastIndex = source.length();
            }
        }
        return sb.append(source.substring(lastIndex)).toString();
    }

    /**
     * 当字符串长度小于指定宽度时,在前面使用空格补齐宽度
     *
     * @param source
     * @param width
     * @return 补齐后的字符串
     */
    public static String leftPad(String source, int width) {
        return leftPad(source, width, ' ');
    }

    /**
     * 当字符串长度小于指定宽度时,在前面使用指定字符补齐宽度
     *
     * @param source
     * @param width
     * @param ch
     * @return 补齐后的字符串
     */
    public static String leftPad(String source, int width, char ch) {
        int n = width - source.length();
        if (n <= 0)
            return source;
        return repeat(new StringBuilder(), ch, n).append(source).toString();
    }


    /**
     * 当字符串长度小于指定宽度时,在前面使用指定字符补齐宽度<br/>
     * 如果补齐字符串超过指定长度时,则对补齐字符串进行截断<br/>
     * <code>
     * <pre>
     * StringUtils.lefPad("!",6,"abc");// abcab!
     * </pre>
     * </code>
     *
     * @param source
     * @param width
     * @param str
     * @return 补齐后的字符串
     */
    public static String leftPad(String source, int width, String str) {
        int n = width - source.length();
        if (n <= 0)
            return source;

        return repeat(new StringBuilder(), str, n).append(source).toString();
    }


    /**
     * 当字符串长度小于指定宽度时,在后面使用空格补齐宽度
     *
     * @param source
     * @param width
     * @return 补齐后的字符串
     */
    public static String rightPad(String source, int width) {
        return rightPad(source, width, ' ');
    }

    /**
     * 当字符串长度小于指定宽度时,在后面使用指定字符补齐宽度
     *
     * @param source
     * @param width
     * @param ch
     * @return 补齐后的字符串
     */
    public static String rightPad(String source, int width, char ch) {
        int n = width - source.length();
        if (n <= 0)
            return source;
        return repeat(new StringBuilder(source), ch, n).toString();
    }

    /**
     * 当字符串长度小于指定宽度时,在后面使用指定字符补齐宽度<br/>
     * 如果补齐字符串超过指定长度时,则对补齐字符串进行截断<br/>
     * <code>
     * <pre>
     * StringUtils.lefPad("!",6,"abc");// !abcab
     * </pre>
     * </code>
     *
     * @param source
     * @param width
     * @param str
     * @return 补齐后的字符串
     */
    public static String rightPad(String source, int width, String str) {
        int n = width - source.length();
        if (n <= 0)
            return source;
        return repeat(new StringBuilder(source), str, n).toString();
    }

    /**
     * 重复字符串,不超过length长度
     *
     * @param sb
     * @param repeat
     * @param length
     * @return
     */
    private static StringBuilder repeat(StringBuilder sb, char repeat, int length) {
        int n = length;
        while (n > 0) {
            sb.append(repeat);
            n--;
        }
        return sb;
    }

    /**
     * 重复字符串,不超过length长度,超过进行截断
     *
     * @param sb
     * @param repeat
     * @param length
     * @return
     */
    private static StringBuilder repeat(StringBuilder sb, String repeat, int length) {
        int len = repeat.length();
        int n = length;
        while (n > 0) {
            if (n > len) {
                sb.append(repeat);
                n -= len;
            } else {
                sb.append(repeat, 0, n);
                n = 0;
            }
        }
        return sb;
    }

    /**
     * 开头字符小写
     *
     * @param source
     * @return 处理后的字符串
     */
    public static String headLower(String source) {
        if (isEmpty(source)) {
            return source;
        }
        return source.substring(0, 1).toLowerCase(ENGLISH)
                + source.substring(1);
    }

    /**
     * 开头字符大写
     *
     * @param source
     * @return 处理后的字符串
     */
    public static String headUpper(String source) {
        if (isEmpty(source)) {
            return source;
        }
        return source.substring(0, 1).toUpperCase(ENGLISH)
                + source.substring(1);
    }

    /**
     * 计算字符串source中第一次出现字符数组array中任意元素时的索引位置<br>
     *
     * @param source
     * @param array
     * @return array中元素首次出现的索引位置<br>
     * 如果array元素为空,返回-1
     */
    public static int indexOfAny(String source, char[] array) {
        if (array.length == 0) {
            return -1;
        }
        for (int i = 0; i < source.length(); i++) {
            if (ArrayUtils.contains(array, source.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 计算字符串source中第一次出现参数parameters中任一值时的索引位置<br>
     *
     * @param source
     * @param parameters
     * @return parameters中参数值首次出现的索引位置<br>
     * 如果未指定parameters,则返回-1
     */
    public static int indexOfAny(String source, String... parameters) {
        if (parameters.length == 0)
            return -1;
        // 最小索引值
        int low = source.length();
        for (String str : parameters) {
            int index = source.indexOf(str);
            if (index != -1 && index < low) {
                low = index;
            }
        }
        return low == source.length() ? -1 : low;
    }

    /**
     * 计算source字符串中第count次匹配pattern的子串索引位置<br>
     *
     * @param source
     * @param pattern
     * @param count
     * @return 第count次匹配pattern的子串索引位置<br>
     * 如果source包含pattern的个数小于count,返回-1<br>
     */
    public static int indexOfTimes(String source, String pattern, int count) {
        if (count <= 0)
            throw new IllegalArgumentException("参数:count必须大于0!");
        int i = 0 - pattern.length();
        while (count > 0) {
            // 从匹配的字符串之后开始新的匹配
            i = source.indexOf(pattern, i + pattern.length());
            if (i == -1) {
                break;
            }
            count--;
        }
        return i;
    }

    /**
     * 如果source为空则返回defaultValue,否则返回source
     *
     * @param source
     * @param defaultValue
     * @return
     */
    public static String defaultIfEmpty(String source, String defaultValue) {
        return isEmpty(source) ? defaultValue : source;
    }

    /**
     * 如果source为null则返回defaultValue,否则返回source
     *
     * @param source
     * @param defaultValue
     * @return
     */
    public static String defaultIfNull(String source, String defaultValue) {
        return source == null ? defaultValue : source;
    }

    /**
     * 如果source为null则返回"",否则返回source
     *
     * @param source
     * @return
     */
    public static String defaultString(String source) {
        return source == null ? "" : source;
    }

    /**
     * 判断字符串是否为空
     *
     * @param source
     * @return 如果字符串为null或"",返回true,否则返回false
     */
    public static boolean isEmpty(String source) {
        return source == null || source.isEmpty();
    }

    /**
     * 判断字符串是否为空白字符
     *
     * @param source
     * @return 如果字符串为空白字符, 返回true, 否则返回false
     */
    public static boolean isBlank(String source) {
        return source == null || source.trim().isEmpty();
    }

    /**
     * 用字符connector连接字符串集合parameters
     *
     * @param connector
     * @param parameters
     * @return 连接后的字符串
     */
    public static String join(char connector, String... parameters) {
        return join(connector + "", parameters);
    }

    /**
     * 用字符串connector连接字符串集合parameters
     *
     * @param connector
     * @param parameters
     * @return 连接后的字符串
     */
    public static String join(String connector, String... parameters) {
        return Arrays.stream(parameters).collect(Collectors.joining(connector));
    }

    /**
     * 用字符串connector连接字符串集合parameters
     *
     * @param connector
     * @param parameters
     * @return 连接后的字符串
     */
    public static String join(String connector, Iterable<String> parameters) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String parameter : parameters) {
            if (i++ > 0) {
                sb.append(connector);
            }
            sb.append(parameter);
        }
        return sb.toString();
    }

    /**
     * 计算字符串source中最后一次出现arrays中任一值的索引位置<br>
     *
     * @param source
     * @param array
     * @return array中元素的最后一次出现的索引位置<br>
     * 如果array元素为空,返回-1
     */
    public static int lastIndexOfAny(String source, char[] array) {
        if (array.length == 0) {
            return -1;
        }
        for (int i = source.length() - 1; i >= 0; i--) {
            if (ArrayUtils.contains(array, source.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 计算字符串source中最后一次出现parameters中任一值的索引位置<br>
     *
     * @param source
     * @param parameters
     * @return 返回parameters中参数值的最后一次出现的索引位置<br>
     * 如果未指定parameters参数,返回-1;
     */
    public static int lastIndexOfAny(String source, String... parameters) {
        if (parameters.length == 0)
            return -1;
        // 最大索引
        int high = -1;
        for (String str : parameters) {
            int index = source.indexOf(str);
            if (index > high) {
                high = index;
            }
        }
        return high;

    }

    /**
     * 计算source字符串倒数第count次匹配pattern字符串的索引位置<br>
     *
     * @param source
     * @param target
     * @param count
     * @return 倒数第count次匹配pattern的子串索引位置<br>
     * 如果source包含pattern的个数小于count,返回-1<br>
     * 如果pattern为空,返回-1<br>
     */
    public static int lastIndexOfTimes(String source, String target, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count cannot be less than 0!");
        }
        int i = -1;
        String subString = source;
        while (count > 0) {
            i = subString.lastIndexOf(target);
            if (i == -1) {
                break;
            }
            // 取索引之前的子字符串
            subString = source.substring(0, i);
            count--;
        }
        return i;
    }

    /**
     * 判断参数parameters的值是否全部不为空
     *
     * @param parameters
     * @return 如果参数parameters的值全部不为空, 返回true, 否则返回false
     */
    public static boolean isNoneEmpty(String... parameters) {
        return !isAnyEmpty(parameters);
    }

    /**
     * 判断字符串source是否非空
     *
     * @param source
     * @return 如果字符串不为null和"",返回true,否则返回false.
     */
    public static boolean isNotEmpty(String source) {
        return !isEmpty(source);
    }

    /**
     * 判断字符串source是否空白字符
     *
     * @param source
     * @return 如果字符串不为空白字符, 返回true, 否则返回false.
     */
    public static boolean isNotBlank(String source) {
        return !isBlank(source);
    }

    /**
     * 将字符转换成boolean值,如果转换失败,返回false
     *
     * @param str
     * @return 返回字符串对应的布尔值
     */
    public static boolean parseBoolean(String str) {
        return parseBoolean(str, false);
    }

    /**
     * 将字符转换成boolean值,如果转换失败,返回默认值
     *
     * @param str
     * @param defaultValue
     * @return 返回字符串对应的布尔值
     */
    public static boolean parseBoolean(String str, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将字符转换成int值,如果转换失败,返回0
     *
     * @param str
     * @return 返回字符串对应的int值
     */
    public static int parseInt(String str) {
        return parseInt(str, 0);
    }

    /**
     * 将字符转换成int值,如果转换失败,返回默认值
     *
     * @param str
     * @return 返回字符串对应的int值
     */
    public static int parseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 将字符串source中的target字符用replacement来替换<br>
     *
     * @param source
     * @param target
     * @param replacement
     * @return 处理后的字符串<br>
     * 如果source为空,则返回源字符串<br>
     */
    public static String replace(String source, char target, String replacement) {
        if (isEmpty(source))
            return source;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char ch = source.charAt(i);
            sb.append(ch == target ? replacement : ch);
        }
        return sb.toString();

    }

    /**
     * 将字符数组targets在字符串source中出现的元素替换成replacement数组对应位置的元素<br>
     *
     * @param source
     * @param targets      待替换的字符集合
     * @param replacements 用来替换的字符串集合
     * @return 替换后的字符串. 如果source为空, 则返回源字符串<br>
     */
    public static String replace(String source, char[] targets,
                                 String[] replacements) {
        if (targets.length != replacements.length)
            throw new IllegalArgumentException("targets and replacements lengths don't match: " + targets.length + " and " + replacements.length + "!");
        String targetStr = new String(targets);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char ch = source.charAt(i);
            int index = targetStr.indexOf(ch);
            sb.append(ArrayUtils.contains(targets, ch) ? replacements[index]
                    : ch);
        }
        return sb.toString();
    }

    /**
     * 将map在文本text中出现的key值替换成对应的value值<br>
     * 如果key的值具有包含关系,则优先替换较大的字符串
     *
     * @param text
     * @param map  key对应待替换的字符串,value对应用来替换的字符串
     * @return
     */
    public static String replace(String text, Map<String, String> map) {
        return TextAnalyzer.replace(text, map);
    }

    /**
     * 将字符串source中成功匹配正则式regex的部分用replacement替换<br>
     *
     * @param source
     * @param regex
     * @param replacement
     * @return 替换后的字符串
     */
    public static String replace(String source, String regex, String replacement) {
        return RegexUtils.replace(source, regex, replacement);
    }

    /**
     * 将在source字符串中匹配正则式regex的部分用map对应key的value值替换<br>
     * 如果map中不存在对应的key,则不进行替换<br>
     *
     * @param source
     * @param regex
     * @param map
     * @return 替换后的字符串
     */
    public static String replace(String source, String regex,
                                 Map<String, String> map) {
        return RegexUtils.replace(source, regex, map);
    }

    /**
     * 将在source字符串中匹配正则式regex的部分用map对应key的value值替换<br>
     * 如果map中不存在对应的key,则用字符串instead替换匹配成功的字符串<br>
     *
     * @param source
     * @param regex
     * @param map
     * @param instead
     * @return 替换后的字符串
     */
    public static String replace(String source, String regex,
                                 Map<String, String> map, String instead) {
        return RegexUtils.replace(source, regex, map, instead);
    }

    /**
     * 将指定字符小写
     *
     * @param ch
     * @return 返回小写字符
     */
    public static char toLower(char ch) {
        return Character.toLowerCase(ch);
    }

    /**
     * 字符串小写
     *
     * @param ch
     * @return 小写字符串
     */
    public static String toLower(String ch) {
        return ch.toLowerCase(Locale.ENGLISH);
    }

    /**
     * 将指定字符大写
     *
     * @param ch
     * @return 返回大写字符
     */
    public static char toUpper(char ch) {
        return Character.toUpperCase(ch);
    }

    /**
     * 字符串大写
     *
     * @param source
     * @return 小写字符大写
     */
    public static String toUpper(String source) {
        return source.toUpperCase(Locale.ENGLISH);
    }

    /**
     * 字符串编码,即以encode格式编码
     *
     * @param source
     * @param encode
     * @return 转码后的字符串
     */
    public static String transCode(String source, String encode) {
        try {
            return new String(source.getBytes(), encode);
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 字符串转码,即先以decode格式解码再以encode格式编码
     *
     * @param source
     * @param decode
     * @param encode
     * @return 转码后的字符串
     */
    public static String transCode(String source, String decode, String encode) {
        try {
            return new String(source.getBytes(decode), encode);
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 清除字符串source两边的空白字符
     *
     * @param source
     * @return 处理后的字符串<br>
     */
    public static String trim(String source) {
        return source == null ? null : source.trim();
    }

    /**
     * 清除字符串string两边出现的字符数组array中的元素
     *
     * @param source
     * @param array
     * @return 处理后的字符串<br>
     * 如果array元素为空,则返回源字符串
     */
    public static String trim(String source, char[] array) {
        return trimRight(trimLeft(source, array), array);
    }

    /**
     * 清除字符串source两边出现的字符串target
     *
     * @param source
     * @param target
     * @return 处理后的字符串<br>
     * 如果target为空,则返回源字符串
     */
    public static String trim(String source, String target) {
        String pattern = Pattern.quote(target);
        String left = "^(" + pattern + ")+";
        String right = "(" + pattern + ")+$";
        return source.replaceAll(left, "").replaceAll(right, "");
    }

    /**
     * 清除字符串string左边出现的字符数组array中的元素<br>
     *
     * @param source
     * @param array
     * @return 处理后的字符串<br>
     * 如果array元素为空,返回源字符串
     */
    public static String trimLeft(String source, char[] array) {
        if (isEmpty(source) || array.length == 0)
            return source;
        int index = 0;
        for (int i = 0; i < source.length(); i++) {
            char ch = source.charAt(i);
            if (!ArrayUtils.contains(array, ch)) {
                index = i;
                break;
            }
        }
        return source.substring(index);
    }

    /**
     * 清除字符串source左边出现的字符串target<br>
     *
     * @param source
     * @param target
     * @return 处理后的字符串<br>
     * 如果target为空,则返回源字符串
     */
    public static String trimLeft(String source, String target) {
        String pattern = "^(" + Pattern.quote(target) + ")+";
        return source.replaceAll(pattern, "");
    }

    /**
     * 清除字符串source右边出现的字符数组array中的元素<br>
     *
     * @param source
     * @param array
     * @return 处理后的字符串<br>
     * 如果array元素为空,返回源字符串
     */
    public static String trimRight(String source, char[] array) {
        if (isEmpty(source) || array.length == 0)
            return source;
        int index = 0;
        for (int i = source.length() - 1; i >= 0; i--) {
            char ch = source.charAt(i);
            if (!ArrayUtils.contains(array, ch)) {
                index = i;
                break;
            }
        }
        return source.substring(0, index + 1);
    }

    /**
     * 清除字符串source右边出现的字符串target<br>
     *
     * @param source
     * @param target
     * @return 处理后的字符串<br>
     * 如果source或target为空,则返回源字符串.
     */
    public static String trimRight(String source, String target) {
        String pattern = "(" + Pattern.quote(target) + ")+$";
        return source.replaceAll(pattern, "");
    }


    // SubStringAfter/SubStringBefore
    //-----------------------------------------------------------------------

    /**
     * <p>Gets the substring before the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * A {@code null} separator will return the input string.</p>
     *
     * <p>If nothing is found, the string input is returned.</p>
     *
     * <pre>
     * StringUtils.substringBefore(null, *)      = null
     * StringUtils.substringBefore("", *)        = ""
     * StringUtils.substringBefore("abc", "a")   = ""
     * StringUtils.substringBefore("abcba", "b") = "a"
     * StringUtils.substringBefore("abc", "c")   = "ab"
     * StringUtils.substringBefore("abc", "d")   = "abc"
     * StringUtils.substringBefore("abc", "")    = ""
     * StringUtils.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring before the first occurrence of the separator,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String substringBefore(final String str, final String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return "";
        }
        final int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>Gets the substring after the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * A {@code null} separator will return the empty string if the
     * input string is not {@code null}.</p>
     *
     * <p>If nothing is found, the empty string is returned.</p>
     *
     * <pre>
     * StringUtils.substringAfter(null, *)      = null
     * StringUtils.substringAfter("", *)        = ""
     * StringUtils.substringAfter(*, null)      = ""
     * StringUtils.substringAfter("abc", "a")   = "bc"
     * StringUtils.substringAfter("abcba", "b") = "cba"
     * StringUtils.substringAfter("abc", "c")   = ""
     * StringUtils.substringAfter("abc", "d")   = ""
     * StringUtils.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring after the first occurrence of the separator,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String substringAfter(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return "";
        }
        final int pos = str.indexOf(separator);
        if (pos == -1) {
            return "";
        }
        return str.substring(pos + separator.length());
    }

    /**
     * <p>Gets the substring before the last occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * An empty or {@code null} separator will return the input string.</p>
     *
     * <p>If nothing is found, the string input is returned.</p>
     *
     * <pre>
     * StringUtils.substringBeforeLast(null, *)      = null
     * StringUtils.substringBeforeLast("", *)        = ""
     * StringUtils.substringBeforeLast("abcba", "b") = "abc"
     * StringUtils.substringBeforeLast("abc", "c")   = "ab"
     * StringUtils.substringBeforeLast("a", "a")     = ""
     * StringUtils.substringBeforeLast("a", "z")     = "a"
     * StringUtils.substringBeforeLast("a", null)    = "a"
     * StringUtils.substringBeforeLast("a", "")      = "a"
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring before the last occurrence of the separator,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String substringBeforeLast(final String str, final String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return str;
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>Gets the substring after the last occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * An empty or {@code null} separator will return the empty string if
     * the input string is not {@code null}.</p>
     *
     * <p>If nothing is found, the empty string is returned.</p>
     *
     * <pre>
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring after the last occurrence of the separator,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String substringAfterLast(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return "";
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == str.length() - separator.length()) {
            return "";
        }
        return str.substring(pos + separator.length());
    }


    /**
     * 连接符转驼峰Camel(".","-","_")
     *
     * @param src
     * @return
     */
    public static String camelCase(String src) {
        String dest = pascalCase(src);
        return dest.substring(0, 1).toLowerCase() + dest.substring(1);
    }

    /**
     * 连接符转帕斯卡Pascal(".","-","_")
     *
     * @param src
     * @return
     */
    public static String pascalCase(String src) {
        String separators = "_-.";
        StringBuilder sb = new StringBuilder();
        boolean needToUpper = false;
        for (char ch : src.toCharArray()) {
            if (separators.indexOf(ch) != -1) {
                needToUpper = true;
            } else if (Character.isLetter(ch)) {
                if (needToUpper) {
                    sb.append(Character.toUpperCase(ch));
                    needToUpper = false;
                } else {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }


    /**
     * 驼峰或帕斯卡转连接符(-)
     *
     * @param src
     * @return
     */
    public static String hyphenCase(String src) {
        return fromCamelOrPascal(src, '-');
    }

    /**
     * 驼峰或帕斯卡转下划线(_)
     *
     * @param src
     * @return
     */
    public static String underscoreCase(String src) {
        return fromCamelOrPascal(src, '_');
    }

    /**
     * 驼峰或帕斯卡转连接符
     *
     * @param src
     * @param separator
     * @return
     */
    private static String fromCamelOrPascal(String src, char separator) {
        StringBuilder sb = new StringBuilder();
        char[] chs = src.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            if (Character.isUpperCase(chs[i]) && i > 0) {
                sb.append(separator);
            }
            sb.append(Character.toLowerCase(chs[i]));
        }
        return sb.toString();
    }


}
