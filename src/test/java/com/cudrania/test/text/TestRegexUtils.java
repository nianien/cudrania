package com.cudrania.test.text;

import com.cudrania.core.text.RegexUtils;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skyfalling
 */
public class TestRegexUtils {


    @Test
    public void test() {
        String html = "<div class=\"line mt-10 app-and-share\"><a alog-action=\"qb-username\" log=\"pms:newqb,pos:bestreplyer\" class=\"avatar-normal-a\" rel=\"nofollow\" href=\"http://www.baidu.com/p/wxl778?from=zhidao\" target=\"_blank\" data-img=\"http://img.iknow.bdimg.com/avatar/48/r6s2g4.gif\"> </a> </p> </div> <div class=\"grid f-aid\"> <p> <a alog-action=\"qb-username\" log=\"pms:newqb,pos:bestreplyer\" class=\"user-name\" rel=\"nofollow\" href=\"http://www.baidu.com/p/wxl778?from=zhidao\" target=\"_blank\">wxl778</a>          <span class=\"f-pipe\">|</span><a class=\"f-aid\" href=\"http://www.baidu.com/search/zhidao_help.html#如何选择头衔\" target=\"_blank\">四级</a><span class=\"ml-10\">采纳率60%</span></p><p class=\"carefield\"><span>擅长：</span><a class=\"mr-5 f-aid\" href=\"/browse/1285\" target=\"_blank\">Linux</a></p></div></div></div></div> ";

        List list = RegexUtils.find(html, "(?:href=)\".*?\"");
        System.out.println(list);
        list = RegexUtils.find(html, "href=\"", "\"");
        System.out.println(list);

    }

    @Test
    public void test2() {
        System.out.println("${a}".replaceAll("\\$\\{.*?}", "b"));
        Map map = new HashMap();
        map.put("${a}", "b");
        map.put("${a1}", "b");
        System.out.println(RegexUtils.replace("${a}${a1}", "\\$\\{.*?}", map));

    }
}
