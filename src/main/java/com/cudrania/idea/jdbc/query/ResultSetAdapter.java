package com.cudrania.idea.jdbc.query;

import com.cudrania.core.collection.map.CaseInsensitiveMap;
import com.cudrania.core.exception.ExceptionChecker;
import com.cudrania.core.reflection.Reflections;
import com.cudrania.idea.jdbc.table.DataTable;
import com.cudrania.idea.jdbc.table.DataTableFactory;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * ResultSet对象的转换类
 *
 * @author skyfalling
 */
public class ResultSetAdapter {
    /**
     * 获取结果集中指定列的数据
     *
     * @param resultSet
     * @param index     列的索引值
     * @return
     */
    public static List<Object> getColumns(ResultSet resultSet, int index) {
        try {
            List<Object> list = new ArrayList<Object>();
            while (resultSet.next()) {
                list.add(resultSet.getObject(index));
            }
            return list;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 获取结果集中指定列的数据
     *
     * @param resultSet
     * @param name      列名
     * @return
     */
    public static List<Object> getColumns(ResultSet resultSet, String name) {
        int index = getColumnIndex(resultSet, name);
        return getColumns(resultSet, index);
    }

    /**
     * 将结果集中指定的两列映射成Map对象
     *
     * @param resultSet
     * @param keyIndex   key列的索引
     * @param valueIndex value列的索引
     * @return
     */
    public static Map<Object, Object> getColumnsMap(ResultSet resultSet, int keyIndex, int valueIndex) {
        try {
            Map<Object, Object> map = new HashMap<Object, Object>();
            while (resultSet.next()) {
                map.put(resultSet.getObject(keyIndex), resultSet.getObject(valueIndex));
            }
            return map;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 将结果集中指定的两列映射成Map对象
     *
     * @param resultSet
     * @param keyName   key列的名称
     * @param valueName value列的名称
     * @return
     */
    public static Map<Object, Object> getColumnsMap(ResultSet resultSet, String keyName, String valueName) {
        int index1 = getColumnIndex(resultSet, keyName);
        int index2 = getColumnIndex(resultSet, valueName);
        return getColumnsMap(resultSet, index1, index2);
    }

    /**
     * 获取结果集中的第一条记录<br>
     *
     * @param resultSet
     * @return
     */
    public static Map<String, Object> getFirstRow(ResultSet resultSet) {
        try {
            Map<String, Object> map = null;
            if (resultSet.next()) {
                map = new CaseInsensitiveMap<String, Object>();
                ResultSetMetaData meta = resultSet.getMetaData();
                int count = meta.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    try {
                        map.put(meta.getColumnLabel(i), resultSet.getObject(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return map;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }

    }

    /**
     * 获取结果集中的第一条记录
     *
     * @param <T>
     * @param resultSet
     * @param clazz
     * @return
     */
    public static <T> T getFirstRow(ResultSet resultSet, Class<T> clazz) {
        try {
            if (resultSet.next()) {
                return getBean(clazz, resultSet);
            }
            return null;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 以Map对象的形式返回结果集
     *
     * @param resultSet
     * @return
     */
    public static List<Map<String, Object>> getRows(ResultSet resultSet) {
        try {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            ResultSetMetaData meta = resultSet.getMetaData();
            int count = meta.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> map = new CaseInsensitiveMap<String, Object>();
                for (int i = 1; i <= count; i++) {
                    map.put(meta.getColumnLabel(i), resultSet.getObject(i));
                }
                list.add(map);
            }
            return list;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 以指定类型的对象返回结果集
     *
     * @param <T>
     * @param resultSet
     * @param clazz
     * @return
     */
    public static <T> List<T> getRows(ResultSet resultSet, Class<T> clazz) {
        try {
            List<T> list = new ArrayList<T>();
            while (resultSet.next()) {
                list.add(getBean(clazz, resultSet));
            }
            return list;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }

    }

    /**
     * 将结果集中的数据从第start条开始取size条,start起始值为1
     *
     * @param <T>
     * @param resultSet
     * @param start
     * @param size
     * @param clazz
     * @return
     */
    public static <T> List<T> getRows(ResultSet resultSet, int start, int size, Class<T> clazz) {
        beforeRow(resultSet, start);
        try {
            List<T> list = new ArrayList<T>();
            int num = 0;
            while (resultSet.next() && num++ < size) {
                list.add(getBean(clazz, resultSet));
            }
            return list;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 将结果集中的数据从第start条开始取size条,start起始值为1
     *
     * @param resultSet
     * @param start
     * @param size
     * @return
     */
    public static List<Map<String, Object>> getRows(ResultSet resultSet, int start, int size) {
        beforeRow(resultSet, start);
        try {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            ResultSetMetaData meta = resultSet.getMetaData();
            int columnCount = meta.getColumnCount();
            int num = 0;
            while (resultSet.next() && num++ < size) {
                Map<String, Object> map = new CaseInsensitiveMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    map.put(meta.getColumnLabel(i), resultSet.getObject(i));
                }
                list.add(map);
            }
            return list;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }

    }

    /**
     * 将ResultSet中的记录以指定列为键值映射成Map&lt;Object, T>对象<br>
     * 其中key为第index列的值,value为记录对应的实例对象
     *
     * @param <T>
     * @param resultSet
     * @param index
     * @param clazz
     * @return Map&lt;String, T>
     */
    public static <T> Map<Object, T> getRowsMap(ResultSet resultSet, int index, Class<T> clazz) {
        try {
            Map<Object, T> map = new HashMap<Object, T>();
            while (resultSet.next()) {
                map.put(resultSet.getObject(index), getBean(clazz, resultSet));
            }
            return map;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 将ResultSet中的记录以指定列为键值映射成Map&lt;Object, T>对象<br>
     * 其中key为keyField列的值,value为记录对应的实例对象
     *
     * @param <T>
     * @param resultSet
     * @param name
     * @param clazz
     * @return Map&lt;Object, T>
     */
    public static <T> Map<Object, T> getRowsMap(ResultSet resultSet, String name, Class<T> clazz) {
        int index = getColumnIndex(resultSet, name);
        return getRowsMap(resultSet, index, clazz);
    }

    /**
     * 返回ResultSet结果集的行数<br>
     *
     * @param resultSet
     * @return 查询结果的记录数
     */
    public static int getRowsCount(ResultSet resultSet) {
        try {
            if (resultSet.getType() == ResultSet.TYPE_FORWARD_ONLY) {
                int num = 0;
                while (resultSet.next()) {
                    num++;
                }
                return num;
            } else {
                resultSet.last();
                return resultSet.getRow();
            }
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }

    /**
     * 将结果集游标移到指定行的前一行
     *
     * @param resultSet
     * @param rowNo     rowNo>0
     */
    private static void beforeRow(ResultSet resultSet, int rowNo) {
        ExceptionChecker.throwIf(rowNo < 1, "the value of rowNo cannot be low than 1: " + rowNo);
        try {
            if (resultSet.getType() == ResultSet.TYPE_FORWARD_ONLY) {
                if (rowNo > 1) {
                    int index = 0;
                    while (resultSet.next()) {
                        index++;
                        if (index < rowNo - 1) continue;
                    }
                }
            } else {
                if (rowNo > 1) {
                    resultSet.absolute(rowNo - 1);
                } else {
                    resultSet.beforeFirst();
                }
            }
        } catch (Exception e) {
            ExceptionChecker.throwException(e);
        }

    }


    /**
     * 根据列名获取列的索引值
     *
     * @param resultSet
     * @param fieldName
     * @return 字段索引
     */
    public static int getColumnIndex(ResultSet resultSet, String fieldName) {
        try {
            return resultSet.findColumn(fieldName);
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }


    /**
     * 获取查询结果的列名称
     *
     * @param resultSet
     * @return
     */
    private static List<String> getColumnsName(ResultSet resultSet) {
        try {
            List<String> columns = new ArrayList<String>();
            ResultSetMetaData meta = resultSet.getMetaData();
            int count = meta.getColumnCount();
            for (int i = 1; i <= count; i++) {
                columns.add(meta.getColumnLabel(i));
            }
            return columns;
        } catch (Exception e) {
            throw ExceptionChecker.throwException(e);
        }
    }


    /**
     * 将查询字段赋值到实体对象中
     *
     * @param beanClass
     * @param resultSet
     */
    protected static <T> T getBean(Class<T> beanClass, ResultSet resultSet) {
        T bean = Reflections.newInstance(beanClass);
        DataTable table = DataTableFactory.get(bean.getClass());
        final List<String> columns = getColumnsName(resultSet);
        for (String column : columns) {
            if (table.hasField(column)) {
                Object value = getFieldValue(table.getFieldType(column), column, resultSet);
                if (value != null) {
                    table.setField(bean, column, value);
                }
            }
        }
        return bean;
    }

    /**
     * 从查询结果中获取字段值
     *
     * @param type
     * @param fieldName
     * @param resultSet
     * @return
     */
    protected static Object getFieldValue(Class type, String fieldName, ResultSet resultSet) {
        Object value = null;
        try {
            // 获取参数类型
            if (type.isAssignableFrom(Array.class)) {
                value = resultSet.getArray(fieldName);
            } else if (type.isAssignableFrom(BigDecimal.class)) {
                value = resultSet.getBigDecimal(fieldName);
            } else if (type.isAssignableFrom(InputStream.class)) {
                value = resultSet.getBinaryStream(fieldName);
            } else if (type.isAssignableFrom(Blob.class)) {
                value = resultSet.getBlob(fieldName);
            } else if (type.isAssignableFrom(boolean.class)) {
                value = resultSet.getBoolean(fieldName);
            } else if (type.isAssignableFrom(byte.class)) {
                value = resultSet.getByte(fieldName);
            } else if (type.isAssignableFrom(byte[].class)) {
                value = resultSet.getBytes(fieldName);
            } else if (type.isAssignableFrom(Reader.class)) {
                value = resultSet.getCharacterStream(fieldName);
            } else if (type.isAssignableFrom(Clob.class)) {
                value = resultSet.getClob(fieldName);
            } else if (type.isAssignableFrom(Date.class)) {
                value = resultSet.getDate(fieldName);
            } else if (type.isAssignableFrom(double.class)) {
                value = resultSet.getDouble(fieldName);
            } else if (type.isAssignableFrom(float.class)) {
                value = resultSet.getFloat(fieldName);
            } else if (type.isAssignableFrom(int.class)) {
                value = resultSet.getInt(fieldName);
            } else if (type.isAssignableFrom(long.class)) {
                value = resultSet.getLong(fieldName);
            } else if (type.isAssignableFrom(Ref.class)) {
                value = resultSet.getRef(fieldName);
            } else if (type.isAssignableFrom(short.class)) {
                value = resultSet.getShort(fieldName);
            } else if (type.isAssignableFrom(SQLXML.class)) {
                value = resultSet.getSQLXML(fieldName);
            } else if (type.isAssignableFrom(String.class)) {
                value = resultSet.getString(fieldName);
            } else if (type.isAssignableFrom(Time.class)) {
                value = resultSet.getTime(fieldName);
            } else if (type.isAssignableFrom(Timestamp.class)) {
                value = resultSet.getTimestamp(fieldName);
            } else if (type.isAssignableFrom(URL.class)) {
                value = resultSet.getURL(fieldName);
            } else {
                // 用字符串构造的对象实例
                String args = resultSet.getString(fieldName);
                value = Reflections.newInstance(type, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
