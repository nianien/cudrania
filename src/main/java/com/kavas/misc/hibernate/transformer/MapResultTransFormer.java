package com.kavas.misc.hibernate.transformer;

import com.kavas.misc.utils.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.BasicTransformerAdapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 查询结果存储Map中,其中key为字段名,value为字段值,key值不区分大小写
 *
 * @author skyfalling
 * @see  CaseInsensitiveMap
 */
public class MapResultTransFormer extends BasicTransformerAdapter implements Serializable {
    /**
     * 查询列处理类
     */
    public static interface ColumnHandler {
        /**
         * 处理字段,当改方法返回值不为null时,则返回结果被自动存储到result对象中,其中key为 name<br/>
         * 实现类也可以在方法中自定义存储处理结果,并返回null,禁止{@link MapResultTransFormer}对象自动存储
         *
         * @param name   查询字段名称
         * @param value  查询字段值
         * @param result 存储查询结果的Map对象
         * @return
         */
        Object handle(String name, Object value, Map<String, Object> result);
    }

    /**
     * 字段处理对象
     */
    protected ColumnHandler columnHandler;
    /**
     * 别名到属性名称的映射
     */
    protected Map<String, String> aliasMap = new HashMap<String, String>();


    /**
     * 构造方法
     */
    public MapResultTransFormer() {
    }

    /**
     * @param columnHandler 字段处理对象,根据字段映射后的名称和字段值进行结果处理
     */
    public MapResultTransFormer(ColumnHandler columnHandler) {
        this.columnHandler = columnHandler;
    }


    /**
     * 根据指定别名列表修正对应数据库查询字段的大小写<br/>
     * 例如: 当构造参数为List("userName","userId")时,对于SQL查询:<code> select username,userID from table</code> 返回字段名称列表为userName,userId<br/>
     * 该构造方法特别适用于<code> select * from table </code>需要修正个别字段大小写的情况
     *
     * @param aliases       指定别名映射
     * @param columnHandler 字段处理对象,根据字段映射后的名称和字段值进行结果处理
     */
    public MapResultTransFormer(List<String> aliases, ColumnHandler columnHandler) {
        this(columnHandler);
        for (String alias : aliases) {
            setAlias(alias, alias);
        }
    }

    /**
     * 根据字段别名映射修改数据库查询字段的名称<br/>
     * 例如: 当构造参数为Map("userName":"name","userId":"id")时,对于SQL查询:<code> select username,userID from table</code> 返回字段名称列表为name,id<br/>
     * 该构造方法特别适用于<code> select * from table </code>需要改变个别字段名称的情况<br/>
     * 注意:参数Map的key值不分大小写
     *
     * @param aliasMap      指定别名映射
     * @param columnHandler 字段处理对象,根据字段映射后的名称和字段值进行结果处理
     */
    public MapResultTransFormer(Map<String, String> aliasMap, ColumnHandler columnHandler) {
        this(columnHandler);
        for (Entry<String, String> entry : aliasMap.entrySet()) {
            setAlias(entry.getKey(), entry.getValue());
        }
    }


    /**
     * 返回Map对象,键值为数据库查询字段别名,不区分大小写
     * {@inheritDoc}
     */
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map result = new CaseInsensitiveMap(tuple.length);
        for (int i = 0; i < tuple.length; i++) {
            String name = getAlias(aliases[i]);
            Object value = tuple[i];
            if (columnHandler != null) {
                try {
                    if ((value = columnHandler.handle(name, value, result)) != null) {
                        result.put(name, value);
                    }
                } catch (Exception e) {
                    System.err.println("failed to handle alias[" + name + "] with value[" + value + "]");
                }
            } else {
                result.put(name, value);
            }
        }
        return result;
    }


    /**
     * @param columnHandler 查询字段处理对象,根据字段名和字段值返回处理结果
     */
    public MapResultTransFormer setColumnHandler(ColumnHandler columnHandler) {
        this.columnHandler = columnHandler;
        return this;
    }


    /**
     * 为查询字段设置别名
     *
     * @param name
     */
    protected void setAlias(String name, String alias) {
        aliasMap.put(name.toLowerCase(), alias);
    }

    /**
     * 根据查询字段获取别名
     *
     * @param name
     * @return
     */
    protected String getAlias(String name) {
        return StringUtils.defaultIfEmpty(aliasMap.get(name.toLowerCase()), name);
    }

}
