package com.cudrania.spring.condition;

import com.nianien.core.text.Wildcard;
import com.nianien.core.util.StringUtils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.springframework.core.type.classreading.MethodMetadataReadingVisitor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
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

    /**
     * 任务列表
     */
    private static Map<String, TaskSpec> taskSpecs = new LinkedHashMap<>();

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (!metadata.isAnnotated(Task.class.getName())) {
            return false;
        }
        Map<String, Object> attributes = metadata.getAnnotationAttributes(Task.class.getName());
        String name = (String) attributes.get("name");
        if (StringUtils.isEmpty(name)) {
            if (metadata instanceof AnnotationMetadataReadingVisitor) {
                name = valueByComponent(metadata);
            } else if (metadata instanceof MethodMetadataReadingVisitor) {
                name = valueByBean(metadata);
            }
        }
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        String desc = StringUtils.defaultIfEmpty((String) attributes.get("desc"), "");
        String group = StringUtils.defaultIfEmpty((String) attributes.get("group"), "task");

        String[] tasks = context.getEnvironment().getProperty(group, "").split("[,;]");
        boolean selected = select(name, tasks);
        taskSpecs.put(name, new TaskSpec(name, group, desc, selected));
        return selected;
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
                if (Wildcard.match(pattern.substring(1), string)) {
                    return false;
                }
            } else {
                match |= Wildcard.match(pattern, string);
            }
        }
        return match;
    }


    /**
     * 获取索引扫描到的任务
     *
     * @return
     */
    public static Collection<TaskSpec> getTasks() {
        return taskSpecs.values();
    }


    /**
     * 打印任务列表
     *
     * @return
     */
    public static String showTasks() {
        StringBuilder sb = new StringBuilder("============================\n");

        String max = taskSpecs.keySet().stream().max(Comparator.comparingInt(String::length)).orElse("");
        sb.append(StringUtils.rightPad("name", max.length()))
                .append(" | ").append("group")
                .append(" | ").append("description")
                .append("\n============================\n");
        for (TaskSpec spec : taskSpecs.values()) {
            sb.append(StringUtils.rightPad(spec.name, max.length())).append(" | ").append(spec.group).append
                    (" | ")
                    .append(spec
                            .desc)
                    .append
                            ("\n");
        }
        return sb.toString();
    }

    public class TaskSpec {
        public final String name;
        public final String group;
        public final String desc;
        public final boolean enabled;

        TaskSpec(String name, String group, String desc, boolean enabled) {
            this.name = name;
            this.enabled = enabled;
            this.group = group;
            this.desc = desc;
        }

        @Override
        public String toString() {
            return "TaskSpec{" +
                    "name='" + name + '\'' +
                    ", group='" + group + '\'' +
                    ", desc='" + desc + '\'' +
                    ", enabled=" + enabled +
                    '}';
        }
    }
}
