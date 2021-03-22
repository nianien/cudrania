package com.cudrania.core.text;

import com.cudrania.core.arrays.ArrayUtils;
import com.cudrania.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提供正则表达式和通配符表达式匹配功能的工具类
 *
 * @author skyfalling
 */
public class RegexUtils {

    /**
     * 正则表达式中的转义字符
     */
    private final static char[] escapeChars = new char[]{'\\', '*', '+', '?',
            '|', '{', '[', '(', ')', '^', '$', '.', '#'};

    /**
     * 取消正则表达式中转义字符的转义功能
     *
     * @param regex
     * @return 取消转义后的正则表达式
     */
    public static String escape(String regex) {
        return Pattern.quote(regex);
    }

    /**
     * 取消正则表达式中chars字符的转义功能
     *
     * @param regex
     * @param chars
     * @return 取消转义后的正则表达式
     */
    public static String escape(String regex, char[] chars) {
        // 获取需要取消转义的字符数组
        StringBuilder sb = new StringBuilder();
        for (char ch : regex.toCharArray()) {
            // 需要转义的字符
            if (ArrayUtils.contains(chars, ch)) {
                sb.append('\\');
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * 取消正则表达式中除chars外转义字符的转义功能
     *
     * @param regex
     * @param chars
     * @return 取消转义后的正则表达式
     */
    public static String escapeExcept(String regex, char[] chars) {
        return escape(regex, /* 需要转义的字符 */
                ArrayUtils.subtract(escapeChars, chars));
    }

    /**
     * 搜索字符串source中匹配正则表达式regex的子串<br>
     *
     * @param source
     * @param regex
     * @return 匹配regex的字符串列表
     */
    public static List<String> find(String source, String regex) {
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            // 匹配的字符串
            list.add(matcher.group());
        }
        return list;
    }

    /**
     * 搜索字符串被left(不含)和right(不含)包围的子串<br>
     *
     * @param source
     * @param left
     * @param right
     * @return 被left和right包围的子串列表
     */
    public static List<String> find(String source, String left, String right) {
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(Pattern.quote(left) + "(.*?)"
                + Pattern.quote(right));
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            // 匹配的字符串
            list.add(matcher.group(1));
        }
        return list;
    }

    /**
     * 判断字符串input是否匹配正则表达式regex
     *
     * @param regex
     * @param input
     * @return 如果匹配返回true, 否则返回false
     */
    public static boolean matchRegex(String regex, String input) {
        return Pattern.matches(regex, input);
    }

    /**
     * 判断字符串input是否匹配正则表达式regex
     *
     * @param regex
     * @param input
     * @param ignoreCase 是否忽略大小写
     * @return 如果匹配返回true, 否则返回false
     */
    public static boolean matchRegex(String regex, String input,
                                     boolean ignoreCase) {
        return Pattern.matches(ignoreCase ? regexIgnoreCase(regex) : regex,
                input);
    }

    /**
     * 判断字符串input是否匹配通配符表达式wildcard
     *
     * @param wildcard
     * @param input
     * @return 如果匹配返回true, 否则返回false
     */
    public static boolean matchWildcard(String wildcard, String input) {
        return matchWildcard(wildcard, input, false);
    }

    /**
     * 判断字符串input是否匹配通配符表达式wildcard
     *
     * @param wildcard
     * @param input
     * @param ignoreCase 是否忽略大小写
     * @return 如果匹配返回true, 否则返回false
     */
    public static boolean matchWildcard(String wildcard, String input,
                                        boolean ignoreCase) {
        return matchRegex(regexForWildcard(wildcard), input, ignoreCase);
    }

    /**
     * 获取包含给定字符的正则表达式
     *
     * @param input
     * @return
     */
    public static String regexForContains(String input, boolean ignoreCase) {
        return (ignoreCase ? "(?i)" : "") + ".*" + input + ".*";
    }

    /**
     * 获取通配符表达式对应的正则表达式<br>
     * "*" 表示零个或多个字符,"?"表示零个或1个字符
     *
     * @param wildcard 通配符表达式
     * @return 通配符表达式对应的正则表达式
     */
    public static String regexForWildcard(String wildcard) {
        // 取消除通配符?,*之外的所有转义字符后再对?,*进行转义
        return escapeExcept(wildcard, new char[]{'?', '*'}).replaceAll(
                "\\?", ".?").replaceAll("\\*", ".*");
    }


    /**
     * 将指定的正则表达式嵌入(?i)标志,使其忽略大小写
     *
     * @param regex
     * @return
     */
    public static String regexIgnoreCase(String regex) {
        return "(?i)" + regex;
    }

    /**
     * 将在source字符串中正则式regex匹配部分作为key查询map中的value进行替换<br>
     * 如果map中不存在对应的key,则不进行替换<br>
     *
     * @param source
     * @param regex
     * @param replacement
     * @return 替换后的字符串
     */
    public static String replace(String source, String regex,
                                 Map<String, String> replacement) {
        return replace(source, regex, replacement, null);
    }


    /**
     * 将在source字符串中正则式regex匹配部分用replacement替换<br>
     *
     * @param source
     * @param regex
     * @param replacement
     * @return 替换后的字符串
     */
    public static String replace(String source, String regex,
                                 String replacement) {
        return replace(source, regex, Collections.<String, String>emptyMap(), StringUtils.defaultIfNull(replacement, ""));
    }

    /**
     * 将在source字符串中正则式regex匹配部分作为key查询map中的value进行替换<br>
     * 如果map中不存在对应的key,则用字符串instead替换,instead为null则不替换<br>
     *
     * @param source
     * @param regex
     * @param replacement 字符替换映射表,不可为null
     * @param instead     替换字符串,如果为null表示使用原
     * @return 替换后的字符串
     */
    public static String replace(String source, String regex,
                                 final Map<String, String> replacement, final String instead) {
        return replace(source, regex, matched -> replacement.containsKey(matched) ? replacement.get(matched) : instead);
    }


    /**
     * 调用{@link Function}&lt;{@link String},{@link String}>处理字符串source成功匹配正则式regex的部分, 将使用返回结果进行替换<br>
     *
     * @param source
     * @param regex
     * @param function
     * @return 替换后的字符串
     */
    public static String replace(String source, String regex,
                                 Function<String, String> function) {
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        // 匹配成功的字符串
        String matched = "";
        // 下一个匹配的起始索引
        int i = 0;
        while (matcher.find()) {
            matched = matcher.group();
            sb.append(source, i, matcher.start())
                    .append(function.apply(matched));
            i = matcher.end();
        }
        // 剩余未匹配的字符串
        sb.append(source.substring(i));
        return sb.toString();
    }

    /**
     * 将字符串source中成功匹配正则式regex的部分按在source中出现的顺序依次用参数parameters填充,
     * 直到不再有匹配regex的子串或者parameters的值用完<br>
     *
     * @param source
     * @param regex
     * @param parameters 用来填充匹配字符串的可变参数
     * @return 填充后的字符串<br>
     */
    public static String fill(String source, String regex, String... parameters) {
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        // 下一个匹配的起始索引
        int i = 0;
        for (String str : parameters) {
            if (!matcher.find())
                break;
            // 该次匹配之前和上次匹配的之后中间的子串
            sb.append(source, i, matcher.start()).append(str);
            i = matcher.end();
        }
        // 剩余未匹配的字符串
        sb.append(source.substring(i));
        return sb.toString();
    }
}
