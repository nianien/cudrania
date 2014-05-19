package com.cudrania.hibernate.transformer;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.BasicTransformerAdapter;

import java.io.Serializable;

/**
 * 获取单个字段的对象转换器,将查询结果中指定的字段进行转换并返回
 *
 * @author skyfalling
 */
public class FieldResultTransFormer extends BasicTransformerAdapter implements Serializable {

    private int index;
    private String name;
    private Class fieldType;

    /**
     * 默认取第一个字段,不进行类型转换
     */
    public FieldResultTransFormer() {
    }

    /**
     * 默认取第一个字段
     *
     * @param fieldType
     */
    public FieldResultTransFormer(Class fieldType) {
        this(0, null, fieldType);
    }

    /**
     * 指定查询字段索引位置
     *
     * @param fieldIndex
     * @param fieldType
     */
    public FieldResultTransFormer(int fieldIndex, Class fieldType) {
        this(fieldIndex, null, fieldType);
    }

    /**
     * 指定查询字段名称,不分大小写
     *
     * @param fieldNameCaseIgnore
     * @param fieldType
     */
    public FieldResultTransFormer(String fieldNameCaseIgnore, Class fieldType) {
        this(0, fieldNameCaseIgnore, fieldType);
    }

    private FieldResultTransFormer(int index, String nameCaseIgnore, Class fieldType) {
        this.index = index;
        this.name = nameCaseIgnore;
        this.fieldType = fieldType;
    }


    /**
     * 字段转换
     * {@inheritDoc}
     */
    public final Object transformTuple(Object[] tuple, String[] aliases) {
        try {
            Object fieldOne = tuple[index];
            if (StringUtils.isNotBlank(name)) {
                for (int j = 0; j < aliases.length; j++) {
                    if (name.equalsIgnoreCase(aliases[j])) {
                        fieldOne = tuple[j];
                    }
                }
            }
            return fieldType != null ? convert(fieldOne, fieldType) : fieldOne;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
