package com.cudrania.test.text;

import com.cudrania.core.text.Wildcard;
import com.cudrania.core.utils.StringUtils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author scorpio
 * @version 1.0.0
 * @email tengzhe.ln@alibaba-inc.com
 */
public class TestWildCard {


    @Test
    public void test() {
        Assert.assertTrue(Wildcard.match("ab?", "abc"));
        Assert.assertTrue(Wildcard.match("ab*", "abc"));
        Assert.assertTrue(Wildcard.matchPath("ab/*/f/**", "ab/c/d/f"));
    }


    @Test
    public void testPad() {
        Assert.assertTrue(StringUtils.leftPad("abc", 5).equals("  abc"));
        Assert.assertTrue(StringUtils.leftPad("abc", 5,' ').equals("  abc"));
        Assert.assertTrue(StringUtils.leftPad("abc", 5, "test").equals("teabc"));
        Assert.assertTrue(StringUtils.rightPad("abc", 5).equals("abc  "));
        Assert.assertTrue(StringUtils.rightPad("abc", 5,' ').equals("abc  "));
        Assert.assertTrue(StringUtils.rightPad("abc", 5, "test").equals("abcte"));
    }
}
