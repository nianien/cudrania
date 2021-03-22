package com.cudrania.core.text;

import com.cudrania.core.comparator.StringComparator;

import java.util.Arrays;

/**
 * 高亮显示工具类,支持多个关键词的高亮显示,如果多个关键字之间存在包含关系,优先高亮显示较长的关键字
 *
 * @author skyfalling
 */
public class HighLighter {

    /**
     * 高亮显示的关键字列表
     */
    private String[] keywords;
    /**
     * 高亮显示时是否忽略大小写
     */
    private boolean ignoreCase;


    /**
     * 高亮显示时是否忽略大小写
     *
     * @return
     */
    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    /**
     * 设置高亮显示时是否忽略大小写
     *
     * @param ignoreCase
     */
    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    /**
     * 高亮显示的关键字列表
     *
     * @return
     */
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * 设置高亮显示的关键字列表
     *
     * @param keywords
     */
    public void setKeywords(String[] keywords) {
        Arrays.sort(keywords,StringComparator.LengthDesc);
        this.keywords = keywords;
    }

    /**
     * 构造函数
     *
     * @param keywords 需要高亮显示的关键字列表
     */
    public HighLighter(String[] keywords) {
        this(keywords, false);
    }


    /**
     * 构造函数
     *
     * @param keywords   需要高亮显示的关键字列表
     * @param ignoreCase 是否忽略大小写
     */
    public HighLighter(String[] keywords, boolean ignoreCase) {
        this.setKeywords(keywords);
        this.setIgnoreCase(ignoreCase);
    }

    /**
     * 高亮显示HTML正文中的关键字
     *
     * @param html HTML文本
     * @return
     */
    public String highLightHtml(String html) {

        /**
         * 正则表达式分割HTML标签和正文内容,匹配正则式的为标签,不匹配的为正文
         */
        return TextAnalyzer.rebuild(html, TextAnalyzer.analyze(html, "<.*?>"),
                (html1, fragment) -> {
                    String value = fragment.content(html1);
                    return fragment.isMatched() ? value
                            : highLightText(value);
                });
    }

    /**
     * 高亮显示纯文本中的关键字
     *
     * @param text 文本内容
     * @return
     */
    public String highLightText(String text) {

        return TextAnalyzer.rebuild(text,
                TextAnalyzer.analyze(text, keywords, ignoreCase),
                (text1, fragment) -> {
                    String value = fragment.content(text1);
                    return fragment.isMatched() ? highLight(value) : value;
                });
    }



    /**
     * 高亮显示关键字,重写该方法以实现不同的高亮模式
     *
     * @param keyword
     * @return
     */
    protected String highLight(String keyword) {
        return new StringBuilder().append("<em>").append(keyword).append("</em>").toString();
    }

}
