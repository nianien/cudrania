package com.cudrania.core.xml;

import com.cudrania.core.date.DateFormatter;
import com.cudrania.core.date.DatePattern;
import com.cudrania.core.reflection.Reflections;
import com.cudrania.core.utils.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 构建XML内容的工具类
 *
 * @author skyfalling
 */
public class XMLBuilder {

    /**
     * 日期格式
     */
    private static ThreadLocal<String> datePattern = ThreadLocal.withInitial(() -> DatePattern.Default);

    /**
     * 设置日期格式
     *
     * @param dataPattern
     */
    public static void setDatePattern(String dataPattern) {
        datePattern.set(dataPattern);
    }

    /**
     * 格式化日期
     *
     * @param date
     * @return
     */
    private static String formatDate(Object date) {
        return DateFormatter.format((Date) date, datePattern.get());
    }

    /**
     * XML文件头信息
     *
     * @param content
     * @param encoding
     * @return XML文本内容
     */
    public static String xmlHeader(String content, String encoding) {
        return new StringBuilder().append("<?xml version=\"1.0\" encoding=\"")
                .append(StringUtils.defaultIfEmpty(encoding, "UTF-8"))
                .append("\"?>").append(content).toString();
    }

    /**
     * 构建名称为nodeName的XML节点,对象属性作为子节点
     *
     * @param <T>
     * @param nodeName
     * @param bean
     * @return
     */
    public static <T> String xml(String nodeName, T bean) {
        StringBuilder sb = new StringBuilder();
        buildNode(bean, nodeName, sb);
        return sb.toString();
    }

    /**
     * 构建名称为nodeName的XML节点集合
     *
     * @param <T>
     * @param nodeName
     * @param elementName
     * @param iterable
     * @return
     */
    public static <T> String xml(String nodeName,
                                 String elementName, Iterable<T> iterable) {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(nodeName).append(">");
        for (T t : iterable) {
            buildNode(t, elementName, sb);
        }
        sb.append("</").append(nodeName).append(">");
        return sb.toString();
    }

    /**
     * 构建名称为nodeName的XML节点数组
     *
     * @param <T>
     * @param nodeName
     * @param elementName
     * @param array
     * @return
     */
    public static <T> String xml(String nodeName,
                                 String elementName, T[] array) {
        return xml(nodeName, elementName, Arrays.asList(array));
    }

    /**
     * 构建名称为nodeName且包含属性的XML节点,简单对象属性作为节点属性,自定义对象属性作为带属性的子节点
     *
     * @param <T>
     * @param nodeName
     * @param bean
     * @return
     */
    public static <T> String xmlWithAttribute(String nodeName, T bean) {
        StringBuilder sb = new StringBuilder();
        buildNodeWithAttribute(bean, nodeName, sb);
        return sb.toString();
    }

    /**
     * 构建名称为nodeName且包含属性的XML节点集合
     *
     * @param <T>
     * @param nodeName
     * @param elementName
     * @param iterable
     * @return
     */
    public static <T> String xmlWithAttribute(String nodeName, String elementName, Iterable<T> iterable) {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(nodeName).append(">");
        for (T t : iterable) {
            buildNodeWithAttribute(t, elementName, sb);
        }
        sb.append("</").append(nodeName).append(">");
        return sb.toString();
    }

    /**
     * 构建名称为nodeName且包含属性的XML节点集合
     *
     * @param <T>
     * @param nodeName
     * @param elementName
     * @param array
     * @return
     */
    public static <T> String xmlWithAttribute(String nodeName,
                                              String elementName, T[] array) {
        return xmlWithAttribute(nodeName, elementName, Arrays.asList(array));
    }

    /**
     * 构建名称为nodeName的XML节点
     *
     * @param bean
     * @param nodeName
     * @param sb
     */
    private static void buildNode(Object bean, String nodeName, StringBuilder sb) {
        if (bean == null)
            return;
        sb.append("<").append(nodeName).append(">");
        if (bean instanceof Boolean || bean instanceof Byte
                || bean instanceof Short || bean instanceof Integer
                || bean instanceof Long || bean instanceof Float
                || bean instanceof Double || bean instanceof Character
                || bean instanceof String) {
            sb.append(bean);
        } else if (bean instanceof Date) {
            sb.append(formatDate(bean));
        } else if (bean instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) bean;
            for (Object key : map.keySet()) {
                buildNode(map.get(key), key.toString(), sb);
            }
        } else if (bean instanceof Iterable<?>) {
            Iterable<?> iterable = (Iterable<?>) bean;
            for (Object obj : iterable) {
                buildNode(obj, obj.getClass().getSimpleName(), sb);
            }
        } else if (bean.getClass().isArray()) {
            int len = Array.getLength(bean);
            for (int i = 0; i < len; i++) {
                Object element = Array.get(bean, i);
                buildNode(element, element.getClass().getSimpleName(), sb);
            }
        } else {
            // 获取get方法
            List<Method> methods = Reflections.getters(bean.getClass());
            for (Method m : methods) {
                String name = Reflections.propertyName(m);
                Object value = Reflections.invoke(m, bean);
                if (value == null)
                    continue;
                buildNode(value, name, sb);
            }
        }
        sb.append("</").append(nodeName).append(">");
    }

    /**
     * 构建名称为nodeName且包含属性的XML节点
     *
     * @param bean
     * @param nodeName
     * @param sb
     */
    private static void buildNodeWithAttribute(Object bean, String nodeName,
                                               StringBuilder sb) {
        if (bean == null)
            return;
        sb.append("<").append(nodeName).append(" ");
        StringBuilder sub = new StringBuilder();
        if (bean instanceof Boolean
                || bean instanceof Byte
                || bean instanceof Short
                || bean instanceof Integer
                || bean instanceof Long
                || bean instanceof Float
                || bean instanceof Double
                || bean instanceof Character
                || bean instanceof String
                || bean instanceof Date) {
            appendAttributes(bean.getClass().getSimpleName(), bean, sb, sub);
        } else if (bean instanceof Iterable<?>) {
            Iterable<?> iterable = (Iterable<?>) bean;
            for (Object obj : iterable) {
                buildNodeWithAttribute(obj, obj.getClass().getSimpleName(), sub);
            }
        } else if (bean.getClass().isArray()) {
            int len = Array.getLength(bean);
            for (int i = 0; i < len; i++) {
                Object element = Array.get(bean, i);
                buildNodeWithAttribute(element, element.getClass().getSimpleName(), sub);
            }
        } else if (bean instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) bean;
            for (Object name : map.keySet()) {
                appendAttributes(name, map.get(name), sb, sub);
            }
        } else {
            // 获取公开的get方法
            List<Method> methods = Reflections
                    .getters(bean.getClass());
            for (Method m : methods) {
                appendAttributes(Reflections.propertyName(m), Reflections.invoke(m, bean), sb, sub);
            }
        }
        if (sub.length() == 0) {
            sb.append("/>");
        } else {
            sb.append(">");
            sb.append(sub);
            sb.append("</").append(nodeName).append(">");
        }
    }

    /**
     * 追加属性
     *
     * @param attrName
     * @param attrValue
     * @param sb
     * @param sub
     */
    private static void appendAttributes(Object attrName, Object attrValue, StringBuilder sb, StringBuilder sub) {
        if (attrValue == null)
            return;
        if (attrValue instanceof Boolean || attrValue instanceof Byte
                || attrValue instanceof Short
                || attrValue instanceof Integer
                || attrValue instanceof Long || attrValue instanceof Float
                || attrValue instanceof Double
                || attrValue instanceof Character
                || attrValue instanceof String) {
            sb.append(attrName).append("=\"").append(attrValue)
                    .append("\" ");
        } else if (attrValue instanceof Date) {
            sb.append(attrName).append("=\"")
                    .append(formatDate(attrValue))
                    .append("\" ");
        } else {
            buildNodeWithAttribute(attrValue, attrName.toString(), sub);
        }
    }
}
