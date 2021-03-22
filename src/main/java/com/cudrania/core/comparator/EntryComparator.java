package com.cudrania.core.comparator;

import java.util.Comparator;
import java.util.Map.Entry;

/**
 * Entry对象的比较类
 *
 * @author skyfalling
 */
public enum EntryComparator implements Comparator<Entry<?, ?>> {

    /**
     * 按Key升序
     */
    KeyAsc {
        /**
         * 按照指定的排序方式进行比较
         */
        @Override
        public int compare(Entry<?, ?> d1, Entry<?, ?> d2) {
            return compareByKey(d1, d2);
        }
    },


    /**
     * 按Key降序
     */
    KeyDesc {
        /**
         * 按照指定的排序方式进行比较
         */
        @Override
        public int compare(Entry<?, ?> d1, Entry<?, ?> d2) {
            return compareByKey(d2, d1);
        }
    },

    /**
     * 按Value升序
     */
    ValueAsc {
        /**
         * 按照指定的排序方式进行比较
         */
        @Override
        public int compare(Entry<?, ?> d1, Entry<?, ?> d2) {
            return compareByValue(d1, d2);
        }
    },

    /**
     * 按Value降序
     */
    ValueDesc {
        /**
         * 按照指定的排序方式进行比较
         */
        @Override
        public int compare(Entry<?, ?> d1, Entry<?, ?> d2) {
            return compareByValue(d2, d1);
        }
    },

    /**
     * 先按Value升序再按Key升序
     */
    ValueAscKeyAsc {
        /**
         * 按照指定的排序方式进行比较
         */
        @Override
        public int compare(Entry<?, ?> d1, Entry<?, ?> d2) {
            int n = compareByValue(d1, d2);
            return n == 0 ? compareByKey(d1, d2) : n;
        }
    },

    /**
     * 先按Value升序再按Key降序
     */
    ValueAscKeyDesc {
        /**
         * 按照指定的排序方式进行比较
         */
        @Override
        public int compare(Entry<?, ?> d1, Entry<?, ?> d2) {
            int n = compareByValue(d1, d2);
            return n == 0 ? compareByKey(d2, d1) : n;
        }
    },

    /**
     * 先按Value降序再按Key升序
     */
    ValueDescKeyAsc {
        /**
         * 按照指定的排序方式进行比较
         */
        @Override
        public int compare(Entry<?, ?> d1, Entry<?, ?> d2) {
            int n = compareByValue(d2, d1);
            return n == 0 ? compareByKey(d1, d2) : n;
        }
    },

    /**
     * 先按Value降序再按Key降序
     */
    ValueDescKeyDesc {
        /**
         * 按照指定的排序方式进行比较
         */
        @Override
        public int compare(Entry<?, ?> d1, Entry<?, ?> d2) {
            int n = compareByValue(d2, d1);
            return n == 0 ? compareByKey(d2, d1) : n;
        }
    };


    /**
     * 按key升序
     *
     * @param d1
     * @param d2
     * @return
     */
    @SuppressWarnings("unchecked")
    private static int compareByKey(Entry<?, ?> d1, Entry<?, ?> d2) {
        Object key1 = d1.getKey();
        Object key2 = d2.getKey();
        return ((Comparable<Object>) key1).compareTo(key2);
    }

    /**
     * 按value升序
     *
     * @param d1
     * @param d2
     * @return
     */
    @SuppressWarnings("unchecked")
    private static int compareByValue(Entry<?, ?> d1, Entry<?, ?> d2) {
        Object value1 = d1.getValue();
        Object value2 = d2.getValue();
        return ((Comparable<Object>) value1).compareTo(value2);
    }
}
