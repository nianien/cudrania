package com.cudrania.core.bits;

import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.utils.StringUtils;

/**
 * 将整型或者二进制字符串转换成bit数组进行操作的对象
 *
 * @author: skyfalling
 */
public class BitValue {
    private long value;
    private int length;
    private boolean littleEndian;


    /**
     * 构造方法
     *
     * @param value 指定bit数组对应的Long整型值,value>=0
     */
    public BitValue(long value) {
        this.setValue(value);
    }

    /**
     * 构造方法
     *
     * @param valueString 采用littleEndian表示法的二进制字符串
     */
    public BitValue(String valueString) {
        this(valueString, true);
    }

    /**
     * 构造方法
     *
     * @param valueString  二进制字符串
     * @param littleEndian true采用采用big_endian表示法,false采用little-endian表示法
     */
    public BitValue(String valueString, boolean littleEndian) {
        if (littleEndian) {
            valueString = new StringBuilder(valueString).reverse().toString();
        }
        this.setValue(Long.valueOf(valueString, 2));
        this.littleEndian = littleEndian;
        this.length = valueString.length();
    }

    /**
     * 设置bit数组对应的Long整型
     *
     * @param value
     */
    private void setValue(long value) {
        ExceptionChecker.throwIf(value < 0, "value cannot be negative: " + value);
        this.value = value;
    }

    /**
     * 获取指定bit位上的布尔值
     *
     * @param index
     * @return
     */
    public boolean get(int index) {
        long test = (1L << index);
        return (value & test) == test;
    }

    /**
     * 将指定bit位的值设为true
     *
     * @param index
     */
    public void set(int index) {
        set(index, true);
    }

    /**
     * 设置指定bit位上的布尔值
     *
     * @param index
     * @param value
     */
    public void set(int index, boolean value) {
        ExceptionChecker.throwIf(index > 64 || index == 64 && value, "index cannot be larger than 64:" + index);
        if (value) {
            this.value |= (1L << index);
        } else {
            this.value &= ~(1L << index);
        }
    }

    /**
     * 获取bit数组对应的Long整型数值
     *
     * @return
     */
    public long value() {
        return value;
    }


    /**
     * 获取bit数组的二进制表示
     *
     * @param littleEndian true采用采用little-endian表示法,false采用big-endian表示法
     * @return
     */
    public String toString(boolean littleEndian) {
        String valueString = StringUtils.leftPad(Long.toString(value, 2), length, '0');
        return littleEndian ? new StringBuilder(valueString).reverse().toString() : valueString;
    }

    /**
     * 获取bit数组的二进制表示<br/>
     *
     * @return
     */
    @Override
    public String toString() {
        return toString(littleEndian);
    }

}

