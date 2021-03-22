package com.cudrania.core.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 * 支持设置浮点精度scale和RoundingMode<br/>
 *
 * @author skyfalling
 * @version 1.0.0
 */
public class FineDecimal extends BigDecimal {


    /**
     * 默认精度为4
     */
    private final int scale;
    private final RoundingMode roundingMode;

    /**
     * 构造方法<br/>
     * 默认小数后4位精度,四舍五入
     *
     * @param val
     */
    public FineDecimal(Number val) {
        this(val, 4, RoundingMode.HALF_UP);
    }

    /**
     * 构造方法<br/>
     * 默认四舍五入
     *
     * @param val   初始值
     * @param scale 精度
     */
    public FineDecimal(Number val, int scale) {
        this(val, scale, RoundingMode.HALF_UP);
    }

    /**
     * 构造方法
     *
     * @param val          初始值
     * @param scale        精度
     * @param roundingMode
     */
    public FineDecimal(Number val, int scale, RoundingMode roundingMode) {
        this(val.doubleValue(), scale, roundingMode);
    }

    /**
     * @param val          初始值
     * @param scale        精度
     * @param roundingMode
     */
    private FineDecimal(double val, int scale, RoundingMode roundingMode) {
        super(val, new MathContext(new BigDecimal(val).setScale(scale, roundingMode).precision(), RoundingMode.HALF_UP));
        this.scale = scale;
        this.roundingMode = roundingMode;
    }


    @Override
    public FineDecimal add(BigDecimal augend) {
        return $(super.add(augend));
    }

    @Override
    public FineDecimal add(BigDecimal augend, MathContext mc) {
        return $(super.add(augend, mc));
    }

    @Override
    public FineDecimal subtract(BigDecimal subtrahend) {
        return $(super.subtract(subtrahend));
    }

    @Override
    public FineDecimal subtract(BigDecimal subtrahend, MathContext mc) {
        return $(super.subtract(subtrahend, mc));
    }

    @Override
    public FineDecimal multiply(BigDecimal multiplicand) {
        return $(super.multiply(multiplicand));
    }

    @Override
    public FineDecimal multiply(BigDecimal multiplicand, MathContext mc) {
        return $(super.multiply(multiplicand, mc));
    }

    @Override
    public FineDecimal divide(BigDecimal divisor, int scale, int roundingMode) {
        return $(super.divide(divisor, scale, roundingMode));
    }

    @Override
    public FineDecimal divide(BigDecimal divisor, int scale, RoundingMode roundingMode) {
        return $(super.divide(divisor, scale, roundingMode));
    }

    @Override
    public FineDecimal divide(BigDecimal divisor, int roundingMode) {
        return $(super.divide(divisor, roundingMode));
    }

    @Override
    public FineDecimal divide(BigDecimal divisor, RoundingMode roundingMode) {
        return $(super.divide(divisor, roundingMode));
    }

    @Override
    public FineDecimal divide(BigDecimal divisor) {
        return $(super.divide(divisor, scale, RoundingMode.HALF_UP));
    }

    @Override
    public FineDecimal divide(BigDecimal divisor, MathContext mc) {
        return $(super.divide(divisor, mc));
    }

    @Override
    public FineDecimal divideToIntegralValue(BigDecimal divisor) {
        return $(super.divideToIntegralValue(divisor));
    }

    @Override
    public FineDecimal divideToIntegralValue(BigDecimal divisor, MathContext mc) {
        return $(super.divideToIntegralValue(divisor, mc));
    }

    @Override
    public FineDecimal remainder(BigDecimal divisor) {
        return $(super.remainder(divisor));
    }

    @Override
    public FineDecimal remainder(BigDecimal divisor, MathContext mc) {
        return $(super.remainder(divisor, mc));
    }


    @Override
    public FineDecimal[] divideAndRemainder(BigDecimal divisor) {
        return $(super.divideAndRemainder(divisor));
    }

    @Override
    public FineDecimal[] divideAndRemainder(BigDecimal divisor, MathContext mc) {
        return $(super.divideAndRemainder(divisor, mc));
    }

    @Override
    public FineDecimal pow(int n) {
        return $(super.pow(n));
    }

    @Override
    public FineDecimal pow(int n, MathContext mc) {
        return $(super.pow(n, mc));
    }

    @Override
    public FineDecimal abs() {
        return $(super.abs());
    }

    @Override
    public FineDecimal abs(MathContext mc) {
        return $(super.abs(mc));
    }

    @Override
    public FineDecimal negate() {
        return $(super.negate());
    }

    @Override
    public FineDecimal negate(MathContext mc) {
        return $(super.negate(mc));
    }

    @Override
    public FineDecimal plus() {
        return $(super.plus());
    }

    @Override
    public FineDecimal plus(MathContext mc) {
        return $(super.plus(mc));
    }


    @Override
    public FineDecimal round(MathContext mc) {
        return $(super.round(mc));
    }

    @Override
    public FineDecimal movePointLeft(int n) {
        return $(super.movePointLeft(n));
    }

    @Override
    public FineDecimal movePointRight(int n) {
        return $(super.movePointRight(n));
    }

    @Override
    public FineDecimal scaleByPowerOfTen(int n) {
        return $(super.scaleByPowerOfTen(n));
    }

    @Override
    public FineDecimal stripTrailingZeros() {
        return $(super.stripTrailingZeros());
    }

    @Override
    public FineDecimal min(BigDecimal val) {
        return $(super.min(val));
    }

    @Override
    public FineDecimal max(BigDecimal val) {
        return $(super.max(val));
    }

    @Override
    public FineDecimal ulp() {
        return $(super.ulp());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BigDecimal ? this.compareTo((BigDecimal) o) == 0 : false;
    }

    /**
     * 自动转换精度
     *
     * @param bigDecimal
     * @return
     */
    private FineDecimal $(BigDecimal bigDecimal) {
        return new FineDecimal(bigDecimal, scale, roundingMode);
    }

    /**
     * 自动转换精度
     *
     * @param bigDecimals
     * @return
     */
    private FineDecimal[] $(BigDecimal[] bigDecimals) {
        return Arrays.stream(bigDecimals).map(this::$).toArray(n -> new FineDecimal[n]);
    }

}
