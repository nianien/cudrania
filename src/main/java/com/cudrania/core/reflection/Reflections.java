package com.cudrania.core.reflection;

import com.cudrania.core.annotation.Property;
import com.cudrania.core.collection.CollectionUtils;
import com.cudrania.core.utils.Enums;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;

import static com.cudrania.core.exception.ExceptionChecker.throwException;
import static com.cudrania.core.exception.ExceptionChecker.throwIfNull;
import static com.cudrania.core.utils.StringUtils.deCapitalize;

/**
 * 反射工具类
 *
 * @author skyfalling
 */
public class Reflections {

    /**
     * 基本类型
     */
    public static enum Primitive {
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

    /**
     * 获取指定类型的注解
     *
     * @param annotatedElement
     * @param annotationClass
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T findAnnotation(AnnotatedElement annotatedElement, Class<T> annotationClass) {
        try {
            return annotatedElement.getAnnotation(annotationClass);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }


    /**
     * 是否声明指定类型的注解
     *
     * @return
     */
    public static <T extends Annotation> boolean hasAnnotation(AnnotatedElement annotatedElement,
                                                               Class<T> annotationClass) {
        return annotatedElement.isAnnotationPresent(annotationClass);
    }

    /**
     * 查找指定名称和参数类型的方法<br/>
     * 首先查找该类的public方法,如果不存在,则查找该类声明的方法,如果不存在,则递归查找父类声明的方法<br/>
     * 如果查找不到相应的方法,则返回null
     *
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            for (; clazz != null; clazz = clazz.getSuperclass()) {
                try {
                    return clazz.getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException ex) {
                    //ignore
                }
            }
            return null;
        }
    }


    /**
     * 获取clazz及其父类声明的public方法
     *
     * @param clazz
     * @return
     */
    public static List<Method> getMethods(Class<?> clazz) {
        return getMethods(clazz, Object.class, IS_STATIC_METHOD.negate());
    }


    /**
     * 获取clazz及其父类声明的符合条件的方法
     *
     * @param clazz
     * @param filter
     * @return
     */
    public static List<Method> getMethods(Class<?> clazz, Class baseExcludeClass, Predicate<Method> filter) {
        filter = nullable(filter);
        List<Method> list = new ArrayList<Method>();
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            if (clazz == baseExcludeClass) {
                break;
            }
            Arrays.stream(clazz.getDeclaredMethods()).filter(filter).forEach(list::add);
        }
        return list;
    }


    /**
     * 查找指定名称的字段<br/>
     * 首先查找该类的public字段,如果不存在,则查找该类声明的字段,如果不存在,则递归查找父类声明的字段<br/>
     * 如果查找不到相应的字段,则返回null
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            for (; clazz != null; clazz = clazz.getSuperclass()) {
                try {
                    return clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ex) {
                    //ignore
                }
            }
            return null;
        }

    }

    /**
     * 获取clazz及其父类声明的public字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getFields(Class<?> clazz) {
        return getFields(clazz, Object.class, null);
    }


    /**
     * 获取clazz及其父类声明的符合条件的成员字段, 不包含excludeBaseClass及其父类声明的字段
     *
     * @param clazz
     * @param baseExcludeClass
     * @param filter
     * @return
     */
    public static List<Field> getFields(Class<?> clazz, Class baseExcludeClass, Predicate<Field> filter) {
        filter = nullable(filter);
        List<Field> list = new ArrayList<>();
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            if (clazz == baseExcludeClass) {
                break;
            }
            Arrays.stream(clazz.getDeclaredFields()).filter(filter).forEach(list::add);
        }
        return list;
    }

    /**
     * 获取getter或setter方法对应的属性名称<br/>
     * 如果方法声明了{@link com.cudrania.core.annotation.Property}注解,则以注解为准; 否则按照getter和setter规则取其属性
     *
     * @param element
     * @return getter或setter方法对应的属性名<br />
     * 注意:这里只对形如getXxx()或isXxx()或SetXxx()的方法有效,对应属性名为xxx<br/>
     */
    public static String propertyName(AnnotatedElement element) {
        Property property = element.getAnnotation(Property.class);
        if (property != null && !property.value().isEmpty()) {
            return property.value();
        }
        if (element instanceof Field) {
            return ((Field) element).getName();
        } else if (element instanceof Method) {
            String name = ((Method) element).getName();
            return deCapitalize(name.substring(name.startsWith("is") ? 2 : 3));
        } else {
            throw new UnsupportedOperationException("illegal annotated type:" + element.getClass());
        }
    }

    /**
     * 执行指定方法,返回执行结果
     *
     * @param method
     * @param bean   声明该方法的实例对象
     * @param args   方法的参数
     * @return 方法的执行结果<br />
     */
    public static Object invoke(Method method, Object bean, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(bean, args);
        } catch (Exception e) {
            throw throwException(e);
        }
    }

    /**
     * 根据方法名和参数调用指定方法<br/>
     * 如果方法名称以及参数长度匹配且唯一，此时若类型不匹配，则会尝试类型转换<br/>
     *
     * @param methodName 方法名称
     * @param bean       方法关联的对象
     * @param parameters 实际参数值
     * @return
     */
    public static Object invoke(String methodName, Object bean, Object[] parameters) {
        Class beanClass = bean.getClass();
        Class<?>[] types = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            types[i] = parameters[i].getClass();
        }
        Method method = getMethod(beanClass, methodName, types);
        if (method != null) {
            return invoke(method, bean, parameters);
        }
        List<Method> methods = getMethods(beanClass);
        Iterator<Method> iterator = methods.iterator();
        while (iterator.hasNext()) {
            Method candidate = iterator.next();
            if (candidate.getName().equals(methodName) && candidate.getParameterTypes().length == parameters.length) {
                if (instanceOfTypes(candidate.getParameterTypes(), parameters)) {
                    return invoke(candidate, bean, parameters);
                }
            } else {
                iterator.remove();
            }
        }
        if (methods.size() != 1) {
            throw new IllegalArgumentException(
                    "no suitable method [" + methodName + "] for class [" + beanClass + "] with parameters: "
                            + Arrays.toString(parameters));
        }
        method = methods.get(0);
        Class[] parameterTypes = method.getParameterTypes();
        // 构造参数转型
        Object[] castParams = cast(parameters, parameterTypes);
        return invoke(method, bean, castParams);
    }

    /**
     * 根据方法名称和参数类型调用指定方法
     * 如果方法名称以及参数长度匹配且唯一，此时若类型不匹配，则会尝试类型转换<br/>
     *
     * @param methodName     方法名称
     * @param bean           方法关联的对象
     * @param parameters     实际参数值
     * @param parameterTypes 方法参数类型
     * @return
     */
    public static Object invoke(String methodName, Object bean, Object[] parameters, Class[] parameterTypes) {
        if (parameterTypes.length != parameters.length) {
            throw new IllegalArgumentException("parameters and parameterTypes must have same size!");
        }
        Class beanClass = bean.getClass();
        Method method = getMethod(beanClass, methodName, parameterTypes);
        // 构造参数转型
        Object[] castParams = cast(parameters, parameterTypes);
        return invoke(method, bean, castParams);
    }


    /**
     * 批量类型转换
     *
     * @param parameters
     * @param parameterTypes
     * @return
     */
    private static Object[] cast(Object[] parameters, Class[] parameterTypes) {
        Object[] castParams = new Object[parameters.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].isInstance(parameters[i])) {
                castParams[i] = parameters[i];
            } else {
                castParams[i] = simpleInstance(parameterTypes[i], parameters[i].toString());
            }
        }
        return castParams;
    }


    /**
     * 获取public getter方法, 如果不存在,则返回null
     *
     * @param clazz
     * @param propertyName 属性名称,注解{@link Property}的优先级高于方法名
     * @return
     */
    public static Method getter(Class<?> clazz, String propertyName) {
        return getters(clazz, m -> propertyName(m).equals(propertyName))
                .stream().findAny().orElse(null);
    }

    /**
     * 获取public getter方法, 如果不存在,则返回null
     *
     * @param clazz
     * @param propertyName 属性名称,注解{@link Property}的优先级高于方法名
     * @param propertyType 属性类型
     * @return
     */
    public static Method getter(Class<?> clazz, String propertyName, Class<?> propertyType) {
        Method getter = getter(clazz, propertyName);
        if (getter != null && getter.getReturnType() == propertyType) {
            return getter;
        }
        return null;
    }

    /**
     * 获取setter方法, 如果不存在,则返回null
     *
     * @param clazz
     * @param propertyName 属性名称,注解{@link Property}的优先级高于方法名
     * @return
     */
    public static Method setter(Class<?> clazz, String propertyName) {
        return setters(clazz, m -> propertyName(m).equals(propertyName))
                .stream().findAny().orElse(null);
    }

    /**
     * 获取setter方法, 如果不存在,则返回null
     *
     * @param clazz
     * @param propertyName 属性名称,注解{@link Property}的优先级高于方法名
     * @param propertyType 属性类型
     * @return
     */
    public static Method setter(Class<?> clazz, String propertyName, Class<?> propertyType) {
        Method setter = getter(clazz, propertyName);
        if (setter != null && setter.getParameterTypes()[0] == propertyType) {
            return setter;
        }
        return null;
    }

    /**
     * 获取setter方法, 如果不存在,则返回null
     *
     * @param clazz
     * @param propertyName  属性名称,注解{@link Property}的优先级高于方法名
     * @param propertyValue 属性值
     * @return
     */
    public static Method setter(Class<?> clazz, String propertyName, Object propertyValue) {
        Method setter = getter(clazz, propertyName);
        if (setter != null && setter.getParameterTypes()[0].isInstance(propertyValue)) {
            return setter;
        }
        return null;
    }


    /**
     * 获取getter方法列表,不包含{@link Object}声明的getter方法
     *
     * @param clazz
     * @return getter方法列表
     */
    public static List<Method> getters(Class<?> clazz) {
        return getters(clazz, null);
    }

    /**
     * 获取符合条件的getter方法列表,不包含{@link Object}声明的getter方法
     *
     * @param clazz
     * @return getter方法列表
     */
    public static List<Method> getters(Class<?> clazz, Predicate<Method> filter) {
        return getMethods(clazz, Object.class, IS_GETTER.and(filter));
    }

    /**
     * 获取setter方法列表,不包含{@link Object}声明的setter方法
     *
     * @param clazz
     * @return getter方法列表
     */
    public static List<Method> setters(Class<?> clazz) {
        return setters(clazz, null);
    }

    /**
     * 获取符合条件的setter方法列表,不包含{@link Object}声明的setter方法
     *
     * @param clazz
     * @return getter方法列表
     */
    public static List<Method> setters(Class<?> clazz, Predicate<Method> filter) {
        return getMethods(clazz, null, IS_SETTER.and(nullable(filter)));
    }

    /**
     * 根据map的key为bean的同名属性赋值
     *
     * @param <T>
     * @param bean
     * @param map
     * @return bean
     */
    public static <T> T setProperties(T bean, Map<String, ?> map) {
        List<Method> methods = setters(bean.getClass());
        for (Method method : methods) {
            String key = propertyName(method);
            Object value = map.get(key);
            if (value != null && instanceOf(method.getParameterTypes()[0], value)) {
                invoke(method, bean, value);
            }
        }
        return bean;
    }

    /**
     * 获取bean的属性集合,属性名称作为键值<br/>
     * 注: 这里不包括null属性以及{@link Object}声明的属性
     *
     * @param bean
     * @return Map对象
     */
    public static Map<String, Object> getProperties(Object bean) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Method> methods = getters(bean.getClass());
        for (Method method : methods) {
            Object value = invoke(method, bean);
            if (value != null) {
                map.put(propertyName(method), value);
            }
        }
        return map;
    }


    /**
     * 调用方法getXxx()或方法isXxx()获取属性值
     *
     * @param obj
     * @param propertyName
     * @return 属性值
     */
    public static Object getPropertyValue(Object obj, String propertyName) {
        Method getter = getter(obj.getClass(), propertyName);
        throwIfNull(getter, new NoSuchMethodException("No such getter Method for property: " + propertyName));
        return invoke(getter, obj);
    }

    /**
     * 调用方法setXxx(propertyValue)设置属性值
     *
     * @param obj
     * @param propertyName
     * @param propertyValue
     */
    public static void setPropertyValue(Object obj, String propertyName, Object propertyValue) {
        Method setter = setter(obj.getClass(), propertyName, propertyValue);
        throwIfNull(setter, new NoSuchMethodException("No such setter Method for property: " + propertyName));
        invoke(setter, obj, propertyValue);
    }


    /**
     * 获取字段值
     *
     * @param obj
     * @param field
     * @return
     */
    public static Object getFieldValue(Object obj, Field field) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw throwException(e);
        }
    }

    /**
     * 获取字段值
     *
     * @param obj
     * @param fieldName
     * @return Object
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        return getFieldValue(obj, getField(obj.getClass(), fieldName));
    }

    /**
     * 设置字段值
     *
     * @param obj
     * @param field
     * @param fieldValue
     */
    public static void setFieldValue(Object obj, Field field, Object fieldValue) {
        try {
            field.setAccessible(true);
            field.set(obj, fieldValue);
        } catch (Exception e) {
            throwException(e);
        }
    }

    /**
     * 设置字段值
     *
     * @param obj
     * @param fieldName
     * @param fieldValue
     */

    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) {
        setFieldValue(obj, getField(obj.getClass(), fieldName), fieldValue);
    }

    /**
     * 将from实例中的属性赋值给to实例中的同名属性<br/>
     * 如果同名属性类型不兼容,则不赋值
     *
     * @param from
     * @param to
     */
    public static void copyProperties(Object to, Object from) {
        Map<String, Method> setters = CollectionUtils.map(setters(to.getClass()), Reflections::propertyName);
        Map<String, Method> getters = CollectionUtils.map(getters(from.getClass()), Reflections::propertyName);
        setters.forEach(
                (n, setter) -> {
                    try {
                        Method getter = getters.get(n);
                        // 调用setter方法赋值
                        if (getter != null && setter.getParameterTypes()[0].isAssignableFrom(getter.getReturnType())) {
                            setter.invoke(to, getter.invoke(from));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }


    /**
     * 根据字符串尽可能地去获取指定类型的实例<br/>
     * 1) 如果是原始类型,返回对应的值<br/>
     * 2) 如果是枚举类型,获取对应名称的实例<br/>
     * 3) 如果是字符串类型,返回字符串<br/>
     * 4) 其他类型,则将字符串作为构造参数以获取实例
     *
     * @param <T>
     * @param clazz
     * @param valueString
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T simpleInstance(Class<T> clazz, String valueString) {
        if (clazz.equals(String.class)) {
            return (T) valueString;
        }
        if (clazz.equals(Boolean.TYPE) || clazz.equals(Boolean.class)) {
            return (T) Boolean.valueOf(valueString);
        }
        if (clazz.equals(Byte.TYPE) || clazz.equals(Byte.class)) {
            return (T) Byte.valueOf(valueString);
        }
        if (clazz.equals(Short.TYPE) || clazz.equals(Short.class)) {
            return (T) Short.valueOf(valueString);
        }
        if (clazz.equals(Integer.TYPE) || clazz.equals(Integer.class)) {
            return (T) Integer.valueOf(valueString);
        }
        if (clazz.equals(Long.TYPE) || clazz.equals(Long.class)) {
            return (T) Long.valueOf(valueString);
        }
        if (clazz.equals(Float.TYPE) || clazz.equals(Float.class)) {
            return (T) Float.valueOf(valueString);
        }
        if (clazz.equals(Double.TYPE) || clazz.equals(Double.class)) {
            return (T) Double.valueOf(valueString);
        }
        if (clazz.equals(Character.TYPE) || clazz.equals(Character.class)) {
            return (T) Character.valueOf(valueString.charAt(0));
        }
        if (clazz.isEnum()) {
            return (T) Enum.valueOf((Class<Enum>) clazz, valueString);
        }
        try {
            return clazz.getConstructor(String.class).newInstance(valueString);
        } catch (Exception e) {
            throw throwException(e);
        }
    }

    /**
     * 创建名称为className的类的实例<br/>
     * 这里,首先使用给定参数去匹配构造方法,如果存在直接进行实例化 <br/>
     * 否则,寻找符合以下条件的构造方法进行实例化:<br/>
     * 1) 给定参数与构造参数数目一致<br/>
     * 2) 给定参数是构造参数类型的实例
     *
     * @param className
     * @param args      构造参数
     * @return 对象实例
     */
    public static Object newInstance(String className, Object... args) {
        return newInstance(getClass(className), args);
    }

    /**
     * 创建clazz类型的实例<br/>
     * 这里,首先使用给定参数去匹配构造方法,如果存在直接进行实例化 <br/>
     * 否则,寻找符合以下条件的构造方法进行实例化:<br/>
     * 1) 给定参数与构造参数数目一致<br/>
     * 2) 给定参数是构造参数类型的实例
     *
     * @param clazz
     * @param args  构造参数
     * @return 对象实例
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz, Object... args) {
        try {
            if (args.length == 0)
                return clazz.newInstance();
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
            try {
                Constructor<T> con = clazz.getConstructor(parameterTypes);
                return con.newInstance(args);
            } catch (NoSuchMethodException e) {
                for (Constructor<?> con : clazz.getConstructors()) {
                    if (instanceOfTypes(con.getParameterTypes(), args)) {
                        return (T) con.newInstance(args);
                    }
                }
                throw new IllegalArgumentException("no matched constructor!");
            }
        } catch (Exception e) {
            throw throwException(e);
        }
    }

    /**
     * 根据类型名称获取数据类型,支持原始类型<br/>
     *
     * @param className
     * @return
     */
    public static Class<?> getClass(String className) {
        try {
            Primitive primitive = Reflections.Primitive.get(className);
            if (primitive != null)
                return primitive.type;
            return Class.forName(className);
        } catch (Exception e) {
            throw throwException(e);
        }
    }

    /**
     * 对于原始类型返回包装类型
     *
     * @param clazz
     * @return
     */
    public static Class wrapClass(Class<?> clazz) {
        Primitive primitive = Reflections.Primitive.get(clazz);
        if (primitive != null)
            return primitive.clazz;
        return clazz;
    }

    /**
     * 判断指定对象obj是不为是clazz类的实例
     *
     * @param clazz
     * @param obj
     * @return
     */
    public static boolean instanceOf(Class<?> clazz, Object obj) {
        return wrapClass(clazz).isInstance(obj);
    }

    /**
     * 判断对象数组中的元素是否分别是类型数组元素的实例
     *
     * @param types
     * @param args
     * @return
     */
    public static boolean instanceOfTypes(Class<?>[] types, Object[] args) {
        if (types.length != args.length)
            return false;
        for (int i = 0; i < types.length; i++) {
            if (!types[i].isInstance(args[i]))
                return false;
        }
        return true;
    }


    /**
     * 判断clazz类型是否为抽象类型
     *
     * @param clazz
     * @return 如果是返回true, 否则返回false
     */
    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }


    /**
     * 根据类型名称判断是否为原始数据类型
     *
     * @param className
     * @return 如果是返回true, 否则返回false
     */
    public static boolean isPrimitive(String className) {
        return Primitive.get(className) != null;
    }


    /**
     * 查找类对象clazz绑定的genericClass声明的泛型参数
     *
     * @param clazz        绑定泛型参数的类
     * @param genericClass 声明泛型的类
     * @param index        泛型在声明类中的索引位置
     * @return 如果绑定了泛型参数, 则返回泛型类型, 否则返回null
     */
    public static Class getGenericType(Class<?> clazz, Class genericClass, int index) {
        return Generics.find(clazz, genericClass, index);
    }

    /**
     * 判断getter方法
     *
     * @param method
     * @return
     */
    public static boolean isGetter(Method method) {
        return !Modifier.isStatic(method.getModifiers())
                && method.getReturnType() != Void.TYPE
                && method.getParameterTypes().length == 0
                && (method.getName().startsWith("get")
                && method.getName().length() > 3
                || method.getName().startsWith("is")
                && method.getReturnType() == Boolean.TYPE
                && method.getName().length() > 2);
    }


    /**
     * 判断setter方法
     */
    public static boolean isSetter(Method method) {
        return !Modifier.isStatic(method.getModifiers())
                && method.getReturnType() == Void.TYPE
                && method.getParameterTypes().length == 1
                && method.getName().startsWith("set")
                && method.getName().length() > 3;
    }


    private static <T> Predicate<T> nullable(Predicate<T> filter) {
        return Optional.ofNullable(filter).orElse((f) -> true);
    }

    public final static Predicate<Method> IS_STATIC_METHOD = method -> Modifier.isStatic(method.getModifiers());
    public final static Predicate<Method> IS_SETTER = Reflections::isSetter;
    public final static Predicate<Method> IS_GETTER = Reflections::isGetter;
    public final static Predicate<Field> IS_FINAL_FIELD = field -> Modifier.isFinal(field.getModifiers());
}
