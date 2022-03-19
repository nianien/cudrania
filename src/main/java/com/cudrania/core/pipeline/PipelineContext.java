package com.cudrania.core.pipeline;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * pipeline上下文,用于存取执行结果
 */
public class PipelineContext {

    private Map<String, Object> store = new HashMap<>();
    @Getter
    private Object[] last;
    @Getter
    private Object[] first;

    public void setLast(Object[] last) {
        this.last = last;
    }

    public void setFirst(Object[] first) {
        this.first = first;
        this.last = first;
    }


    public void put(String name, Object value) {
        store.put(name, value);
    }

    /**
     * 是否包含参数
     *
     * @param name
     * @return
     */
    public boolean has(String name) {
        return store.containsKey(name);
    }

    /**
     * 获取参数
     *
     * @param name
     * @return
     */
    public Object get(String name) {
        return store.get(name);
    }


}
