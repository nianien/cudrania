package com.cudrania.test.math;

import com.cudrania.algorithm.Calculator;

import org.junit.jupiter.api.Test;


public class TestMath {

    @Test
    public void test() {
        String a = "(2^1^4)+(2^1)^4";
        System.out.println(Calculator.calculate("-0-(-1)"));
        System.out.println(Calculator.calculate(a));
        System.out.println(Calculator.calculate("(2+3/(1.0*2))-2"));

    }

}
