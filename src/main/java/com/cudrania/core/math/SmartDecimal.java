package com.cudrania.core.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 * 支持浮点运算结果自动设置精度scale和{@link RoundingMode}
 *
 * @author skyfalling
 * @version 1.0.0
 */
public class SmartDecimal extends BigDecimal {


    /**
     * 默认精度为4
     */
    private final int scale;
    private final RoundingMode roundingMode;

    /**
     * 构造方法<p>
     * 默认小数后4位精度,四舍五入
     *
     * @param val
     */
    public SmartDecimal(Number val) {
        this(val, 4, RoundingMode.HALF_UP);
    }

    /**
     * 构造方法<p>
     * 默认四舍五入
     *
     * @param val   初始值
     * @param scale 精度
     */
    public SmartDecimal(Number val, int scale) {
        this(val, scale, RoundingMode.HALF_UP);
    }

    /**
     * 构造方法
     *
     * @param val          初始值
     * @param scale        精度
     * @param roundingMode
     */
    public SmartDecimal(Number val, int scale, RoundingMode roundingMode) {
        this(val.doubleValue(), scale, roundingMode);
    }

    /**
     * @param val          初始值
     * @param scale        精度
     * @param roundingMode
     */
    private SmartDecimal(double val, int scale, RoundingMode roundingMode) {
        super(val, new MathContext(new BigDecimal(val).setScale(scale, roundingMode).precision(), RoundingMode.HALF_UP));
        this.scale = scale;
        this.roundingMode = roundingMode;
    }


    @Override
    public SmartDecimal add(BigDecimal augend) {
        return of(super.add(augend));
    }

    @Override
    public SmartDecimal add(BigDecimal augend, MathContext mc) {
        return of(super.add(augend, mc));
    }

    @Override
    public SmartDecimal subtract(BigDecimal subtrahend) {
        return of(super.subtract(subtrahend));
    }

    @Override
    public SmartDecimal subtract(BigDecimal subtrahend, MathContext mc) {
        return of(super.subtract(subtrahend, mc));
    }

    @Override
    public SmartDecimal multiply(BigDecimal multiplicand) {
        return of(super.multiply(multiplicand));
    }

    @Override
    public SmartDecimal multiply(BigDecimal multiplicand, MathContext mc) {
        return of(super.multiply(multiplicand, mc));
    }

    @Override
    @Deprecated(since="9")
    public SmartDecimal divide(BigDecimal divisor, int scale, int roundingMode) {
        return of(super.divide(divisor, scale, roundingMode));
    }

    @Override
    public SmartDecimal divide(BigDecimal divisor, int scale, RoundingMode roundingMode) {
        return of(super.divide(divisor, scale, roundingMode));
    }

    @Override
    public SmartDecimal divide(BigDecimal divisor, int roundingMode) {
        return of(super.divide(divisor, roundingMode));
    }

    @Override
    public SmartDecimal divide(BigDecimal divisor, RoundingMode roundingMode) {
        return of(super.divide(divisor, roundingMode));
    }

    @Override
    public SmartDecimal divide(BigDecimal divisor) {
        return of(super.divide(divisor, scale, RoundingMode.HALF_UP));
    }

    @Override
    public SmartDecimal divide(BigDecimal divisor, MathContext mc) {
        return of(super.divide(divisor, mc));
    }

    @Override
    public SmartDecimal divideToIntegralValue(BigDecimal divisor) {
        return of(super.divideToIntegralValue(divisor));
    }

    @Override
    public SmartDecimal divideToIntegralValue(BigDecimal divisor, MathContext mc) {
        return of(super.divideToIntegralValue(divisor, mc));
    }

    @Override
    public SmartDecimal remainder(BigDecimal divisor) {
        return of(super.remainder(divisor));
    }

    @Override
    public SmartDecimal remainder(BigDecimal divisor, MathContext mc) {
        return of(super.remainder(divisor, mc));
    }


    @Override
    public SmartDecimal[] divideAndRemainder(BigDecimal divisor) {
        return of(super.divideAndRemainder(divisor));
    }

    @Override
    public SmartDecimal[] divideAndRemainder(BigDecimal divisor, MathContext mc) {
        return of(super.divideAndRemainder(divisor, mc));
    }

    @Override
    public SmartDecimal pow(int n) {
        return of(super.pow(n));
    }

    @Override
    public SmartDecimal pow(int n, MathContext mc) {
        return of(super.pow(n, mc));
    }

    @Override
    public SmartDecimal abs() {
        return of(super.abs());
    }

    @Override
    public SmartDecimal abs(MathContext mc) {
        return of(super.abs(mc));
    }

    @Override
    public SmartDecimal negate() {
        return of(super.negate());
    }

    @Override
    public SmartDecimal negate(MathContext mc) {
        return of(super.negate(mc));
    }

    @Override
    public SmartDecimal plus() {
        return of(super.plus());
    }

    @Override
    public SmartDecimal plus(MathContext mc) {
        return of(super.plus(mc));
    }


    @Override
    public SmartDecimal round(MathContext mc) {
        return of(super.round(mc));
    }

    @Override
    public SmartDecimal movePointLeft(int n) {
        return of(super.movePointLeft(n));
    }

    @Override
    public SmartDecimal movePointRight(int n) {
        return of(super.movePointRight(n));
    }

    @Override
    public SmartDecimal scaleByPowerOfTen(int n) {
        return of(super.scaleByPowerOfTen(n));
    }

    @Override
    public SmartDecimal stripTrailingZeros() {
        return of(super.stripTrailingZeros());
    }

    @Override
    public SmartDecimal min(BigDecimal val) {
        return of(super.min(val));
    }

    @Override
    public SmartDecimal max(BigDecimal val) {
        return of(super.max(val));
    }

    @Override
    public SmartDecimal ulp() {
        return of(super.ulp());
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
    private SmartDecimal of(BigDecimal bigDecimal) {
        return new SmartDecimal(bigDecimal, scale, roundingMode);
    }

    /**
     * 自动转换精度
     *
     * @param bigDecimals
     * @return
     */
    private SmartDecimal[] of(BigDecimal[] bigDecimals) {
        return Arrays.stream(bigDecimals).map(this::of).toArray(SmartDecimal[]::new);
    }

}
