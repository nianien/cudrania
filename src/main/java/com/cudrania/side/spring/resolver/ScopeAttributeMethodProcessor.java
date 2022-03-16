package com.cudrania.side.spring.resolver;

import com.cudrania.side.spring.resolver.ScopeAttribute.Scope;
import org.springframework.core.Conventions;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.cudrania.core.reflection.Reflections.wrapClass;

/**
 * 从Session或Request或ThreadLocal属性中获取参数,并将方法返回值绑定到Session或Request属性中
 *
 * @author skyfalling
 */
public class ScopeAttributeMethodProcessor implements HandlerMethodArgumentResolver, HandlerMethodReturnValueHandler {

    private static ThreadLocal<Map<String, Object>> localMap = ThreadLocal.withInitial(() -> new HashMap<>());


    /**
     * 绑定对象到当前线程<br/>
     * 键值为:type#name
     *
     * @param name  对象名称
     * @param type  对象类型
     * @param value
     * @return
     */
    public static <T> void bind(String name, Class<T> type, T value) {
        bind(nameWithType(name, type), value);
    }

    /**
     * 绑定对象到当前线程
     * 键值为: name
     *
     * @param name  对象名称,作为键值
     * @param value
     * @return
     */
    protected static void bind(String name, Object value) {
        localMap.get().put(name, value);
    }

    /**
     * 清空当前线程绑定的数据
     */
    public static void clear() {
        localMap.get().clear();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ScopeAttribute.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {
        ScopeAttribute scopeAttribute = parameter.getParameterAnnotation(ScopeAttribute.class);
        if (scopeAttribute == null)
            return null;
        String name = getNameForParameter(parameter);
        Object attribute = null;
        for (Scope scope : scopeAttribute.scope()) {
            switch (scope) {
                case REQUEST:
                    attribute = request.getAttribute(name, NativeWebRequest.SCOPE_REQUEST);
                    break;
                case SESSION:
                    attribute = request.getAttribute(name, NativeWebRequest.SCOPE_SESSION);
                    break;
                case THREAD:
                    attribute = localMap.get().get(name);
                    break;
            }
            if (wrapClass(parameter.getParameterType()).isInstance(attribute))
                break;
        }
        WebDataBinder binder = binderFactory.createBinder(request, attribute, name);
        if (binder.getTarget() != null) {
            validateIfApplicable(binder, parameter);
            //如果没有Error参数接受校验错误的话,则抛出异常
            if (binder.getBindingResult().hasErrors()) {
                if (isBindExceptionRequired(binder, parameter)) {
                    throw new BindException(binder.getBindingResult());
                }
            }
        }
        return binder.getTarget();
    }


    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasParameterAnnotation(ScopeAttribute.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest request) {
        if (returnValue != null) {
            ScopeAttribute scopeAttribute = returnType.getMethodAnnotation(ScopeAttribute.class);
            if (scopeAttribute == null)
                return;
            String name = getNameForReturnValue(returnValue, returnType);
            Object attribute = null;
            if (attribute != null && scopeAttribute.scope().length > 0) {
                Scope scope = scopeAttribute.scope()[0];
                switch (scope) {
                    case REQUEST:
                        request.setAttribute(name, attribute, NativeWebRequest.SCOPE_REQUEST);
                        break;
                    case SESSION:
                        request.setAttribute(name, attribute, NativeWebRequest.SCOPE_SESSION);
                        break;
                    case THREAD:
                        localMap.get().put(name, attribute);
                        break;
                }
            }

        }
    }


    /**
     * Validate the model attribute if applicable.
     * <p>The default implementation checks for {@code @javax.validation.Valid}.
     *
     * @param binder    the DataBinder to be used
     * @param parameter the method parameter
     */
    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().startsWith("Valid")) {
                Object hints = AnnotationUtils.getValue(annotation);
                binder.validate(hints instanceof Object[] ? (Object[]) hints : new Object[]{hints});
                break;
            }
        }
    }

    /**
     * Whether to raise a {@link org.springframework.validation.BindException} on validation errors.
     *
     * @param binder    the data binder used to perform data binding
     * @param parameter the method argument
     * @return {@code true} if the next method argument is not of type {@link org.springframework.validation.Errors}.
     */
    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getMethod().getParameterTypes();
        boolean hasBindingResult = (paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1]));

        return !hasBindingResult;
    }


    /**
     * Derives the model attribute name for a method parameter based on:
     * <ol>
     * <li>The parameter {@code @ModelAttribute} annotation value
     * <li>The parameter type
     * </ol>
     *
     * @return the derived name; never {@code null} or an empty string
     */
    protected String getNameForParameter(MethodParameter parameter) {
        ScopeAttribute scopeAttribute = parameter.getParameterAnnotation(ScopeAttribute.class);
        String attrName = (scopeAttribute != null) ? scopeAttribute.value() : null;
        return StringUtils.hasText(attrName) ? attrName : nameWithType(parameter.getParameterName(), parameter.getParameterType());
    }


    /**
     * Derive the model attribute name for the given return value using
     * one of the following:
     * <ol>
     * <li>The method {@code ModelAttribute} annotation value
     * <li>The declared return type if it is other than {@code Object}
     * <li>The actual return value type
     * </ol>
     *
     * @param returnValue the value returned from a method invocation
     * @param returnType  the return type of the method
     * @return the model name, never {@code null} nor empty
     */
    protected String getNameForReturnValue(Object returnValue, MethodParameter returnType) {
        ScopeAttribute scopeAttribute = returnType.getMethodAnnotation(ScopeAttribute.class);
        if (scopeAttribute != null && StringUtils.hasText(scopeAttribute.value())) {
            return scopeAttribute.value();
        }
        Method method = returnType.getMethod();
        Class<?> resolvedType = GenericTypeResolver.resolveReturnType(method, returnType.getDeclaringClass());
        return Conventions.getVariableNameForReturnType(method, resolvedType, returnValue);
    }


    /**
     * 返回形式: type#name
     *
     * @param name
     * @param type
     * @return
     */
    private static String nameWithType(String name, Class type) {
        return wrapClass(type).getName() + "#" + name;
    }



}
