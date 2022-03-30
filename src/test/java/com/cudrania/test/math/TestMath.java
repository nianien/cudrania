package com.cudrania.test.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.cudrania.algorithm.Calculator.calculate;


public class TestMath {

    @ParameterizedTest
    @CsvSource({
            "-0-(-1),1",
            "2**1**4+(2**1)**4,18",
            "(2^1^4)+(2^1)^4,14",
            "(2+3/(1.0*2))-2,1.5",
    })
    public void test(String expr, double res) {
        Assertions.assertEquals(res, calculate(expr));
    }

}
