package com.cudrania.test.jackson;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 财务数据类型
 * 统一浮点数精度
 *
 * @author skyfalling
 * @version 1.0.0
 */
public class FinanceDecimal extends BigDecimal {

    public static final int DECIMAL_DEFAULT_SCALE = 2;

    /**
     * @param val
     */
    public FinanceDecimal(String val) {
        super(val, mathContext(val));
    }

    public FinanceDecimal(Number val) {
        this(String.valueOf(val));
    }

    @Override
    public BigDecimal multiply(BigDecimal multiplicand, MathContext mc) {
        return cast(super.multiply(multiplicand, mc));
    }

    @Override
    public BigDecimal divide(BigDecimal divisor, int scale, int roundingMode) {
        return cast(super.divide(divisor, scale, roundingMode));
    }

    @Override
    public BigDecimal divide(BigDecimal divisor, int scale, RoundingMode roundingMode) {
        return cast(super.divide(divisor, scale, roundingMode));
    }

    @Override
    public BigDecimal divide(BigDecimal divisor, int roundingMode) {
        return cast(super.divide(divisor, roundingMode));
    }

    @Override
    public BigDecimal divide(BigDecimal divisor, RoundingMode roundingMode) {
        return cast(super.divide(divisor, roundingMode));
    }

    @Override
    public BigDecimal divide(BigDecimal divisor) {
        return cast(super.divide(divisor));
    }

    @Override
    public BigDecimal divide(BigDecimal divisor, MathContext mc) {
        return cast(super.divide(divisor, mc));
    }

    @Override
    public BigDecimal divideToIntegralValue(BigDecimal divisor) {
        return cast(super.divideToIntegralValue(divisor));
    }

    @Override
    public BigDecimal divideToIntegralValue(BigDecimal divisor, MathContext mc) {
        return cast(super.divideToIntegralValue(divisor, mc));
    }

    @Override
    public BigDecimal remainder(BigDecimal divisor) {
        return cast(super.remainder(divisor));
    }

    @Override
    public BigDecimal remainder(BigDecimal divisor, MathContext mc) {
        return cast(super.remainder(divisor, mc));
    }


    @Override
    public BigDecimal[] divideAndRemainder(BigDecimal divisor) {
        return cast(super.divideAndRemainder(divisor));
    }

    @Override
    public BigDecimal[] divideAndRemainder(BigDecimal divisor, MathContext mc) {
        return cast(super.divideAndRemainder(divisor, mc));
    }

    @Override
    public BigDecimal pow(int n) {
        return cast(super.pow(n));
    }

    @Override
    public BigDecimal pow(int n, MathContext mc) {
        return cast(super.pow(n, mc));
    }

    @Override
    public BigDecimal abs() {
        return cast(super.abs());
    }

    @Override
    public BigDecimal abs(MathContext mc) {
        return cast(super.abs(mc));
    }

    @Override
    public BigDecimal negate() {
        return cast(super.negate());
    }

    @Override
    public BigDecimal negate(MathContext mc) {
        return cast(super.negate(mc));
    }

    @Override
    public BigDecimal plus() {
        return cast(super.plus());
    }

    @Override
    public BigDecimal plus(MathContext mc) {
        return cast(super.plus(mc));
    }


    @Override
    public BigDecimal round(MathContext mc) {
        return cast(super.round(mc));
    }

    @Override
    public BigDecimal setScale(int newScale, RoundingMode roundingMode) {
        return cast(super.setScale(newScale, roundingMode));
    }

    @Override
    public BigDecimal setScale(int newScale, int roundingMode) {
        return cast(super.setScale(newScale, roundingMode));
    }

    @Override
    public BigDecimal setScale(int newScale) {
        return cast(super.setScale(newScale));
    }

    @Override
    public BigDecimal movePointLeft(int n) {
        return cast(super.movePointLeft(n));
    }

    @Override
    public BigDecimal movePointRight(int n) {
        return cast(super.movePointRight(n));
    }

    @Override
    public BigDecimal scaleByPowerOfTen(int n) {
        return cast(super.scaleByPowerOfTen(n));
    }

    @Override
    public BigDecimal stripTrailingZeros() {
        return cast(super.stripTrailingZeros());
    }

    @Override
    public BigDecimal min(BigDecimal val) {
        return cast(super.min(val));
    }

    @Override
    public BigDecimal max(BigDecimal val) {
        return cast(super.max(val));
    }

    @Override
    public BigDecimal ulp() {
        return cast(super.ulp());
    }

    private FinanceDecimal cast(BigDecimal bigDecimal) {
        return new FinanceDecimal(bigDecimal);
    }

    private FinanceDecimal[] cast(BigDecimal[] bigDecimals) {
        FinanceDecimal[] financeDecimals = new FinanceDecimal[bigDecimals.length];
        for (int i = 0; i < bigDecimals.length; i++) {
            financeDecimals[i] = cast(bigDecimals[i]);
        }
        return financeDecimals;
    }

    private static MathContext mathContext(String val) {
        BigDecimal bigDecimal = new BigDecimal(val);
        bigDecimal = bigDecimal.setScale(DECIMAL_DEFAULT_SCALE, ROUND_HALF_UP);
        return new MathContext(bigDecimal.precision(), RoundingMode.HALF_UP);
    }

    public static BigDecimal valueOf(double val) {
        return new FinanceDecimal(Double.toString(val));
    }
}
