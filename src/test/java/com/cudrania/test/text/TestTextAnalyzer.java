package com.cudrania.test.text;

import com.cudrania.core.text.HighLighter;
import com.cudrania.core.text.TextAnalyzer;
import com.cudrania.core.text.TextAnalyzer.Fragment;
import com.cudrania.core.utils.RegexUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author skyfalling
 */
public class TestTextAnalyzer {


    @Test
    public void test() {
        String html = "<resource><directory>../../un-core/src/main/resources</directory><targetPath>WEB-INF/classes</targetPath></resource>";

        List<Fragment> list = TextAnalyzer.analyze(html, "<.*?>");
        System.out.println(TextAnalyzer.toString(html, list));
        String result = TextAnalyzer.rebuild(html, list, (text, fragment) -> !fragment.isMatched() ? text.substring(fragment.begin(), fragment.end()) : "");
        System.out.println(result);
    }

    @Test
    public void test2() {
        String str = "abcd中国民党美国啊，中美国啊中国，啊我爱你~";
        List<Fragment> list = TextAnalyzer.analyzeOccur(str, new String[]{"国民党", "中国", "ABCD"}, true);
        System.out.println(TextAnalyzer.toString(str, list));

        Map<String, String> map = new HashMap<String, String>();
        map.put("ab", "甲乙");
        map.put("bcd", "乙丙丁");
        map.put("中美国", "美国");
        map.put("国啊中国", "失败");
        str = TextAnalyzer.replace(str, map);
        assertEquals("a乙丙丁中国民党美国啊，中美失败，啊我爱你~", str);
        System.out.println(str);

        str = "NetEase,<a href='www.netease.com'>网易NetEase，网聚人的力量</a>,网易新闻netease";
        HighLighter hl = new HighLighter(new String[]{"网易", "netease"});
        hl.setIgnoreCase(true);
        String html = hl.highLightHtml(str);
        assertEquals("<em>NetEase</em>,<a href='www.netease.com'><em>网易</em><em>NetEase</em>，网聚人的力量</a>,<em>网易</em>新闻<em>netease</em>", html);
        System.out.println(html);

        String fill = RegexUtils.fill(str, "<.*?>", "****", "****");
        String replace = RegexUtils.replace(str, "<.*?>", "****");
        String replaceAll = str.replaceAll("<.*?>", "****");
        assertEquals(fill, replace);
        assertEquals(fill, replaceAll);
    }
}
