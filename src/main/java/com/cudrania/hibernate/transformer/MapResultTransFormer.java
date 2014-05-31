package com.cudrania.hibernate.transformer;

import com.cudrania.common.utils.CaseInsensitiveMap;
import org.apache.commons.beanutils.ConvertUtils;
import org.hibernate.transform.BasicTransformerAdapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link org.hibernate.transform.ResultTransformer}的接口实现,查询结果以Map的形式返回.<br/>
 * 这里的Map对象为{@link CaseInsensitiveMap}实例,查询时,key值不区分大小写,但输出时,保证原key不变.<br/>
 * 通过方法{@link #setType(String, Class)}可以设置查询字段类型,通过构造方法{@link #MapResultTransFormer(com.cudrania.hibernate.transformer.MapResultTransFormer.ColumnHandler)}可以针对字段进行更加灵活的处理.<br/>
 * 需要注意的是,如果通过{@link #setType(String, Class)}方法设置了字段类型,那么{@link ColumnHandler#handle(String, Object, java.util.Map)}接口方法中的字段值已经进行了类型转换.<br/>
 *
 * @author skyfalling
 * @see ColumnHandler
 * @see com.cudrania.common.utils.CaseInsensitiveMap
 */
public class MapResultTransFormer extends BasicTransformerAdapter implements Serializable {
    /**
     * 字段处理接口定义
     */
    public static interface ColumnHandler {
        /**
         * 字段处理,当该方法返回值不为null时,则返回结果将以name为key值存储到result对象中<br/>
         *
         * @param name   字段名称
         * @param value  字段值
         * @param result Map对象,作为一条查询记录返回
         * @return 字段处理结果, 如果不为null, 则将结果将以name为key值存储到result对象中
         */
        Object handle(String name, Object value, Map<String, Object> result);
    }

    /**
     * 字段处理对象
     */
    protected ColumnHandler columnHandler;

    /**
     * 字段类型映射
     */
    private Map<String, Class> typesMap = new HashMap<String, Class>();

    /**
     * 构造方法
     */
    public MapResultTransFormer() {
    }

    /**
     * 构造方法,指定字段处理对象
     *
     * @param columnHandler 字段处理对象
     */
    public MapResultTransFormer(ColumnHandler columnHandler) {
        this.columnHandler = columnHandler;
    }

    /**
     * 设置字段类型
     *
     * @param name 字段名, 不区分大小写
     * @param type 字段类型
     * @return
     */
    public MapResultTransFormer setType(String name, Class type) {
        typesMap.put(name.toLowerCase(), type);
        return this;
    }


    /**
     * 设置字段处理对象
     *
     * @param columnHandler
     */
    public MapResultTransFormer setColumnHandler(ColumnHandler columnHandler) {
        this.columnHandler = columnHandler;
        return this;
    }

    /**
     * 返回Map对象,键值为数据库查询字段别名,不区分大小写
     * {@inheritDoc}
     */
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map result = new CaseInsensitiveMap(tuple.length);
        for (int i = 0; i < tuple.length; i++) {
            String name = aliases[i];
            Object value = tuple[i];
            Class type = typesMap.get(name);
            if (type != null && !type.isInstance(value)) {
                value = ConvertUtils.convert(value, type);
            }
            if (columnHandler != null) {
                if ((value = columnHandler.handle(name, value, result)) != null) {
                    result.put(name, value);
                }
            } else {
                result.put(name, value);
            }
        }
        return result;
    }


}
