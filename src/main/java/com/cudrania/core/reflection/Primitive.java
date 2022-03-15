package com.cudrania.core.reflection;

import com.cudrania.core.utils.Enums;

import static com.cudrania.core.exception.ExceptionChecker.throwException;

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
    String name;
    Class type;
    Class clazz;

    Primitive(String name, Class clazz) {
        try {
            this.name = name;
            this.clazz = clazz;
            this.type = (Class) clazz.getField("TYPE").get(null);
        } catch (Exception e) {
            throwException(e);
        }
    }

    public static Primitive get(String name) {
        return Enums.with(Primitive.class, "name", name);
    }

    public static Primitive get(Class clazz) {
        return Enums.with(Primitive.class, "clazz", clazz);
    }

}