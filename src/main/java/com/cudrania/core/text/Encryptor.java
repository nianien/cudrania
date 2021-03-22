package com.cudrania.core.text;

import com.cudrania.core.utils.StringUtils;

/**
 * 针对文本提供编码解码操作的工具类
 *
 * @author skyfalling
 */
public class Encryptor {

    /**
     * 默认字符集合[0-9A-Za-z]
     */
    public final static char[] defaultCharset = Characters.NUMBER_LETTER;

    /**
     * 采用默认的字符集进行编码<br>
     * 这里默认字符集合[0-9A-Za-z]
     *
     * @param source
     * @return 编码后的字符串
     */
    public static String encrypt(String source) {
        return encrypt(source, defaultCharset);
    }

    /**
     * 采用指定的字符集进行编码
     *
     * @param source
     * @param charset 编码所使用的字符集
     * @return 编码后的字符串
     */
    public static String encrypt(String source, char[] charset) {
        StringBuilder sb = new StringBuilder();
        for (char c : source.toCharArray()) {
            String str = RadixUtils.toRadix(c, charset);
            sb.append(StringUtils.leftPad(str, getDigit(charset.length),
                    charset[0]));
        }
        return sb.toString();

    }

    /**
     * 采用默认的字符集进行解码<br>
     * 默认字符集合[0-9A-Za-z]
     *
     * @param source
     * @return 返回解码后的字符串
     */
    public static String decrypt(String source) {
        String[] arr = groupByLength(source, 3);
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append((char) RadixUtils.toNumber(s, 62));
        }
        return sb.toString();
    }

    /**
     * 采用指定的字符集进行解码
     *
     * @param source
     * @param charset 解码所使用的字符集
     * @return 返回解码后的字符串
     */
    public static String decrypt(String source, char[] charset) {
        String[] arr = groupByLength(source, getDigit(charset.length));
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append((char) RadixUtils.toNumber(s, charset));
        }
        return sb.toString();
    }

    /**
     * 对给定的字符串用数字编码
     *
     * @param source
     * @return 数字字符串
     */
    public static String encryptToNumber(String source) {
        StringBuilder sb = new StringBuilder();
        // 纯数字表示,需要5位
        int width = 5;
        char[] chs = source.toCharArray();
        for (char c : chs) {
            String str = (int) c + "";
            sb.append(StringUtils.leftPad(str, width, '0'));
        }
        return sb.toString();
    }

    /**
     * 对用数字编码的字符串进行解码<br>
     * 待解码的字符串必须为数字字符串
     *
     * @param source
     * @return 解码后字符串
     */
    public static String decryptFromNumber(String source) {
        // 纯数字表示,需要5位
        int width = 5;
        String[] arr = groupByLength(source, width);
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append((char) Integer.parseInt(s));
        }
        return sb.toString();
    }

    /**
     * 根据进制基数计算表示一个Unicode字符所需要的位数<br/>
     * 如二进制需要16位来表示一个Unicode字符
     *
     * @param base
     * @return N进制数的位数
     */
    private static int getDigit(int base) {
        int len;
        if (base == 2) {
            len = 16;
        } else if (base == 3) {
            len = 11;
        } else if (base == 4) {
            len = 8;
        } else if (base >= 5 && base <= 6) {
            len = 7;
        } else if (base >= 7 && base <= 9) {
            len = 6;
        } else if (base >= 10 && base <= 15) {
            len = 5;
        } else if (base >= 16 && base <= 40) {
            len = 4;
        } else if (base >= 41 && base <= 255) {
            len = 3;
        } else if (base >= 256 && base <= 65535) {
            len = 2;
        } else if (base >= 65536) {
            len = 1;
        } else {
            throw new IllegalArgumentException("the base of hex must be no low than 2.");
        }
        return len;
    }

    /**
     * 将字符串按长度从后往前进行分组<br>
     * 分组后除第一组长度可能小于指定长度外,其余均等于指定长度
     * 例如:groupByLength("123456789",4)//返回:{[1],[2345],[6789]}<br>
     *
     * @param source
     * @param length
     * @return 分组后的字符串数组
     */
    private static String[] groupByLength(String source, int length) {
        int len = source.length() % length == 0 ? source.length() / length
                : source.length() / length + 1;
        String[] arr = new String[len];
        int begin;
        for (int i = len; i > 0; i--) {
            begin = source.length() > length ? source.length() - length : 0;
            arr[i - 1] = source.substring(begin);
            source = source.substring(0, begin);

        }
        return arr;
    }
}
