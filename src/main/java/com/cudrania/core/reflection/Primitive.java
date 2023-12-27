package com.cudrania.core.reflection;

import lombok.SneakyThrows;

/**
 * 基本类型
 */
public enum Primitive {
    Boolean("boolean", java.lang.Boolean.class),
    Byte("byte", java.lang.Byte.class),
    Short("short", java.lang.Short.class),
    Integer("int", java.lang.Integer.class),
    Long("long", java.lang.Long.class),
    Float("float", java.lang.Float.class),
    Double("double", java.lang.Double.class),
    Character("char", java.lang.Character.class);
    public final String name;
    public final Class type;
    public final Class clazz;

    @SneakyThrows
    Primitive(String name, Class clazz) {
        this.name = name;
        this.clazz = clazz;
        this.type = (Class) clazz.getField("TYPE").get(null);
    }

    /**
     * 根据名称获取基本类型
     *
     * @param name
     * @return
     */
    public static Primitive get(String name) {
        for (Primitive primitive : Primitive.values()) {
            if (primitive.name.equals(name)) {
                return primitive;
            }
        }
        return null;
    }

    /**
     * 根据类型获取基本类型
     *
     * @param clazz
     * @return
     */
    public static Primitive get(Class clazz) {
        for (Primitive primitive : Primitive.values()) {
            if (primitive.clazz == clazz || primitive.type == clazz) {
                return primitive;
            }
        }
        return null;
    }

}