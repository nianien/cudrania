package com.cudrania.spring.condition;

import com.nianien.core.text.Wildcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.springframework.core.type.classreading.MethodMetadataReadingVisitor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

import static java.beans.Introspector.decapitalize;
import static org.springframework.util.ClassUtils.getShortName;

/**
 * 任务选择器, 判断是否需要加载当前任务
 *
 * @author scorpio
 * @version 1.0.0
 */
public class TaskSelector implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(Task.class.getName());
        String value = (String) attributes.get("value");
        if (StringUtils.isEmpty(value)) {
            if (metadata instanceof AnnotationMetadataReadingVisitor) {
                value = valueByComponent(metadata);
            } else if (metadata instanceof MethodMetadataReadingVisitor) {
                value = valueByBean(metadata);
            }
        }
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        String key = (String) attributes.get("key");
        if (StringUtils.isEmpty(key)) {
            key = "task";
        }
        String[] tasks = context.getEnvironment().getProperty(key, "").split("[,;]");
        return select(value, tasks);
    }

    /**
     * 获取{@link Component}声明的beanName
     *
     * @param metadata
     * @return
     */
    private String valueByComponent(AnnotatedTypeMetadata metadata) {
        String annotationName = Component.class.getName();
        if (metadata.isAnnotated(annotationName)) {
            String value = (String) metadata.getAnnotationAttributes(annotationName).get("value");
            if (!StringUtils.isEmpty(value)) {
                return value;
            }
        }
        String shortClassName = getShortName(((AnnotationMetadataReadingVisitor) metadata).getClassName());
        return decapitalize(shortClassName);
    }

    /**
     * 获取{@link Bean}声明的beanName<br/>
     * 如果bean声明多个名称,则取第一个
     *
     * @param metadata
     * @return
     */
    private String valueByBean(AnnotatedTypeMetadata metadata) {
        String annotationName = Bean.class.getName();
        if (metadata.isAnnotated(annotationName)) {
            String[] values = (String[]) metadata.getAnnotationAttributes(annotationName).get("value");
            for (String value : values) {
                if (!StringUtils.isEmpty(value)) {
                    return value;
                }
            }
        }
        return decapitalize(((MethodMetadataReadingVisitor) metadata).getMethodName());
    }


    /**
     * 判断是否匹配指定字符串模式<br/>
     * 这里字符串模式支持通配符,多个匹配模型以","或";"分割<br/>
     * 匹配模式以"!"开始表示否定匹配
     *
     * @param string
     * @param patterns
     * @return
     */
    private static boolean select(String string, String... patterns) {
        boolean match = false;
        for (String pattern : patterns) {
            if (pattern.startsWith("!")) {
                if (Wildcard.match(string, pattern.substring(1))) {
                    return false;
                }
            } else {
                match |= Wildcard.match(string, pattern);
            }
        }
        return match;
    }

}
