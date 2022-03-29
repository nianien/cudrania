package com.cudrania.test.collection;


import com.cudrania.core.collection.map.CaseInsensitiveMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lining05
 * Date: 2013-01-29
 */
public class TestCaseInsensitiveMap {

    @Test
    public void test() {
        Map map = new HashMap();
        map.put("a", "a");
        CaseInsensitiveMap cmap = new CaseInsensitiveMap(map);
        Assertions.assertTrue(cmap.containsKey("A"));


    }
}
