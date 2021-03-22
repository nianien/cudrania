package com.cudrania.core.comparator;

import java.util.Comparator;

/**
 * 字符串比较类,可满足四种排序方式:<br>
 * 按字典顺序,按字典逆序,按长度升序,按长度降序<br>
 * 无论哪种排序,null都将排在最后,亦即null的值随排序方式不同而不同
 *
 * @author skyfalling
 */
public enum StringComparator implements Comparator<String> {

    /**
     * 按字典顺序排序
     */
    DictionaryAsc {
        /**
         * 根据指定排序方式进行排序,null值排在最后
         */
        @Override
        public int compare(String str1, String str2) {
            return compareD(str1, str2);
        }
    },
    /**
     * 按字典逆序排序
     */
    DictionaryDesc {
        /**
         * 根据指定排序方式进行排序,null值排在最后
         */
        @Override
        public int compare(String str1, String str2) {
            return compareD(str2, str1);
        }
    },
    /**
     * 按长度升序排序
     */
    LengthAsc {
        /**
         * 根据指定排序方式进行排序,null值排在最后
         */
        @Override
        public int compare(String str1, String str2) {
            return compareL(str1, str2);
        }
    },
    /**
     * 按长度降序排序
     */
    LengthDesc {
        /**
         * 根据指定排序方式进行排序,null值排在最后
         */
        @Override
        public int compare(String str1, String str2) {
            return compareL(str2, str1);
        }
    };


    /**
     * 按字典顺序排序
     *
     * @param str1
     * @param str2
     * @return
     */
    private static int compareD(String str1, String str2) {
        return str1 == null ? 1 : str2 == null ? 1 : str1
                .compareToIgnoreCase(str2);
    }

    /**
     * 按长度升序排序
     *
     * @param str1
     * @param str2
     * @return
     */
    private static int compareL(String str1, String str2) {
        return str1 == null ? 1 : str2 == null ? 1 : str1.length()
                - str2.length();
    }

}
