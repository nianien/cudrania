package com.cudrania.side.spring.handlermapping;

import org.springframework.util.StringValueResolver;

/**
 * 命名处理类<p>
 * 当使用默认类映射规则时，可指定配置路径是否自动转为小写，方法{@link #setUseLowerCase(boolean)}
 *
 * @author skyfalling
 */
public class NamingResolver implements StringValueResolver {

    private boolean useLowerCase = true;
    private boolean nameConvert = true;

    /**
     * 是否进行命名转换
     */
    public boolean isUseLowerCase() {
        return useLowerCase;
    }

    /**
     * 是否进行命名转换
     */
    public void setUseLowerCase(boolean useLowerCase) {
        this.useLowerCase = useLowerCase;
    }

    /**
     * 是否进行命名转换
     *
     * @return
     */
    public boolean isNameConvert() {
        return nameConvert;
    }

    /**
     * 是否进行命名转换
     *
     * @param nameConvert
     */
    public void setNameConvert(boolean nameConvert) {
        this.nameConvert = nameConvert;
    }


    @Override
    public String resolveStringValue(String strVal) {
        if (nameConvert) {
            strVal = convert(strVal);
        }
        if (useLowerCase) {
            strVal = strVal.toLowerCase();
        }
        return strVal;
    }


    /**
     * 命名转换,将驼峰命名转换为连字符命名
     *
     * @param name
     * @return
     */
    protected String convert(String name) {
        StringBuilder sb = new StringBuilder();
        int length = name.length();
        char[] chars = name.toCharArray();
        for (int i = 0; i < length; i++) {
            char ch = chars[i];
            if (Character.isUpperCase(ch)) {
                if (i > 0 && Character.isUpperCase(chars[i - 1]) && i < length - 1 && Character.isLowerCase(chars[i + 1])) {
                    sb.append("-");
                }
                sb.append(ch);
            } else {
                sb.append(ch);
                if (i < length - 1 && Character.isUpperCase(chars[i + 1])) {
                    sb.append("-");
                }
            }
        }
        return sb.toString();
    }

}
