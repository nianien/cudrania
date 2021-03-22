package com.cudrania.core.bits;

import java.util.BitSet;

/**
 * 使用N个bit表示状态的BitSet<br>
 * 1个bit可以表示两个状态,当一个对象需要表示多个状态时,就要用多个bit来表示<br>
 * 其中,n个bit最多可以表示2^n个状态,状态值分别为:0~2^n-1
 *
 * @author skyfalling
 */
public class MultiBitSet {

    /**
     * 使用一个bit表示状态的BitSet
     */
    private BitSet bitset = new BitSet();
    /**
     * 用于表示状态的bit数
     */
    private int nBit = 1;
    /**
     * 用于表示状态的最大值
     */
    private int maxValue = 1;

    /**
     * 构造方法,指定状态表示所需要的bit数
     *
     * @param nbit 用来表示状态的bit数
     */
    public MultiBitSet(int nbit) {
        if (nbit < 1)
            throw new IllegalArgumentException(
                    "the argument of nbit cannot be low than 1: " + nbit);
        this.nBit = nbit;
        this.maxValue = (1 << nbit) - 1;
    }

    /**
     * 设置指定索引位置的值
     *
     * @param index 索引位置
     * @param value 索引位置的值
     */
    public void set(int index, int value) {
        this.set(index, index + 1, value);
    }

    /**
     * 将索引位置从beginIndex(含)到endIndex(不含)的值设为value
     *
     * @param beginIndex 起始索引位置(含)
     * @param endIndex   结束索引位置(不含)
     * @param value
     */
    public void set(int beginIndex, int endIndex, int value) {
        if (value > maxValue || value < 0)
            throw new IllegalArgumentException(
                    "the argument of value must be between 0 and " + maxValue
                            + ": " + value);
        boolean[] bits = toBits(value, nBit);
        for (int i = beginIndex; i < endIndex; i++) {
            this.set(i * nBit, bits);
        }
    }

    /**
     * 获取指定索引位置的值
     *
     * @param index
     */
    public int get(int index) {
        boolean[] bits = new boolean[nBit];
        int n = 0;
        // from index*N to (index+1)*N-1
        int begin = index * nBit;
        int end = (index + 1) * nBit;
        for (int i = begin; i < end; i++) {
            bits[n++] = bitset.get(i);
        }
        return valueOf(bits);
    }

    /**
     * 将所有索引位置的值清零
     */
    public void clear() {
        this.bitset.clear();
    }

    /**
     * 将索引位置从beginIndex(含)到endIndex(不含)的值置零
     *
     * @param beginIndex 起始索引位置(含)
     * @param endIndex   结束索引位置(不含)
     */
    public void clear(int beginIndex, int endIndex) {
        this.bitset.clear(beginIndex * nBit, endIndex * nBit);
    }

    /**
     * 获取使用的索引数目,即值最大非零索引位置+1
     *
     * @return
     */
    public int length() {
        int length = this.bitset.length();
        return (length / nBit) + (length % nBit == 0 ? 0 : 1);
    }

    /**
     * 获取当前容量,size=2^n,使得n满足2^(n-1)<length<2^n
     *
     * @return
     */
    public int size() {
        return this.bitset.size() >> (nBit - 1);
    }

    /**
     * 获取非零索引的数目,即实际存在的元素总数
     *
     * @return
     */
    public int cardinality() {
        // 非零索引数
        int sum = 0;
        // 步长为N,一个索引占用n个bit
        for (int i = 0; i < this.bitset.length(); i += nBit) {
            for (int j = i; j < i + nBit - 1; j++) {
                // 任意一个bit不为零即为非零索引
                if (this.bitset.get(j)) {
                    sum++;
                    break;
                }
            }
        }
        return sum;
    }

    /**
     * 从当前位置开始(含),获取第一个值非零索引,如果不存在返回-1
     *
     * @param fromIndex
     * @return
     * @see java.util.BitSet#nextSetBit
     */
    public int nextSetBit(int fromIndex) {
        return this.bitset.nextSetBit(fromIndex * nBit) / nBit;
    }

    /**
     * 从指定位置开始(含),依次将索引值设为数组中的元素值
     *
     * @param beginIndex
     * @param bits
     */
    private void set(int beginIndex, boolean[] bits) {
        for (int i = 0; i < bits.length; i++)
            bitset.set(beginIndex + i, bits[i]);
    }

    /**
     * 计算二进制bit数组的十进制值<br>
     * bit使用布尔类型表示,true为1,false为0,采用little-endian表示法<br>
     *
     * @param bits
     * @return
     */
    private static int valueOf(boolean[] bits) {
        int sum = 0;
        for (int i = bits.length - 1; i >= 0; i--) {
            sum = sum * 2 + (bits[i] ? 1 : 0);
        }
        return sum;
    }

    /**
     * 整型转换为长度为n的二进制bit数组<br>
     * bit使用布尔类型表示,true为1,false为0,采用little-endian表示法
     *
     * @param value
     * @param n
     * @return
     */
    private static boolean[] toBits(int value, int n) {
        boolean[] bits = new boolean[n];
        int i = 0;
        while (value != 0) {
            bits[i++] = (value % 2 == 1);
            value /= 2;
        }
        return bits;
    }
}
