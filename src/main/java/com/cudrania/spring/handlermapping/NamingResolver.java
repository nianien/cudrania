package com.cudrania.spring.handlermapping;

import org.springframework.util.StringValueResolver;

/**
 * 驼峰命名法转连字符命名法
 * <ul>
 * <li>
 * <li>当使用默认类映射规则时，可指定配置路径是否自动转为小写，方法{@link #useLowerCase(boolean)}</li>
 * </li>
 * </ul>
 *
 * @author skyfalling
 */
public class NamingResolver implements StringValueResolver {

    /**
     * 是否启用小写模式
     */
    private boolean useLowerCase = true;

    public boolean useLowerCase() {
        return useLowerCase;
    }

    /**
     * 设置是否启用小写模式
     *
     * @param useLowerCase
     */
    public void useLowerCase(boolean useLowerCase) {
        this.useLowerCase = useLowerCase;
    }


    /**
     * 命名转换,将驼峰命名转换为连字符命名
     *
     * @param name
     * @return
     */
    private static String convert(String name) {
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

    @Override
    public String resolveStringValue(String strVal) {
        strVal = convert(strVal);
        return useLowerCase ? strVal.toLowerCase() : strVal;
    }
}
