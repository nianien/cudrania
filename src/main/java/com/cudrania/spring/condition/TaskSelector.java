package com.cudrania.spring.condition;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.springframework.core.type.classreading.MethodMetadataReadingVisitor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.nianien.core.text.RegexUtils.matchWildcard;
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
        Environment environment = context.getEnvironment();
        Map<String, Object> attributes = metadata.getAnnotationAttributes(Task.class.getName());
        String[] values = getTaskNames(metadata, new Class[]{
                Task.class,
                Component.class,
                Bean.class
        });

        String key = (String) attributes.get("key");
        if (StringUtils.isEmpty(key)) {
            key = "task";
        }
        String[] tasks = environment.getProperty(key, "").split("[,;]");
        return select(values, tasks);
    }

    /**
     * 获取任务名字列表
     *
     * @param metadata
     * @param classes
     * @return
     */
    private String[] getTaskNames(AnnotatedTypeMetadata metadata, Class<? extends Annotation>[] classes) {
        for (Class<? extends Annotation> clazz : classes) {
            List<String> names = new ArrayList<>();
            if (metadata.isAnnotated(clazz.getName())) {
                Object value = metadata.getAnnotationAttributes(clazz
                        .getName())
                        .get("value");
                if (value instanceof String) {
                    names.add((String) value);
                } else if (value instanceof String[]) {
                    names.addAll(Arrays.asList((String[]) value));
                }
            }
            names.remove("");
            if (!names.isEmpty()) {
                return names.toArray(new String[0]);
            }
        }
        if (metadata instanceof AnnotationMetadataReadingVisitor) {
            String shortClassName = getShortName(((AnnotationMetadataReadingVisitor) metadata).getClassName());
            return new String[]{decapitalize(shortClassName)};
        } else if (metadata instanceof MethodMetadataReadingVisitor) {
            return new String[]{decapitalize(((MethodMetadataReadingVisitor) metadata).getMethodName())};
        }
        return new String[0];
    }

    private static boolean select(String[] strings, String... patterns) {
        for (String string : strings) {
            if (select(string, patterns)) {
                return true;
            }
        }
        return false;
    }

    private static boolean select(String string, String... patterns) {
        boolean match = false;
        for (String pattern : patterns) {
            if (pattern.startsWith("!")) {
                if (matchWildcard(pattern.substring(1), string)) {
                    return false;
                }
            } else {
                match |= matchWildcard(pattern, string);
            }
        }
        return match;

    }

}
