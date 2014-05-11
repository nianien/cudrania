package com.kavas.misc.spring.handlermapping;

import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * 可配置的请求映射处理类，满足以下功能：<br/>
 * <ul>
 * <li>
 * 支持{@link org.springframework.web.bind.annotation.RequestMapping}注解和类自动映射，RequestMapping配置优先级高于类映射；<br/>
 * 当未使用{@link org.springframework.web.bind.annotation.RequestMapping}注解或者未指定映射路径时，则采用类映射
 * <li>采用正则表达式配置类映射规则，见方法{@link #setClassPattern(String)}和{@link #setClassReplacement(String)}</li>
 * <li>采用正则表达式配置包路径映射，见方法{@link #setPackagePattern(String)}和{@link #setPackageReplacement(String)}</li>
 * </li>
 * </ul>
 * <pre>
 *      <code>设存在类com.abc.controller.xyz.XxxController，在未配置{@link org.springframework.web.bind.annotation.RequestMapping}注解的情况下，则默认取Xxx作为映射路径
 *      此时如果配置packagePattern="^com.abc.controller.?"，packageReplacement="/api"，则映射路径为/api/xyz/Xxx<br/>
 *      </code>
 *  </pre>
 *
 * @author skyfalling
 */
public class RequestMappingConfiguration {
    /**
     * 匹配包名的正则表达式
     */
    private String packagePattern = ".*";
    /**
     * 替换匹配包名的表达式
     */
    private String packageReplacement = "";
    /**
     * 匹配类名的正则表达式
     */
    private String classPattern = "(.*)Controller";
    /**
     * 替换匹配类名的表达式
     */
    private String classReplacement = "$1";

    /**
     * 请求方法配置
     */
    private Map<String, RequestMethod> requestMethodPatterns = new HashMap<String, RequestMethod>();

    /**
     * 类名及方法名的处理
     */
    private StringValueResolver nameResolver = new NamingResolver();

    public String getPackagePattern() {
        return packagePattern;
    }

    /**
     * 设置包名匹配模式,默认值".*"
     *
     * @param packagePattern
     */
    public void setPackagePattern(String packagePattern) {
        this.packagePattern = packagePattern;
    }

    public String getPackageReplacement() {
        return packageReplacement;
    }

    /**
     * 设置匹配包名的替换表达式,默认值""
     *
     * @param packageReplacement
     */
    public void setPackageReplacement(String packageReplacement) {
        this.packageReplacement = packageReplacement;
    }

    public String getClassPattern() {
        return classPattern;
    }

    /**
     * 设置匹配类名的正则表达式,默认值"(.*)Controller"
     *
     * @param classPattern
     */
    public void setClassPattern(String classPattern) {
        this.classPattern = classPattern;
    }

    public String getClassReplacement() {
        return classReplacement;
    }

    /**
     * 设置匹配类名的替换表达式,默认值"$1"
     *
     * @param classReplacement
     */
    public void setClassReplacement(String classReplacement) {
        this.classReplacement = classReplacement;
    }

    public Map<String, RequestMethod> getRequestMethodPatterns() {
        return requestMethodPatterns;
    }

    /**
     * 根据方法名正则表达式设置允许的RequestMethod
     *
     * @param requestMethodPatterns
     */
    public void setRequestMethodPatterns(Map<String, RequestMethod> requestMethodPatterns) {
        this.requestMethodPatterns = requestMethodPatterns;
    }

    public StringValueResolver getNameResolver() {
        return nameResolver;
    }

    /**
     * 设置匹配路径的处理类,这里用于对匹配路径的转换
     *
     * @param nameResolver
     */
    public void setNameResolver(StringValueResolver nameResolver) {
        this.nameResolver = nameResolver;
    }


}
