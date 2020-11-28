package com.cudrania.spring.handlermapping;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
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
  private StringValueResolver nameResolver = strVal -> strVal;


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
    RequestMappingInfo requestMappingInfo = null;
    int mod = method.getModifiers();
    if (Modifier.isPublic(mod) /*必须是public方法*/
            && !Modifier.isStatic(mod) /*必须是成员方法*/
            && AnnotatedElementUtils.findMergedAnnotation(handlerType, NotMapping.class) == null /*类型上未添加@NotMapping注解*/
            && AnnotatedElementUtils.findMergedAnnotation(method, NotMapping.class) == null /*方法上未添加@NotMapping注解*/) {
      RequestMappingInfo typeRequestMappingInfo =
              createRequestMappingInfo(resolveRequestMapping(handlerType),
                      getCustomTypeCondition(handlerType));
      RequestMappingInfo methodRequestMappingInfo =
              createRequestMappingInfo(resolveRequestMapping(method), getCustomMethodCondition(method));
      requestMappingInfo = typeRequestMappingInfo.combine(methodRequestMappingInfo);
    }
    return requestMappingInfo;
  }

  /**
   * 获取类上的RequestMapping配置
   *
   * @param handlerType
   * @return
   */
  protected RequestMappingWrapper resolveRequestMapping(Class handlerType) {
    Package aPackage = handlerType.getPackage();
    String baseName = (aPackage != null ? aPackage.getName() : "").replaceAll(packagePattern, packageReplacement);
    String defaultName = nameResolver.resolveStringValue(handlerType.getSimpleName().replaceAll(classPattern, classReplacement));
    RequestMappingWrapper wrapper = new RequestMappingWrapper(findRequestMapping(handlerType));
    wrapper.value(combineURL(baseName, defaultName, wrapper.value()));
    return wrapper;
  }

  /**
   * 获取方法上的RequestMapping配置
   *
   * @param method
   * @return
   */
  protected RequestMappingWrapper resolveRequestMapping(Method method) {
    RequestMappingWrapper wrapper = new RequestMappingWrapper(findRequestMapping(method));
    String methodName = method.getName();
    String defaultName = nameResolver.resolveStringValue(methodName);
    wrapper.value(combineURL("", defaultName, wrapper.value()));

    if (wrapper.method().length == 0) {
      //默认RequestMethod
      wrapper.method(defaultRequestMethods);
      for (String key : requestMethodMapping.keySet()) {
        if (methodName.matches(key)) {
          //配置RequestMethod
          wrapper.method(requestMethodMapping.get(key));
          break;
        }
      }
    }
    return wrapper;
  }


  /**
   * 获取RequestMapping注解
   *
   * @param element
   * @return
   */
  private RequestMappingWrapper findRequestMapping(AnnotatedElement element) {
    RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
    return new RequestMappingWrapper(requestMapping);
  }

  /**
   * 组合URL, 如果指定urls不为空,使用base+urls组合, 否则使用baseUrl+defaultUrl组合
   *
   * @param baseUrl    基本路径
   * @param defaultUrl 默认路径
   * @param urls       URL组
   * @return
   */
  private String[] combineURL(String baseUrl, String defaultUrl, String[] urls) {
    defaultUrl = "/" + defaultUrl;
    if (urls == null || urls.length == 0) {
      urls = new String[]{defaultUrl};
    }
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
