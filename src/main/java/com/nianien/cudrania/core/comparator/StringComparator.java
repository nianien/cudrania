package com.nianien.cudrania.core.comparator;

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
            return nullLast(str1, str2, String::compareToIgnoreCase);
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
            return nullLast(str1, str2, (a, b) -> b.compareToIgnoreCase(a));
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
            return nullLast(str1, str2, (a, b) -> Integer.compare(a.length(), b.length()));
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
            return nullLast(str1, str2, (a, b) -> Integer.compare(b.length(), a.length()));
        }
    };


    /**
     * null始终排在最后(与升序/降序无关): str1为null返回1(排后), str2为null返回-1(排前);
     * 两者均非null时按core比较. 这样保证四种排序下null都在最后, 且不违反Comparator契约.
     *
     * @param str1 参数1
     * @param str2 参数2
     * @param core 非null情形的比较逻辑(升序/降序由各枚举传入)
     * @return
     */
    private static int nullLast(String str1, String str2, Comparator<String> core) {
        if (str1 == null && str2 == null) return 0;
        if (str1 == null) return 1;
        if (str2 == null) return -1;
        return core.compare(str1, str2);
    }

}
