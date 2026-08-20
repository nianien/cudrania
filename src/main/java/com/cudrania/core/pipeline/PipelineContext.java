package com.cudrania.core.pipeline;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 单次 Pipeline 调用的执行上下文
 * <p>
 * 上下文保存初始参数、最近步骤输出以及通过 {@link Named} 命名的值
 * {@link Pipelines} 每次执行都会创建新实例，因此不同调用之间不会共享这里的数据
 * <pre>{@code
 * PipelineContext context = new PipelineContext();
 * context.setFirst(new Object[]{10, 20});
 * context.put("sum", 30);
 *
 * Object value = context.get("sum");
 * }</pre>
 * 该类型服务于执行器内部，不负责并发控制和参数类型校验
 */
public class PipelineContext {

    /**
     * 按名称保存的初始参数和步骤结果
     */
    private Map<String, Object> store = new HashMap<>();

    /**
     * 最近一次执行步骤的输出数组
     * <p>
     * 初始化时指向全部初始参数，步骤开始执行后通常只包含上一步的单个输出
     */
    @Getter
    private Object[] last;

    /**
     * 本次执行收到的原始初始参数
     */
    @Getter
    private Object[] first;

    /**
     * 更新最近一次执行步骤的输出
     *
     * @param last 最近输出数组
     */
    public void setLast(Object[] last) {
        this.last = last;
    }

    /**
     * 设置本次执行的初始参数
     * <p>
     * 初始化阶段还没有步骤结果，因此同时把最近输出设置为初始参数
     *
     * @param first 原始初始参数数组
     */
    public void setFirst(Object[] first) {
        this.first = first;
        this.last = first;
    }


    /**
     * 按名称保存初始参数或步骤结果
     *
     * @param name  上下文键
     * @param value 对应值
     */
    public void put(String name, Object value) {
        store.put(name, value);
    }

    /**
     * 判断上下文是否包含指定名称
     *
     * @param name 上下文键
     * @return 存在该键时返回 {@code true}
     */
    public boolean has(String name) {
        return store.containsKey(name);
    }

    /**
     * 获取指定名称对应的值
     *
     * @param name 上下文键
     * @return 对应值，不存在时返回 {@code null}
     */
    public Object get(String name) {
        return store.get(name);
    }


}
