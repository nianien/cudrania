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
 * 通过方法{@link #addScalar(String, String, Class)}可以设置字段类型以及别名,通过构造方法{@link #MapResultTransFormer(MapResultTransFormer.FieldHandler)}可以针对字段进行更加灵活的处理.<br/>
 * 需要注意的是,如果通过{@link #addScalar(String, String, Class)}方法设置了字段类型和别名,那么{@link MapResultTransFormer.FieldHandler#handle(String, Object, java.util.Map)}接口方法中的参数将变为置换后的别名和类型转换后字段值<br/>
 *
 * @author skyfalling
 * @see com.cudrania.hibernate.transformer.MapResultTransFormer.FieldHandler
 * @see com.cudrania.common.utils.CaseInsensitiveMap
 */
public class MapResultTransFormer extends BasicTransformerAdapter implements Serializable {
    /**
     * 字段处理接口定义
     */
    public static interface FieldHandler {
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
    private FieldHandler fieldHandler;

    /**
     * 字段别名类型映射
     */
    private Map<String, AliasType> aliasTypeMap = new HashMap<String, AliasType>();


    /**
     * 构造方法
     */
    public MapResultTransFormer() {
    }

    /**
     * 构造方法,指定字段处理对象
     *
     * @param fieldHandler 字段处理对象
     */
    public MapResultTransFormer(FieldHandler fieldHandler) {
        this.fieldHandler = fieldHandler;
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
            AliasType aliasType = getScalar(aliases[i]);
            if (aliasType != null) {
                name = aliasType.getAlias(name);
                value = aliasType.getValue(value);
            }
            if (fieldHandler != null) {
                if ((value = fieldHandler.handle(name, value, result)) != null) {
                    result.put(name, value);
                }
            } else {
                result.put(name, value);
            }
        }
        return result;
    }

    /**
     * 设置字段类型
     *
     * @param name 字段名, 不区分大小写
     * @param type 字段类型
     * @return
     */
    public MapResultTransFormer addScalar(String name, Class type) {
        return addScalar(name, name, type);
    }

    /**
     * 设置字段别名
     *
     * @param name 字段名, 不区分大小写
     * @return
     */
    public MapResultTransFormer addScalar(String name, String alias) {
        return addScalar(name, alias, null);
    }

    /**
     * 设置字段别名和类型
     *
     * @param name  字段名, 不区分大小写
     * @param alias 字段别名
     * @param type  字段类型
     * @return
     */
    public MapResultTransFormer addScalar(String name, String alias, Class type) {
        aliasTypeMap.put(name.toLowerCase(), new AliasType(alias, type));
        return this;
    }


    /**
     * 根据字段名获取AliasType对象
     *
     * @param name 字段名, 不区分大小写
     * @return
     */
    protected AliasType getScalar(String name) {
        return aliasTypeMap.get(name.toLowerCase());
    }


    /**
     * 字段别名和类型
     */
    private class AliasType {

        private String alias;
        private Class type;

        private AliasType(String alias, Class type) {
            this.alias = alias;
            this.type = type;
        }

        public String getAlias(String name) {
            return alias != null ? alias : name;
        }

        public Object getValue(Object value) {
            if (value != null && type != null && !type.isInstance(value)) {
                return ConvertUtils.convert(value, type);
            }
            return value;
        }
    }

}
