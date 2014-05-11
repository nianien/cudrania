package com.kavas.misc.spring.resolver;

import com.kavas.misc.exception.DefinedException;
import com.kavas.misc.spring.resolver.ErrorResponse.ErrorField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.ErrorCoded;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 全局异常统一处理类,将错误信息转换成合适的JSON格式返回。
 *
 * @author skyfalling
 */
public class GlobalHandlerExceptionResolver extends AbstractHandlerExceptionResolver {


    /**
     * 处理异常，返回null则不进行处理
     */
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ex.printStackTrace();
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        WebApplicationContext context = RequestContextUtils.getWebApplicationContext(request);

        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setExtractValueFromSingleKeyModel(true);
        view.addStaticAttribute("view", bindError(response, context, ex, locale));
        return new ModelAndView(view);
    }


    /**
     * 绑定错误信息
     *
     * @param context
     * @param e
     * @param locale
     * @return
     */
    protected ErrorResponse bindError(HttpServletResponse httpResponse, ApplicationContext context, Exception e, Locale locale) {
        ErrorResponse errorResponse = new ErrorResponse();
        Errors errors = getErrors(e);
        errorResponse.setFields(renderErrors(context, errors, locale));
        boolean defined = AnnotationUtils.findAnnotation(e.getClass(), DefinedException.class) != null;
        if (e instanceof ErrorCoded) {
            errorResponse.setErrorCode(((ErrorCoded) e).getErrorCode());
        }
        if (e instanceof MessageSourceResolvable) {
            errorResponse.setMessage(context.getMessage((MessageSourceResolvable) e, locale));
        } else if (defined) {
            errorResponse.setMessage(context.getMessage(e.getMessage(), new Object[0], locale));
        }
        HttpStatus status =
                (e instanceof MessageSourceResolvable
                        || e instanceof DefinedException
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
}
