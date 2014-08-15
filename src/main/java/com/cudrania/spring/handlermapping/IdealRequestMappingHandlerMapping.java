package com.cudrania.spring.handlermapping;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;

/**
 * 扩展基于注解的请求映射处理类,实现自动映射功能和统一配置，具体配置项参考{@link RequestMappingConfiguration}<br/>
 *
 * @author skyfalling
 * @see RequestMappingConfiguration
 */
public class IdealRequestMappingHandlerMapping extends
        RequestMappingHandlerMapping {

    /**
     * 匹配包名的正则表达式
     */
    private String packagePattern = ".*";
    /**
     * 替换匹配包名的表达式
     */
    private String packageReplacement = "";
    /**
     * 匹配类名的正则表达式
     */
    private String classPattern = "(.*)Controller";
    /**
     * 替换匹配类名的表达式
     */
    private String classReplacement = "$1";

    /**
     * 默认RequestMethod列表
     */
    private RequestMethod[] defaultRequestMethods = new RequestMethod[0];
    /**
     * RequestMethod映射配置
     */
    private LinkedHashMap<String, RequestMethod[]> requestMethodMapping = new LinkedHashMap<String, RequestMethod[]>();

    /**
     * 类名及方法名的处理
     */
    private StringValueResolver nameResolver = new StringValueResolver() {
        @Override
        public String resolveStringValue(String strVal) {
            return strVal;
        }
    };


    /**
     * 默认构造方法
     */
    public IdealRequestMappingHandlerMapping() {
    }

    /**
     * 构造方法,指定配置
     *
     * @param requestMappingConfiguration
     */
    public IdealRequestMappingHandlerMapping(RequestMappingConfiguration requestMappingConfiguration) {
        if (requestMappingConfiguration != null) {
            this.packagePattern = requestMappingConfiguration.getPackagePattern();
            this.packageReplacement = requestMappingConfiguration.getPackageReplacement();
            this.classPattern = requestMappingConfiguration.getClassPattern();
            this.classReplacement = requestMappingConfiguration.getClassReplacement();
            this.defaultRequestMethods = requestMappingConfiguration.getDefaultRequestMethods();
            this.requestMethodMapping = requestMappingConfiguration.getRequestMethodMapping();
            this.nameResolver = requestMappingConfiguration.getNameResolver();
        }
    }


    /**
     * Uses method and type-level @{@link org.springframework.web.bind.annotation.RequestMapping} annotations to create
     * the RequestMappingInfo.
     *
     * @return the created RequestMappingInfo, or {@code null} if the method
     *         does not have a {@code @RequestMapping} annotation.
     * @see #getCustomMethodCondition(java.lang.reflect.Method)
     * @see #getCustomTypeCondition(Class)
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        int mod = method.getModifiers();
        if (!Modifier.isPublic(mod) //必须是public方法
                || Modifier.isStatic(mod) //必须是成员方法
                || AnnotationUtils.findAnnotation(handlerType, NotMapping.class) != null //方法上不能添加@NotMapping注解
                || AnnotationUtils.findAnnotation(method, NotMapping.class) != null) //方法上不能添加@NotMapping注解
            return null;
        return createRequestMappingInfo(createRequestMappingConfig(handlerType)).combine(createRequestMappingInfo(createRequestMappingConfig(method)));
    }


    /**
     * 获取方法上的RequestMapping配置
     *
     * @param method
     * @return
     */
    protected RequestMappingAnnotationConfiguration createRequestMappingConfig(Method method) {
        RequestMappingAnnotationConfiguration config = new RequestMappingAnnotationConfiguration();
        RequestMapping annotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        String methodName = method.getName();
        String defaultName = nameResolver.resolveStringValue(methodName);
        config.value(combineURL("", defaultName, annotation != null ? annotation.value() : null));

        if (annotation != null && annotation.method().length > 0) {
            config.method(annotation.method());
        } else {
            //默认RequestMethod
            config.method(defaultRequestMethods);
            for (String key : requestMethodMapping.keySet()) {
                if (methodName.matches(key)) {
                    //配置RequestMethod
                    config.method(requestMethodMapping.get(key));
                    break;
                }
            }
        }
        config.requestCondition(getCustomMethodCondition(method));
        return config;
    }

    /**
     * 获取类上的RequestMapping配置
     *
     * @param handlerType
     * @return
     */
    protected RequestMappingAnnotationConfiguration createRequestMappingConfig(Class handlerType) {
        RequestMappingAnnotationConfiguration config = new RequestMappingAnnotationConfiguration();
        RequestMapping annotation = AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);
        Package aPackage = handlerType.getPackage();
        String baseName = (aPackage != null ? aPackage.getName() : "").replaceAll(packagePattern, packageReplacement);
        String defaultName = nameResolver.resolveStringValue(handlerType.getSimpleName().replaceAll(classPattern, classReplacement));
        config.value(combineURL(baseName, defaultName, annotation != null ? annotation.value() : null));
        config.requestCondition(getCustomTypeCondition(handlerType));
        return config;
    }

    /**
     * 根据RequestMapping配置创建RequestMappingInfo对象
     *
     * @param config
     * @return
     */
    protected RequestMappingInfo createRequestMappingInfo(RequestMappingAnnotationConfiguration config) {
        String[] patterns = resolveEmbeddedValuesInPatterns(config.value());
        return new RequestMappingInfo(
                new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(),
                        this.useSuffixPatternMatch(), this.useTrailingSlashMatch(), this.getFileExtensions()),
                new RequestMethodsRequestCondition(config.method()),
                new ParamsRequestCondition(config.params()),
                new HeadersRequestCondition(config.headers()),
                new ConsumesRequestCondition(config.consumes(), config.headers()),
                new ProducesRequestCondition(config.produces(), config.headers(), getContentNegotiationManager()),
                config.requestCondition());
    }

    /**
     * 组合URL
     *
     * @param baseUrl    基本路径
     * @param defaultUrl 默认路径
     * @param urls       URL组
     * @return
     */
    private String[] combineURL(String baseUrl, String defaultUrl, String[] urls) {
        defaultUrl = "/" + defaultUrl;
        if (urls == null || urls.length == 0)
            urls = new String[]{defaultUrl};
        int i = 0;
        for (String value : urls) {
            //不是绝对路径,自动添加类路径
            if (!value.startsWith("/")) {
                value = defaultUrl + "/" + value;
            }
            urls[i++] = (baseUrl + "/" + value).replace('.', '/').replaceAll("/+", "/");
        }
        return urls;
    }


}
