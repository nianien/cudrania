package com.cudrania.test.generic;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author skyfalling.
 */
public class InterfaceA2 implements InterfaceA<String> {
    public List<String> getStrings() {
        return new ArrayList<String>();
    }

    public static void main(String[] args) throws Exception {

        Method method = InterfaceA.class.getMethod("getStrings");
        System.out.println(getRunType(method));

//        System.out.println(InterfaceA2.genericType(InterfaceA2.class));
    }

    private static Class getRunType(Method method) {
        Type type = method.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getActualTypeArguments().length > 0) {
                Type gType = pType.getActualTypeArguments()[0];
                if (gType instanceof Class) {
                    return (Class) gType;
                }
                TypeVariable[] variables = method.getTypeParameters();
                if (variables.length > 0) {
                    Type[] bounds = variables[0].getBounds();
                    if (bounds.length > 0 && bounds[0] instanceof Class) {
                        return (Class) bounds[0];
                    }
                }
            }
        }
        return Object.class;
    }

//    public static Class genericType(Class clazz) {
//        Type[] type = clazz.getGenericInterfaces();
////        Type type = clazz.getGenericInterfaces();
//        if (type instanceof ParameterizedType) {
//
//        }
//        return Object.class;
//    }
//
//
//    public static Class genericType(Type type) {
//        ParameterizedType pType = (ParameterizedType) type;
//        // Comparator的泛型类型
//        if (pType.getActualTypeArguments().length > 0) {
//            Type gType = pType.getActualTypeArguments()[0];
//            if (gType instanceof Class) {
//                return (Class) gType;
//            }
//            TypeVariable[] variables = clazz.getTypeParameters();
//            if (variables.length > 0) {
//                Type[] bounds = variables[0].getBounds();
//                if (bounds.length > 0 && bounds[0] instanceof Class) {
//                    return (Class) bounds[0];
//                }
//            }
//        }
//        return null;
//    }


}
