package com.cudrania.hibernate.transformer;

import com.nianien.core.util.StringUtils;

import org.apache.commons.beanutils.ConvertUtils;
import org.hibernate.transform.BasicTransformerAdapter;

import java.io.Serializable;

/**
 * {@link org.hibernate.transform.ResultTransformer}的接口实现,取查询结果中的特定字段作为结果返回<br/>
 *
 * @author skyfalling
 */
public class FieldResultTransFormer extends BasicTransformerAdapter implements Serializable {

    /**
     * 字段在查询列中的索引位置
     */
    private int index;
    /**
     * 字段名称,不分大小写
     */
    private String name;
    /**
     * 字段类型
     */
    private Class fieldType;

    /**
     * 默认取第一个字段,不进行类型转换
     */
    public FieldResultTransFormer() {
    }

    /**
     * 默认取第一个字段,并进行类型转换
     *
     * @param fieldType 字段类型,若为null则不进行类型转换
     */
    public FieldResultTransFormer(Class fieldType) {
        this(0, null, fieldType);
    }

    /**
     * 取指定索引位置的字段,并进行类型转换
     *
     * @param fieldIndex 字段索引位置
     * @param fieldType  字段类型,若为null则不进行类型转换
     */
    public FieldResultTransFormer(int fieldIndex, Class fieldType) {
        this(fieldIndex, null, fieldType);
    }

    /**
     * 指定查询字段名称,并进行类型转换
     *
     * @param fieldNameCaseIgnore 字段名,不分大小写
     * @param fieldType           字段类型,若为null则不进行类型转换
     */
    public FieldResultTransFormer(String fieldNameCaseIgnore, Class fieldType) {
        this(0, fieldNameCaseIgnore, fieldType);
    }

    /**
     * 指定字段索引位置,字段名称以及字段类型
     *
     * @param index
     * @param nameCaseIgnore
     * @param fieldType
     */
    private FieldResultTransFormer(int index, String nameCaseIgnore, Class fieldType) {
        this.index = index;
        this.name = nameCaseIgnore;
        this.fieldType = fieldType;
    }


    /**
     * 取指定的字段,并进行类型转换
     * {@inheritDoc}
     */
    public final Object transformTuple(Object[] tuple, String[] aliases) {
        Object fieldOne = tuple[index];
        if (StringUtils.isNotBlank(name)) {
            for (int j = 0; j < aliases.length; j++) {
                if (name.equalsIgnoreCase(aliases[j])) {
                    fieldOne = tuple[j];
                }
            }
        }
        return fieldType != null ? convert(fieldOne, fieldType) : fieldOne;
    }

    /**
     * 将指定对象转换为目标类型
     *
     * @param srcObject
     * @param targetType
     * @return
     */
    public Object convert(Object srcObject, Class targetType) {
        return ConvertUtils.convert(srcObject, targetType);
    }
}
