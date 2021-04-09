package com.cudrania.algorithm;

import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.text.MessageDigests;

import java.util.TreeMap;

/**
 * 一致性哈希实现<br>
 * 对给定的节点进行一致性散列,同时可设虚拟节点以保持均衡分布
 *
 * @author skyfalling
 */
public class ConsistentHash<T> {

    /**
     * 可排序的Map容器,存储虚拟节点到实际节点的映射
     */
    private TreeMap<Long, T> keysMap = new TreeMap<Long, T>();


    /**
     * 构造方法,指定需要散列的节点,以及每个节点对应的虚拟节点数
     *
     * @param nodes
     * @param size  每个节点所对应的虚拟节点数,如果值小于1,则默认为1
     */
    public ConsistentHash(Iterable<T> nodes, int size) {
        if (size < 1)
            size = 1;
        for (T key : nodes) {
            // 将key映射到虚拟节点
            String virtualKey = MessageDigests.md5(key.toString());
            for (int i = 0; i < size; i++) {
                // 计算第i个虚拟节点的哈希值
                long m = hashCode(virtualKey);
                keysMap.put(m, key);
                // 映射下一个虚拟节点
                virtualKey = MessageDigests.md5(virtualKey);
            }
        }
    }

    /**
     * 构造方法,指定需要散列的节点, 默认每个节点对应512个虚拟节点
     *
     * @param nodes
     */
    public ConsistentHash(Iterable<T> nodes) {
        this(nodes, 512);
    }

    /**
     * 根据请求数据获取命中的节点
     *
     * @param data
     * @return
     */
    public T getTarget(Object data) {
        Long key = hashCode(data.toString());
        if (!keysMap.containsKey(key)) {
            key = keysMap.ceilingKey(key);
            if (key == null) {
                key = keysMap.firstKey();
            }
        }
        return keysMap.get(key);
    }

    /**
     * 计算字符串的哈希值
     *
     * @param key
     * @return
     */
    private long hashCode(String key) {
        try {
            // 十六位字节数组
            byte[] bytes = MessageDigests.md5(key.getBytes());
            // 每隔四位取一个字节
            long code = ((long) (bytes[12] & 0xFF) << 24)
                    | ((long) (bytes[8] & 0xFF) << 16)
                    | ((long) (bytes[4] & 0xFF) << 8) | (bytes[0] & 0xFF);
            return code;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }
}
