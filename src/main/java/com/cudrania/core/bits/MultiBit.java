package com.cudrania.core.bits;

/**
 * 一个bit只能表示0和1两种状态, 这里使用n个bit表示一个状态位,每个状态位可以表示2^n-1种状态
 *
 * @author scorpio
 * @version 1.0.0
 */
public class MultiBit {

    /**
     * 表示状态的bit数
     */
    private final int bitNum;
    /**
     * 最大索引位置
     */
    private final int maxIndex;
    /**
     * 每个索引位置的最大值(2^bits-1)
     */
    private final int maxValue;
    /**
     * 原码表示,状态位的二进制表示
     */
    private long trueCode;
    /**
     * 掩码表示,指定状态位全为1
     */
    private long maskCode;

    /**
     * @param bitNum 指定多少bit表示一个状态值
     */
    protected MultiBit(int bitNum) {
        this.bitNum = bitNum;
        this.maxIndex = 63 / bitNum - 1;
        this.maxValue = (1 << bitNum) - 1;
    }

    /**
     * 获取索引位置的值(bitNum)
     *
     * @param index from 0 to {@link #maxIndex}
     * @return
     */
    public int get(int index) {
        return (int) (this.trueCode >> index * bitNum) & maxValue;
    }

    /**
     * 设置索引位置的状态值
     *
     * @param index from 0 to {@link #maxIndex}
     * @param value from 0 to {@link #maxValue}
     * @return
     */
    public MultiBit set(int index, int value) {
        if (index < 0 || index > maxIndex) {
            throw new IllegalArgumentException("index must be between 0 and " + maxIndex);
        }
        if (value < 0 || value > maxValue) {
            throw new IllegalArgumentException("value must be between 0 and " + maxValue);
        }
        this.trueCode = this.trueCode & ~(maxValue << index * bitNum) | (value << index * bitNum);
        this.maskCode = this.maskCode | (maxValue << index * bitNum);
        return this;
    }

    /**
     * 取消索引位置的赋值
     *
     * @param index
     * @return
     */
    public MultiBit unset(int index) {
        if (index < 0 || index > maxIndex) {
            throw new IllegalArgumentException("index must be between 0 and " + maxIndex);
        }
        this.trueCode = this.trueCode & ~(maxValue << index * bitNum);
        this.maskCode = this.maskCode & ~(maxValue << index * bitNum);
        return this;
    }

    /**
     * 将bit位应用到指定参数上
     *
     * @param n
     * @return
     */
    public long apply(long n) {
        return n & ~maskCode | trueCode;
    }

    /**
     * 将bit位应用到指定参数上的表达式
     *
     * @param field
     * @return
     */
    public String apply(String field) {
        return field + "=" + field + " & " + (~maskCode) + " | " + trueCode;
    }

    /**
     * 获取当前bit值对应的十进制
     *
     * @return
     */
    public long value() {
        return trueCode;
    }

    /**
     * 返回对应进制的字符串
     *
     * @return
     */
    @Override
    public String toString() {
        return Long.toString(trueCode, maxValue + 1);
    }

    /**
     * 3个bit表示一个状态值(0~7)
     *
     * @return
     */
    public static MultiBit oct() {
        return new MultiBit(3);
    }

    /**
     * 4个bit表示一个状态值(0~15)
     *
     * @return
     */
    public static MultiBit hex() {
        return new MultiBit(4);
    }

    /**
     * n个bit表示一个状态值(0~2^n-1)
     *
     * @param n
     * @return
     */
    public static MultiBit of(int n) {
        return new MultiBit(n);
    }
}
