package com.cudrania.core.text;

import com.cudrania.core.arrays.ArrayUtils;
import com.cudrania.core.collection.CollectionUtils;
import com.cudrania.core.comparator.StringComparator;
import com.cudrania.core.utils.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本解析类,根据一定的规则将文本切分成不同的片断,每个片断包含在原文本中的起止索引位置以及是否匹配指定条件等信息<br>
 *
 * @author skyfalling
 */
public class TextAnalyzer {
    /**
     * 文本切分片断,包含该片断在原文本中的起止索引位置,以及是否匹配指定条件等信息<br>
     * 该对象不存储片断的文本内容,必须依赖于原文本才能获取片断内容
     *
     * @author skyfalling
     */
    public static class Fragment {
        private int begin;
        private int end;
        private boolean matched;

        /**
         * 构造函数
         *
         * @param begin   在原文本中的起始索引
         * @param end     在原文本中的结束索引
         * @param matched 是否匹配指定条件
         */
        public Fragment(int begin, int end, boolean matched) {
            this.begin = begin;
            this.end = end;
            this.matched = matched;
        }

        /**
         * 在原文本中的起始索引
         *
         * @return
         */
        public int begin() {
            return begin;
        }

        /**
         * 根据原始文本获取切分片断的内容
         *
         * @param text
         * @return
         */
        public String content(String text) {
            return text.substring(begin, end);
        }

        /**
         * 在原文本中的结束索引
         *
         * @return
         */
        public int end() {
            return this.end;
        }

        /**
         * 是否匹配指定条件
         *
         * @return
         */
        public boolean isMatched() {
            return matched;
        }

        @Override
        public String toString() {
            return "[" + begin + ":" + end() + "|" + matched + "]";
        }

        public String toString(String text) {
            return this + content(text);
        }

    }

    /**
     * Fragment对象处理类
     *
     * @author skyfalling
     */
    public interface FragmentHandler {

        /**
         * 根据切分片断并参照原文本对切分片断进行处理
         *
         * @param text     原文本内容
         * @param fragment 文本切分片断
         * @return
         */
        String handle(String text, Fragment fragment);
    }

    /**
     * 根据指定的字符集合,将文本内容按照是否属于集合元素切分成不同的片断,这些片断要么属于集合元素,要么不包含任何集合元素<br>
     * 对于每个切分片断Fragment对象,如果属于集合元素,则方法isMatched()返回tue,否则返回false
     *
     * @param text    原文本内容
     * @param targets 用于切分文本的字符集合
     * @return
     */
    public static List<Fragment> analyze(String text, char[] targets) {
        List<Fragment> list = new ArrayList<>();
        int lastIndex = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ArrayUtils.contains(targets, ch)) {
                if (i > lastIndex) {
                    // 上次匹配与当前匹配位置之间的文本
                    list.add(new Fragment(lastIndex, i, false));
                }
                // 匹配字符
                list.add(new Fragment(i, i + 1, true));
                lastIndex = i + 1;
            }
        }
        // 剩余的为匹配的文本
        if (lastIndex < text.length()) {
            list.add(new Fragment(lastIndex, text.length(), false));
        }
        return list;
    }

    /**
     * 根据正则表达式,将文本内容按照是否匹配正则式切分成不同的片断,这些片断要么完全匹配正则表达式,要么不包含任何匹配的部分<br>
     * 对于每个切分片断Fragment对象,如果匹配正则表达式,则方法isMatched()返回tue,否则返回false
     *
     * @param text  原文本内容
     * @param regex 用于切分文本的正则表达式
     * @return
     */
    public static List<Fragment> analyze(String text, String regex) {
        List<Fragment> list = new ArrayList<>();
        if (regex == null || regex.isEmpty()) {
            list.add(new Fragment(0, text.length(), false));
            return list;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        // 下一个匹配的起始索引
        int i = 0;
        while (matcher.find()) {
            // 未匹配正则表式的文本
            if (i < matcher.start()) {
                list.add(new Fragment(i, matcher.start(), false));
            }
            // 匹配正则表式的文本
            list.add(new Fragment(matcher.start(), matcher.end(), true));
            i = matcher.end();
        }
        // 剩余未匹配正则表式的文本
        if (i < text.length()) {
            list.add(new Fragment(i, text.length(), false));
        }
        return list;
    }

    /**
     * 根据给定的字符串集合,将文本内容按照是否属于集合元素切分成不同的片断,这些片断中属于集合元素,要么不包含任何集合元素<br>
     * 对于每个切分片断Fragment对象,如果属于集合元素,则方法isMatched()返回tue,否则返回false<br>
     * 需要注意的是,切分文本时是按照集合元素的遍历顺序进行切分的,因此,先被访问到元素将优先被切分出来<br>
     * 因此,如果切分的集合元素存在优先顺序的话,应在文本切分前按照切分优先级对集合元素进行排序
     *
     * @param text       原文本内容
     * @param targets    用于切分文本的字符串集合
     * @param ignoreCase 是否忽略大小写
     * @return
     */
    public static List<Fragment> analyze(String text, Iterable<String> targets,
                                         boolean ignoreCase) {
        if (ignoreCase) {
            text = text.toLowerCase();
        }
        targets = filterEmpty(targets, ignoreCase);
        List<Fragment> list = new ArrayList<>();
        list.add(new Fragment(0, text.length(), false));
        for (String target : targets) {
            List<Fragment> tempList = new ArrayList<>();
            for (Fragment fragment : list) {
                if (fragment.isMatched()) {
                    tempList.add(fragment);
                } else {
                    tempList.addAll(analyze0(fragment.content(text), target,
                            fragment.begin));
                }
            }
            list = tempList;
        }
        return list;
    }

    /**
     * 根据给定的字符串集合,将文本内容按照是否属于集合元素切分成不同的片断,这些片断中属于集合元素,要么不包含任何集合元素<br>
     * 对于每个切分片断Fragment对象,如果属于集合元素,则方法isMatched()返回tue,否则返回false<br>
     * 需要注意的是,切分文本时是按照集合元素的遍历顺序进行切分的,因此,先被访问到元素将优先被切分出来<br>
     * 因此,如果切分的集合元素存在优先顺序的话,应在文本切分前按照切分优先级对集合元素进行排序
     *
     * @param text       原文本内容
     * @param targets    用于切分文本的字符串集合
     * @param ignoreCase 是否忽略大小写
     * @return
     */
    public static List<Fragment> analyze(String text, String[] targets,
                                         boolean ignoreCase) {
        return analyze(text, Arrays.asList(targets), ignoreCase);
    }

    /**
     * 按照字符串在文本中出现的顺序进行切分
     *
     * @param text
     * @param targets
     * @param ignoreCase
     * @return
     */
    public static List<Fragment> analyzeOccur(String text,
                                              Iterable<String> targets, boolean ignoreCase) {
        // 忽略大小写
        if (ignoreCase) {
            text = text.toLowerCase();
        }
        targets = filterEmpty(targets, ignoreCase);
        List<Fragment> list = new ArrayList<>();
        int lastIndex = 0;
        while (lastIndex < text.length()) {
            //最先出现文本片段的索引位置
            int low = text.length();
            int length = 0;
            //求最先出现文本片段的索引位置
            for (String target : targets) {
                int index = text.indexOf(target, lastIndex);
                if (index != -1 && index < low) {
                    low = index;
                    length = target.length();
                }
            }
            if (low > lastIndex)
                list.add(new Fragment(lastIndex, low, false));
            if (low < text.length())
                list.add(new Fragment(low, low + length, true));
            lastIndex = low + length;
        }
        return list;
    }

    /**
     * 按照字符串在文本中出现的顺序进行切分
     *
     * @param text
     * @param targets
     * @param ignoreCase
     * @return
     */
    public static List<Fragment> analyzeOccur(String text, String[] targets,
                                              boolean ignoreCase) {
        return analyzeOccur(text, Arrays.asList(targets), ignoreCase);
    }

    /**
     * 将map在文本text中出现的key值替换成对应的value值<br>
     * 如果key的值具有包含关系,则优先替换较大的字符串
     *
     * @param text
     * @param map  key对应待替换的字符串,value对应用来替换的字符串
     * @return
     */
    public static String replace(String text, final Map<String, String> map) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        if (map.containsKey(text)) {
            return map.get(text);
        }
        List<String> list = CollectionUtils.list(map.keySet());
        Collections.sort(list, StringComparator.LengthDesc);
        return rebuild(text, analyze(text, list, false), (s, fragment) -> {
            String value = fragment.content(s);
            return fragment.isMatched() ? map.get(value) : value;
        });
    }

    /**
     * 根据文本切分片断列表重新生成文本内容<br>
     * 具体过程是依次遍历片断列表,对每个片断对象的处理结果进行字符串拼接
     *
     * @param text      原文本内容
     * @param fragments 切分片断列表
     * @param handler   切分片断处理器
     * @return
     */
    public static String rebuild(String text, Iterable<Fragment> fragments,
                                 FragmentHandler handler) {
        StringBuilder sb = new StringBuilder();
        for (Fragment fragment : fragments) {
            sb.append(handler.handle(text, fragment));
        }
        return sb.toString();
    }

    /**
     * 将切分片断列表转换成适合分析的文本形式
     *
     * @param text      原文本内容
     * @param fragments 切分片断列表
     * @return
     */
    public static String toString(String text, Iterable<Fragment> fragments) {
        StringBuilder sb = new StringBuilder();
        for (Fragment fragment : fragments) {
            sb.append(fragment.toString(text)).append("\n");
        }
        return sb.toString();
    }

    /**
     * 根据是否匹配指定字符串,将文本切分片断再次切分成不同的子片断,并根据原片断在文本中的偏移量修正子片断的起止索引位置
     *
     * @param text   文本切分片断的内容
     * @param target 指定匹配的字符串
     * @param offset 切分片断在原文本中的位置
     * @return
     */
    private static List<Fragment> analyze0(String text, String target, int offset) {
        //当前索引位置
        int thisIndex = 0;
        //上次索引位置
        int lastIndex = 0;
        List<Fragment> list = new ArrayList<Fragment>();
        while ((thisIndex = text.indexOf(target, lastIndex)) != -1) {
            // 未匹配的部分
            if (thisIndex > lastIndex) {
                list.add(new Fragment(lastIndex + offset, thisIndex + offset, false));
            }
            // 匹配的部分
            list.add(new Fragment(thisIndex + offset, thisIndex + offset
                    + target.length(), true));
            lastIndex = thisIndex + target.length();
        }
        // 剩余未匹配的部分
        if (text.length() > lastIndex) {
            list.add(new Fragment(lastIndex + offset, text.length() + offset,
                    false));
        }
        return list;
    }


    /**
     * 过滤空的文本
     *
     * @param targets
     * @param ignoreCase
     * @return
     */
    private static List<String> filterEmpty(Iterable<String> targets,
                                            boolean ignoreCase) {
        List<String> list = new ArrayList<>();
        for (String target : targets) {
            if (StringUtils.isNotEmpty(target)) {
                list.add(ignoreCase ? target.toLowerCase() : target);
            }
        }
        return list;
    }

}
