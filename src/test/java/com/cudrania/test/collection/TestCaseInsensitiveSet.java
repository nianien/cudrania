package com.cudrania.test.collection;

import com.cudrania.core.collection.set.CaseInsensitiveSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skyfalling
 */
public class TestCaseInsensitiveSet {

    @Test
    public void main() {

        List<String> list = new ArrayList<String>();
        list.add("A");
        list.add("B");
        CaseInsensitiveSet set = new CaseInsensitiveSet(list);
        list.add("C");
        Assertions.assertTrue(set.contains("b"));
        Assertions.assertTrue(!set.contains("c"));

    }
}
