package com.cudrania.test.text;

import com.cudrania.core.text.Wildcard;
import com.cudrania.core.utils.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author scorpio
 * @version 1.0.0
 */
public class TestWildCard {


    @Test
    public void test() {
        assertTrue(Wildcard.match("ab?", "abc"));
        assertTrue(Wildcard.match("ab*", "abc"));
        assertTrue(Wildcard.matchPath("ab/**/f/**", "ab/c/d/f"));
    }


    @Test
    public void testPad() {
        assertTrue(StringUtils.leftPad("abc", 5).equals("  abc"));
        assertTrue(StringUtils.leftPad("abc", 5, ' ').equals("  abc"));
        assertTrue(StringUtils.leftPad("abc", 5, "test").equals("teabc"));
        assertTrue(StringUtils.rightPad("abc", 5).equals("abc  "));
        assertTrue(StringUtils.rightPad("abc", 5, ' ').equals("abc  "));
        assertTrue(StringUtils.rightPad("abc", 5, "test").equals("abcte"));
    }
}
