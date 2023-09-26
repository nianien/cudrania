package com.cudrania.core.collection.wrapper;

import com.cudrania.core.collection.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 定义绑定到{@link Map<K, V>}对象的函数集合
 *
 * @param <K>
 * @param <V>
 */
public interface MapSupplier<K, V> extends Supplier<Map<K, V>> {


    /**
     * 翻转键值对
     *
     * @return
     */
    default Map<V, List<K>> invert() {
        return CollectionUtils.groupBy(get().entrySet(), Map.Entry::getValue, Map.Entry::getKey);
    }


    /**
     * 翻转键值对，翻转后如果健值冲突，则进行覆盖。
     *
     * @return
     */
    default Map<V, K> invertUnique() {
        return CollectionUtils.map(get().entrySet(), Map.Entry::getValue, Map.Entry::getKey);
    }

    /**
     * 分批处理键值对
     *
     * @param limit
     * @param consumer
     */
    default void doBatch(int limit, Consumer<Map<K, V>> consumer) {
        Map<K, V> subMap = new HashMap<>(limit);
        for (Map.Entry<K, V> entry : get().entrySet()) {
            subMap.put(entry.getKey(), entry.getValue());
            if (subMap.size() == limit) {
                consumer.accept(subMap);
                subMap = new HashMap<>(limit);
            }
        }
        if (subMap.size() > 0) {
            consumer.accept(subMap);
        }
    }


}
