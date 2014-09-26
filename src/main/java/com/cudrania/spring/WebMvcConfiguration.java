package com.cudrania.spring;

import com.cudrania.spring.handlermapping.IdealRequestMappingHandlerMapping;
import com.cudrania.spring.handlermapping.RequestMappingConfiguration;
import com.cudrania.spring.resolver.GlobalHandlerExceptionResolver;
import com.cudrania.spring.resolver.ScopeAttributeMethodProcessor;
import com.cudrania.validation.ResourceBundleMessageInterpolator;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * REST风格配置对象
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {


    private ApplicationContext applicationContext;
    @Resource
    private MessageSource messageSource;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        this.applicationContext = applicationContext;
    }

    @Bean
    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingConfiguration requestMappingConfiguration = getNullableBean(RequestMappingConfiguration.class);
        IdealRequestMappingHandlerMapping handlerMapping = new IdealRequestMappingHandlerMapping(requestMappingConfiguration);
        handlerMapping.setOrder(0);
        handlerMapping.setInterceptors(getInterceptors());
        handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager());
        return handlerMapping;
    }


    @Override
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter();
        Method method = ReflectionUtils.findMethod(RequestMappingHandlerAdapter.class, "getDefaultArgumentResolvers");
        ReflectionUtils.makeAccessible(method);
        List<HandlerMethodArgumentResolver> argumentResolvers = (List<HandlerMethodArgumentResolver>) ReflectionUtils.invokeMethod(method, adapter);
        //优先调用自定义的ArgumentResolver
        argumentResolvers.add(0, new ScopeAttributeMethodProcessor());
        adapter.setArgumentResolvers(argumentResolvers);

        method = ReflectionUtils.findMethod(RequestMappingHandlerAdapter.class, "getDefaultReturnValueHandlers");
        ReflectionUtils.makeAccessible(method);
        List<HandlerMethodReturnValueHandler> returnValueHandlers = (List<HandlerMethodReturnValueHandler>) ReflectionUtils.invokeMethod(method, adapter);
        //优先调用自定义的ReturnValueHandler
        returnValueHandlers.add(0, new ScopeAttributeMethodProcessor());
        adapter.setReturnValueHandlers(returnValueHandlers);
        return adapter;
    }


    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        addDefaultHttpMessageConverters(converters);
        Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
        while (iterator.hasNext()) {
            HttpMessageConverter<?> it = iterator.next();
            if (it instanceof StringHttpMessageConverter) {
                //移除默认编码ISO-8859-1
                iterator.remove();
            } else if (it instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) it;
                //JSON序列化忽略空值
                converter.getObjectMapper()
                        .configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false).setSerializationInclusion(Include.NON_NULL)
                        // 允许字段名不用引号
                        .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                                // 允许使用单引号
                        .configure(Feature.ALLOW_SINGLE_QUOTES, true)
                                // 允许数字含有前导0
                        .configure(Feature.ALLOW_NUMERIC_LEADING_ZEROS, true)
                        .configure(Feature.STRICT_DUPLICATE_DETECTION, true)
                                // 允许未知的属性
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                //允许空对象
                        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            }
        }
        //添加编码UTF-8
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }

    @Override
    protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        HandlerExceptionResolver resolver = getNullableBean(HandlerExceptionResolver.class);
        //如果未定义,则加载默认异常处理
        if (resolver == null) {
            exceptionResolvers.add(new GlobalHandlerExceptionResolver());
        }
    }


    @Bean
    @Override
    public Validator mvcValidator() {
        Validator validator = super.mvcValidator();
        if (validator instanceof LocalValidatorFactoryBean) {
            ((LocalValidatorFactoryBean) validator).setMessageInterpolator(
                    new ResourceBundleMessageInterpolator(
                            new MessageSourceResourceBundleLocator(messageSource)
                    )
            );
        }
        return validator;
    }


    @Bean
    @Override
    public FormattingConversionService mvcConversionService() {
        FormattingConversionService conversionService = super.mvcConversionService();
        //清除字符首尾空白字符
        conversionService.addConverter(new Converter<String, String>() {
            @Override
            public String convert(String source) {
                return StringUtils.trim(source);
            }
        });
        return conversionService;
    }

    @Bean
    public static ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/messages", "i18n/errors");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }


    private <T> T getNullableBean(Class<T> type) {
        try {
            return applicationContext.getBean(type);
        } catch (BeansException e) {
            return null;
        }
    }
}
