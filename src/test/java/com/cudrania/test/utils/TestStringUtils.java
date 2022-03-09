package com.cudrania.test.utils;

import com.cudrania.core.arrays.ArrayUtils;
import com.cudrania.core.utils.StringUtils;

import org.junit.jupiter.api.Test;

import java.beans.Expression;
import java.util.Arrays;

/**
 * @author skyfalling
 */
public class TestStringUtils {

    @Test
    public void test() throws Exception {
        System.out.println(StringUtils.leftPad("a", 3, 'c'));
        System.out.println(StringUtils.leftPad("!", 6, "abc"));
        System.out.println(StringUtils.fill("?a?b?c???", '?', "1", "2","3"));
        System.out.println(StringUtils.fill("a?b?c?", '?', "1", "2","3","4"));
        System.out.println(StringUtils.fill("a??b??c??","??", "1", "2","3","4"));
        System.out.println(StringUtils.fill("a??b??????c??","???", "1", "2"));


        System.out.println(ArrayUtils.toString(Arrays.asList("1","2","3").toArray(),"|"));
        Expression expression = new Expression("abc", "toCharArray", null);
        System.out.println(expression.getValue());
    }
}
