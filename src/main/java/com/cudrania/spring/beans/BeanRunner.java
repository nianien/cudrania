package com.cudrania.spring.beans;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link SpringBean}对象的调度类<br/> for example:
 * <pre>
 * package com.my.bean
 * &#064;ImportResource("classpath:spring-root.xml")
 * public class MyBean extends {@link SpringBean} {
 *
 *    &#064;AutoWired
 *    private Service service;
 *
 *    public void doSomething(){
 *       service.doSomething();
 *    }
 *
 *    public void doOtherThing(int a,String b,...){
 *       service.doSomething();
 *    }
 *  }
 * </pre>
 * 调用命令如下:
 * <ol>
 *     <li>java com.cudrania.spring.beans.BeanRunner com.my.bean.MyBean#doSomething</li>
 *     <li>java com.cudrania.spring.beans.BeanRunner com.my.bean.MyBean#doOtherThing 1 test</li>
 * </ol>
 *
 * @author skyfalling
 * @date 16/11/15
 * @see SpringBean
 */
public class BeanRunner {

    /**
     * bean对象方法执行入口
     * @param args beanClass#methodName [args...]
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String beanClass = StringUtils.substringBeforeLast(args[0], "#");
        String method = StringUtils.substringAfterLast(args[0], "#");
        String[] params;
        if (StringUtils.isEmpty(method)) {
            method = args[1];
            params = Arrays.copyOfRange(args, 2, args.length);
        } else {
            params = Arrays.copyOfRange(args, 1, args.length);
        }

        SpringBean bean = (SpringBean) Class.forName(beanClass).newInstance();
        run(bean.init(), method, params);
    }


    /**
     * 根据方法名和参数调用指定方法
     * @param bean
     * @param methodName
     * @param parameters
     * @return
     * @throws Exception
     */
    public static Object run(Object bean, String methodName, Object[] parameters) throws Exception {
        Class beanClass = bean.getClass();
        try {
            Class<?>[] types = new Class<?>[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                types[i] = parameters[i].getClass();
            }
            return beanClass.getMethod(methodName, types).invoke(bean, parameters);
        } catch (NoSuchMethodException e) {
            Method method = find(beanClass, methodName, parameters);
            Class[] parameterTypes = method.getParameterTypes();
            Object[] castParams = new Object[parameters.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i].isInstance(parameters[i])) {
                    castParams[i] = parameters[i];
                } else {
                    castParams[i] = simpleInstance(parameterTypes[i], parameters[i].toString());
                }
            }
            return method.invoke(bean, castParams);
        }
    }

    /**
     * 调用指定方法
     * @param bean
     * @param methodName
     * @param parameters
     * @param parameterTypes
     * @return
     * @throws Exception
     */
    public static Object run(Object bean, String methodName, Object[] parameters,Class[] parameterTypes) throws Exception {
        if (parameterTypes.length != parameters.length) {
            throw new IllegalArgumentException("parameters and parameterTypes must have same size!");
        }
        Class beanClass = bean.getClass();
        Method method = ClassUtils.getMethodIfAvailable(beanClass, methodName, parameterTypes);
        // 构造参数转型
        Object[] castParams = new Object[parameters.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].isInstance(parameters[i])) {
                castParams[i] = parameters[i];
            } else {
                castParams[i] = simpleInstance(parameterTypes[i], parameters[i].toString());
            }
        }
        return method.invoke(bean, castParams);
    }


    /**
     * 根据方法名和参数查找方法对象
     * @param beanClass
     * @param methodName
     * @param args
     * @return
     * @throws Exception
     */
    public static Method find(Class beanClass, String methodName, Object[] args) throws Exception {
        List<Method> list = new ArrayList();
        for (Method method : beanClass.getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterTypes().length == args.length) {
                list.add(method);
            }
        }
        if (list.size() != 1) {
            throw new IllegalArgumentException("no suitable method [" + methodName + "] for class [" + beanClass + "] with parameters: "
                    + Arrays.toString(args));
        }
        return list.get(0);

    }

    /**
     * 构建简单对象实例
     * @param clazz
     * @param valueString
     * @param <T>
     * @return
     */
    private static <T> T simpleInstance(Class<T> clazz, String valueString) {
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
            throw new RuntimeException(e);
        }
    }
}
