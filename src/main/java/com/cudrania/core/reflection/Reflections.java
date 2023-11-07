package com.cudrania.core.reflection;

import com.cudrania.core.collection.CollectionUtils;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.cudrania.core.utils.StringUtils.decapitalize;

/**
 * 反射工具类
 *
 * @author skyfalling
 */
public class Reflections {

    private final static Map<Class, List<Method>> methodCache = new WeakHashMap<>();
    private final static Map<Class, List<Field>> fieldCache = new WeakHashMap<>();
    public final static Predicate<Method> IS_SETTER = Reflections::isSetter;
    public final static Predicate<Method> IS_GETTER = Reflections::isGetter;


    /**
     * 获取指定类型的注解
     *
     * @param annotatedElement
     * @param annotationClass
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T getAnnotation(AnnotatedElement annotatedElement, Class<T> annotationClass) {
        return annotatedElement.getAnnotation(annotationClass);
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
     * 查找指定名称和参数类型的方法<p>
     * 如果查找不到相应的方法,则返回null
     *
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        List<Method> methods = getMethods(clazz, m -> m.getName().equals(methodName));
        List<Method> candidates = new ArrayList<>();
        for (Method method : methods) {
            int matched = typeMatched(method.getParameterTypes(), parameterTypes);
            if (matched == 0) {
                return method;
            }
            candidates.add(method);
        }
        if (candidates.size() > 1) {
            throw new IllegalArgumentException("ambiguous methods:" + candidates);
        }

        if (candidates.size() == 0) {
            throw new IllegalArgumentException(
                    "no suitable method [" + methodName + "] for class [" + clazz + "] with parameters: "
                            + Arrays.toString(parameterTypes));
        }
        return candidates.get(0);
    }


    /**
     * 获取clazz及其父类声明的符合条件的方法
     *
     * @param clazz
     * @param filter
     * @return
     */
    public static List<Method> getMethods(Class<?> clazz, Predicate<Method> filter) {
        return getMethods(clazz).stream().filter(nullable(filter)).collect(Collectors.toList());
    }

    /**
     * 获取clazz及其父类声明的符合条件的方法
     *
     * @param clazz
     * @return
     */
    public static List<Method> getMethods(Class<?> clazz) {
        return methodCache.computeIfAbsent(clazz, key -> {
            Set<Method> methods = new LinkedHashSet<>();
            Arrays.stream(clazz.getMethods()).forEach(methods::add);
            Arrays.stream(clazz.getDeclaredMethods()).forEach(methods::add);
            return methods.stream().collect(Collectors.toList());
        });
    }

    /**
     * 获取getter方法
     *
     * @param clazz
     * @return getter方法
     */
    public static Method getter(Class<?> clazz, String propertyName) {
        return getters(clazz).stream().filter(m -> propertyName(m).equals(propertyName)).findAny().orElse(null);
    }

    /**
     * 获取getter方法列表,不包含{@link Object}声明的方法
     *
     * @param clazz
     * @return getter方法列表
     */
    public static List<Method> getters(Class<?> clazz) {
        return getMethods(clazz, IS_GETTER);
    }

    /**
     * 获取setter方法
     *
     * @param clazz
     * @return getter方法
     */
    public static Method setter(Class<?> clazz, String propertyName) {
        return setters(clazz).stream().filter(m -> propertyName(m).equals(propertyName)).findAny().orElse(null);
    }

    /**
     * 获取setter方法列表,不包含{@link Object}声明的方法
     *
     * @param clazz
     * @return getter方法列表
     */
    public static List<Method> setters(Class<?> clazz) {
        return getMethods(clazz, IS_SETTER);
    }


    /**
     * 获取clazz的BeanProperty列表
     *
     * @param clazz
     * @return
     */
    public static List<BeanProperty> beanProperties(Class<?> clazz) {
        Map<String, Method> getters = CollectionUtils.map(getters(clazz), m -> propertyName(m));
        Map<String, Method> setters = CollectionUtils.map(setters(clazz), m -> propertyName(m));
        Map<String, Field> fields = CollectionUtils.map(getFields(clazz), Field::getName);
        Map<String, BeanProperty> propertyMap = new HashMap<>();
        getters.forEach((name, value) -> propertyMap.computeIfAbsent(name, key -> new BeanProperty(key, value, setters.get(key), fields.get(key))));
        fields.forEach((name, value) -> propertyMap.computeIfAbsent(name, key -> new BeanProperty(key, getters.get(key), setters.get(key), value)));
        return new ArrayList<>(propertyMap.values());
    }


    /**
     * 获取clazz的BeanProperty列表
     *
     * @param clazz
     * @return
     */
    public static BeanProperty beanProperty(Class<?> clazz, String propertyName) {
        return beanProperties(clazz).stream().filter(b -> b.getAlias().equals(propertyName)).findAny().orElse(null);
    }

    /**
     * 查找指定名称的字段<p>
     * 如果查找不到相应的字段,则返回null
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        return getFields(clazz, f -> f.getName().equals(fieldName)).stream().findAny().orElse(null);

    }

    /**
     * 获取clazz及其父类声明字段
     *
     * @param clazz
     * @param filter
     * @return
     */
    public static List<Field> getFields(Class<?> clazz, Predicate<Field> filter) {
        return getFields(clazz).stream().filter(nullable(filter)).collect(Collectors.toList());
    }

    /**
     * 获取clazz及其父类声明的非静态字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getFields(Class<?> clazz) {
        return getFields0(clazz).stream().filter(f -> !Modifier.isStatic(f.getModifiers())).collect(Collectors.toList());
    }

    /**
     * 获取clazz及其父类声明字段
     *
     * @param clazz
     * @return
     */
    private static List<Field> getFields0(Class<?> clazz) {
        return fieldCache.computeIfAbsent(clazz, key -> {
            List<Field> list = new ArrayList<>();
            for (; key != Object.class; key = key.getSuperclass()) {
                Arrays.stream(clazz.getDeclaredFields()).forEach(list::add);
            }
            return list;
        });
    }

    /**
     * 获取字段值
     *
     * @param field
     * @param obj
     * @return
     */
    @SneakyThrows
    public static Object getFieldValue(Field field, Object obj) {
        field.setAccessible(true);
        return field.get(obj);
    }

    /**
     * 获取字段值
     *
     * @param fieldName
     * @param obj
     * @return Object
     */
    public static Object getFieldValue(String fieldName, Object obj) {
        return getFieldValue(getField(obj.getClass(), fieldName), obj);
    }

    /**
     * 设置字段值
     *
     * @param field
     * @param obj
     * @param fieldValue
     */
    @SneakyThrows
    public static void setFieldValue(Field field, Object obj, Object fieldValue) {
        field.setAccessible(true);
        field.set(obj, fieldValue);
    }

    /**
     * 设置字段值
     *
     * @param fieldName
     * @param obj
     * @param fieldValue
     */

    public static void setFieldValue(String fieldName, Object obj, Object fieldValue) {
        setFieldValue(getField(obj.getClass(), fieldName), obj, fieldValue);
    }


    /**
     * 获取getter或setter方法对应的属性名称
     *
     * @param method
     * @return
     */
    public static String propertyName(Method method) {
        String name = method.getName();
        return decapitalize(name.substring(name.startsWith("is") ? 2 : 3));
    }


    /**
     * 将from实例中的属性赋值给to实例中的同名属性<p>
     * 如果同名属性类型不兼容,则不赋值
     *
     * @param from
     * @param to
     */
    public static void populate(Object to, Object from) {
        Map<String, BeanProperty> setters = CollectionUtils.map(beanProperties(to.getClass()), BeanProperty::getAlias);
        Map<String, ?> getters = from instanceof Map ? (Map<String, Object>) from : CollectionUtils.map(beanProperties(from.getClass()), BeanProperty::getAlias);
        setters.forEach(
                (n, setter) -> {
                    try {
                        // 调用setter方法赋值
                        Object value = getters.get(n);
                        if (value instanceof BeanProperty) {
                            BeanProperty property = (BeanProperty) value;
                            Method getter = ((BeanProperty) value).getGetter();
                            if (getter != null && setter.getSetter().getParameterTypes()[0].isAssignableFrom(getter.getReturnType())) {
                                value = property.getValue(from);
                            } else {
                                value = null;
                            }
                        }
                        if (value != null) {
                            setter.setValue(to, value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    /**
     * 执行指定方法,返回执行结果
     *
     * @param method     待执行方法
     * @param owner      声明该方法的实例对象
     * @param parameters 方法的参数
     * @return 方法的执行结果
     */
    @SneakyThrows
    public static Object invoke(Method method, Object owner, Object... parameters) {
        return invoke(method, owner, null, parameters);
    }

    /**
     * 执行指定方法,返回执行结果
     *
     * @param method        待执行方法
     * @param owner         声明该方法的实例对象
     * @param typeConverter 参数类型转换
     * @param parameters    方法的参数
     * @return 方法的执行结果
     */
    @SneakyThrows
    public static Object invoke(Method method, Object owner, BiFunction<Object, Class, Object> typeConverter, Object... parameters) {
        method.setAccessible(true);
        Object[] castParams = parameters;
        if (typeConverter != null) {
            castParams = convert(parameters, method.getParameterTypes(), typeConverter);
        }
        return method.invoke(owner, castParams);
    }


    /**
     * 根据方法名和参数调用指定方法<p>
     *
     * @param methodName 方法名称
     * @param owner      方法关联的对象
     * @param parameters 实际参数值
     * @return
     */
    public static Object invoke(String methodName, Object owner, Object[] parameters) {
        Class beanClass = owner.getClass();
        List<Method> methods = getMethods(beanClass, m -> m.getName().equals(methodName) && argsMatched(m.getParameterTypes(), parameters) >= 0);
        for (Method method : methods) {
            if (argsMatched(method.getParameterTypes(), parameters) == 0) {
                return invoke(method, owner, parameters);
            }
        }
        if (methods.size() > 1) {
            throw new IllegalArgumentException("ambiguous methods:" + methods);
        }
        if (methods.size() == 0) {
            throw new IllegalArgumentException(
                    "no suitable method [" + methodName + "] for class [" + beanClass + "] with parameters: "
                            + Arrays.toString(parameters));
        }
        Method method = methods.get(0);
        return invoke(method, owner, parameters);
    }


    /**
     * 参数类型转换
     *
     * @param parameters     原始参数类型
     * @param parameterTypes 目标参数类型
     * @param typeConverter  类型转化器
     * @return
     */
    public static Object[] convert(Object[] parameters, Class[] parameterTypes, BiFunction<Object, Class, Object> typeConverter) {
        Object[] castParams = new Object[parameters.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].isInstance(parameters[i])) {
                castParams[i] = parameters[i];
            } else {
                castParams[i] = convert(parameters[i], parameterTypes[i], typeConverter);
            }
        }
        return castParams;
    }


    /**
     * 根据字符串尽可能地去获取指定类型的实例<p>
     * 1) 如果是原始类型,返回对应的值<p>
     * 2) 如果是枚举类型,获取对应名称的实例<p>
     * 3) 如果是字符串类型,返回字符串<p>
     * 4) 否则进行类型转换
     *
     * @param <T>
     * @param clazz
     * @param value
     * @return
     */
    @SneakyThrows
    private static <T> T convert(Object value, Class<T> clazz, BiFunction<Object, Class, T> typeConverter) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            String valueString = (String) value;
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
        }
        if (typeConverter == null) {
            return clazz.getConstructor(value.getClass()).newInstance(value);
        }
        return typeConverter.apply(value, clazz);
    }

    /**
     * 创建名称为className的类的实例<p>
     * 这里,首先使用给定参数去匹配构造方法,如果存在直接进行实例化 <p>
     * 否则,寻找符合以下条件的构造方法进行实例化:<p>
     * 1) 给定参数与构造参数数目一致<p>
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
     * 创建clazz类型的实例<p>
     * 这里,首先使用给定参数去匹配构造方法,如果存在直接进行实例化 <p>
     * 否则,寻找符合以下条件的构造方法进行实例化:<p>
     * 1) 给定参数与构造参数数目一致<p>
     * 2) 给定参数是构造参数类型的实例
     *
     * @param clazz
     * @param args  构造参数
     * @return 对象实例
     */
    @SneakyThrows
    public static <T> T newInstance(Class<T> clazz, Object... args) {
        if (args.length == 0)
            return clazz.getConstructor().newInstance();
        Class<?>[] parameterTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        try {
            Constructor<T> con = clazz.getConstructor(parameterTypes);
            return con.newInstance(args);
        } catch (NoSuchMethodException e) {
            for (Constructor<?> con : clazz.getConstructors()) {
                if (argsMatched(con.getParameterTypes(), args) >= 0) {
                    return (T) con.newInstance(args);
                }
            }
            throw new IllegalArgumentException("no matched constructor!");
        }
    }

    /**
     * 根据类型名称获取数据类型,支持原始类型<p>
     *
     * @param className
     * @return
     */
    @SneakyThrows
    public static Class<?> getClass(String className) {
        Primitive primitive = Primitive.get(className);
        if (primitive != null)
            return primitive.type;
        return Class.forName(className);
    }

    /**
     * 对于原始类型返回包装类型
     *
     * @param clazz
     * @return
     */
    public static Class wrapClass(Class<?> clazz) {
        Primitive primitive = Primitive.get(clazz);
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
     * 判断参数类型是否匹配期望类型列表<p>
     * 返回结果等于0表示精确匹配,大于0表示兼容匹配,小于0表示不匹配
     *
     * @param expectedTypes 期望类型列表
     * @param argTypes      实际参数类型
     * @return
     */
    private static int typeMatched(Class<?>[] expectedTypes, Class<?>[] argTypes) {
        if (argTypes.length != argTypes.length) {
            return -1;
        }
        int res = 0;
        for (int i = 0; i < argTypes.length; i++) {
            if (!expectedTypes[i].isAssignableFrom(argTypes[i])) {
                return -1;
            }
            if (wrapClass(expectedTypes[i]) != wrapClass(argTypes[i])) {
                res++;
            }
        }
        return res;
    }

    /**
     * 判断类型列表是否匹配参数类型<p>
     * 返回结果等于0表示精确匹配,大于0表示兼容匹配,小于0表示不匹配
     *
     * @param expectedTypes 期望类型
     * @param args          参数列表
     * @return
     */
    private static int argsMatched(Class<?>[] expectedTypes, Object[] args) {
        if (args.length != args.length) {
            return -1;
        }
        int res = 0;
        for (int i = 0; i < args.length; i++) {
            if (!expectedTypes[i].isInstance(args[i])) {
                return -1;
            }
            if (wrapClass(expectedTypes[i]) != wrapClass(args[i].getClass())) {
                res++;
            }
        }
        return res;
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
     */
    public static boolean isGetter(Method method) {
        return method.getDeclaringClass() != Object.class
                && !Modifier.isStatic(method.getModifiers())
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
        return method.getDeclaringClass() != Object.class
                && !Modifier.isStatic(method.getModifiers())
                && method.getReturnType() == Void.TYPE
                && method.getParameterTypes().length == 1
                && method.getName().startsWith("set")
                && method.getName().length() > 3;
    }

    private static <T> Predicate<T> nullable(Predicate<T> filter) {
        return Optional.ofNullable(filter).orElse((f) -> true);
    }


}
