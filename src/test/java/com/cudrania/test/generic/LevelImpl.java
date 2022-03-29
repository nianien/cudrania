package com.cudrania.test.generic;

import com.cudrania.core.reflection.Generics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author scorpio
 * @version 1.0.0
 */
public class LevelImpl<T> implements ILevelSecond<Character, T, List<Boolean>> {


    @Test
    public void test() {
        Assertions.assertEquals(Character.class, Generics.find(LevelImpl.class, ILevelSecond.class, 0));
        Assertions.assertEquals(null, Generics.find(LevelImpl.class, ILevelSecond.class, 1));
        Assertions.assertEquals(List.class, Generics.find(LevelImpl.class, ILevelSecond.class, 2));
        Assertions.assertEquals(String.class, Generics.find(LevelImpl2.class, LevelImpl.class, 0));
        Assertions.assertEquals(List.class, Generics.find(LevelImpl2.class, ILevel.class, 0));
        Assertions.assertEquals(List.class, Generics.find(LevelImpl.class, ILevel.class, 0));
        Assertions.assertEquals(Character.class, Generics.find(LevelImpl.class, ILevelFirst.class, 0));
        Assertions.assertEquals(List.class, Generics.find(LevelImpl.class, ILevelFirst.class, 1));
        Assertions.assertEquals(String.class, Generics.find(LevelImpl3.class, LevelImpl.class, 0));

    }


    static class LevelImpl2 extends LevelImpl<String> {

    }

    static class LevelImpl3 extends LevelImpl2 {

    }


}
