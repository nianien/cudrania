package com.cudrania.spring.resolver;

import com.cudrania.common.exception.DefinedException;
import com.cudrania.spring.resolver.ErrorResponse.ErrorField;
import com.nianien.core.util.StringUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常统一处理类,将错误信息转换成合适的JSON格式返回。
 *
 * @author skyfalling
 */
public class GlobalHandlerExceptionResolver extends AbstractHandlerExceptionResolver {

    {
        //设置优先级
        this.setOrder(0);
    }

    /**
     * 忽略的异常
     */
    private Set<Class<? extends Exception>> ignoreExceptions = Collections.EMPTY_SET;

    /**
     * 预定义异常
     */
    private Set<Class<? extends Exception>> definedExceptions = Collections.EMPTY_SET;


    /**
     * 设置忽略的异常,由其他异常处理器处理
     *
     * @param ignoreExceptions
     */
    public void setIgnoreExceptions(Set<Class<? extends Exception>> ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    /**
     * 设置预定义的异常,将包装异常消息
     *
     * @param definedExceptions
     */
    public void setDefinedExceptions(Set<Class<? extends Exception>> definedExceptions) {
        this.definedExceptions = definedExceptions;
    }

    /**
     * 处理异常，返回null则不进行处理
     */
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //忽略的异常,被其他程序处理
        if (isIgnoreException(ex))
            return null;
        ex.printStackTrace();
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        WebApplicationContext context = RequestContextUtils.findWebApplicationContext(request);

        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setExtractValueFromSingleKeyModel(true);
        view.addStaticAttribute("view", bindError(response, context, ex, locale));
        return new ModelAndView(view);
    }


    /**
     * 绑定错误信息
     *
     * @param context
     * @param ex
     * @param locale
     * @return
     */
    protected ErrorResponse bindError(HttpServletResponse httpResponse, ApplicationContext context, Exception ex, Locale locale) {
        ErrorResponse errorResponse = new ErrorResponse();
        Errors errors = getErrors(ex);
        errorResponse.setFields(renderErrors(context, errors, locale));

        if (ex instanceof MessageSourceResolvable) {
            errorResponse.setMessage(context.getMessage((MessageSourceResolvable) ex, locale));
        } else if (isDefinedException(ex)) {
            try {
                errorResponse.setMessage(context.getMessage(ex.getMessage(), new Object[0], ex.getMessage(), locale));
            } catch (Exception e) {
                errorResponse.setMessage(ex.getMessage());
            }
        } else {
            errorResponse.setMessage("Internal Unknown Error");
        }
        HttpStatus status =
                (ex instanceof MessageSourceResolvable
                        || ex instanceof DefinedException
                        || errors != null)
                        ? HttpStatus.CONFLICT :
                        HttpStatus.INTERNAL_SERVER_ERROR;
        httpResponse.setStatus(status.value());
        errorResponse.setFields(renderErrors(context, errors, locale));
        return errorResponse;
    }


    /**
     * 渲染错误消息
     *
     * @param context
     * @param locale
     * @return
     */
    protected List<ErrorField> renderErrors(ApplicationContext context, Errors errors, Locale locale) {
        List<ErrorField> list = new ArrayList<ErrorField>();
        if (errors != null) {
            for (ObjectError error : errors.getAllErrors()) {
                String fieldName = error.getObjectName();
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    if (StringUtils.isNotEmpty(fieldError.getField())) {
                        fieldName = ((FieldError) error).getField();
                    }
                }
                list.add(new ErrorField(fieldName, context.getMessage(error, locale)));
            }
        }
        return list;
    }

    /**
     * 从异常中获取错误消息
     *
     * @param e
     * @return
     */
    private Errors getErrors(Exception e) {
        Method method = ReflectionUtils.findMethod(e.getClass(), "getBindingResult");
        if (method != null) {
            ReflectionUtils.makeAccessible(method);
            return (Errors) ReflectionUtils.invokeMethod(method, e);
        }
        return null;
    }

    /**
     * 是否为忽略的异常
     *
     * @param ex
     * @return
     */
    protected boolean isIgnoreException(Exception ex) {
        return contains(ignoreExceptions, ex);
    }

    /**
     * 是否为预定义异常
     *
     * @param ex
     * @return
     */
    protected boolean isDefinedException(Exception ex) {
        return AnnotationUtils.findAnnotation(ex.getClass(), DefinedException.class) != null || contains(definedExceptions, ex);
    }

    /**
     * 是否包含异常或子类
     *
     * @param set
     * @param ex
     * @return
     */
    private boolean contains(Set<Class<? extends Exception>> set, Exception ex) {
        for (Class<? extends Exception> clazz : set) {
            if (clazz.isInstance(ex))
                return true;
        }
        return false;
    }
}
