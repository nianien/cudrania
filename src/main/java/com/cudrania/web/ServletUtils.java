package com.cudrania.web;

import com.nianien.core.exception.ExceptionHandler;
import com.nianien.core.reflect.Reflections;
import com.nianien.core.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet工具类
 *
 * @author skyfalling
 */
public class ServletUtils {
    /**
     * 依次从request,session,application中查找属性
     *
     * @param name
     * @param request
     * @return
     */
    public static Object find(String name, HttpServletRequest request) {
        Object value = request.getAttribute(name);
        if (value == null) {
            value = request.getSession().getAttribute(name);
        }
        if (value == null) {
            value = request.getSession().getServletContext().getAttribute(name);
        }
        return value;
    }

    /**
     * 将当前HttpServletRequest对象中的属性保存到Map对象中
     *
     * @param request
     * @return Map对象
     */
    public static Map<String, Object> getAttributes(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Enumeration<?> en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            map.put(name, request.getAttribute(name));
        }
        return map;
    }

    /**
     * 转发地址
     *
     * @param url
     * @param request
     * @param response
     */
    public static void forward(String url, HttpServletRequest request,
                               HttpServletResponse response) {
        try {
            request.getRequestDispatcher(url).forward(request, response);
        } catch (Exception e) {
            ExceptionHandler.throwException(e);
        }
    }

    /**
     * 跳转指定地址
     *
     * @param response
     * @param url
     */
    public static void sendRedirect(HttpServletResponse response, String url) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            ExceptionHandler.throwException(e);
        }
    }


    /**
     * 根据请求参数值按照Ognl语法设置bean对象的属性<br/>
     *
     * @param request
     * @param bean
     * @return bean
     * @see #setValue(Object, String, String[])
     */
    @SuppressWarnings("unchecked")
    public static Object geBean(HttpServletRequest request, Object bean) {
        setValue(bean, request.getParameterMap());
        return bean;
    }

    /**
     * 获取指定名称的Cookie对象,如果不存在返回null
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * 获取指定Cookie的值,如果不存在返回默认值
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookieValue(HttpServletRequest request,
                                        String cookieName, String defaultValue) {
        Cookie cookie = getCookie(request, cookieName);
        return cookie != null ? cookie.getValue() : defaultValue;
    }

    /**
     * 保存当前请求的URL地址,含请求参数
     *
     * @param request
     * @return
     */
    public static void saveCurrentURL(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String url = request.getRequestURI() + "?" + request.getQueryString();
        // 前缀:"http://?=",防止键值覆盖
        session.setAttribute("http://?=" + session.getId(), url);
    }

    /**
     * 获取上一次保持的请求URL地址
     *
     * @param request
     * @return
     */
    public static String getLastSavedURL(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (String) session.getAttribute("http://?=" + session.getId());
    }

    /**
     * 获取请求参数的long值<br>
     *
     * @param request
     * @param paraName
     * @param defaultValue
     * @return
     */
    public static long getLong(HttpServletRequest request, String paraName,
                               long defaultValue) {
        try {
            String value = request.getParameter(paraName);
            if (value != null)
                return Long.parseLong(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 获取请求参数的double值<br>
     *
     * @param request
     * @param paraName
     * @param defaultValue
     * @return
     */
    public static double getDouble(HttpServletRequest request, String paraName,
                                   double defaultValue) {
        try {
            String value = request.getParameter(paraName);
            if (value != null)
                return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 获取请求参数的int值<br>
     *
     * @param request
     * @param paraName
     * @param defaultValue
     * @return
     */
    public static int getInt(HttpServletRequest request, String paraName,
                             int defaultValue) {
        try {
            String value = request.getParameter(paraName);
            if (value != null)
                return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 获取请求参数的float值<br>
     *
     * @param request
     * @param paraName
     * @param defaultValue
     * @return
     */
    public static float getFloat(HttpServletRequest request, String paraName,
                                 float defaultValue) {
        try {
            String value = request.getParameter(paraName);
            if (value != null)
                return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 获取请求参数的boolean值<br>
     *
     * @param request
     * @param paraName
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(HttpServletRequest request,
                                     String paraName, boolean defaultValue) {
        try {
            String value = request.getParameter(paraName);
            if (value != null)
                return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 获取请求参数的boolean值<br>
     *
     * @param request
     * @param paraName
     * @param defaultValue
     * @return
     */
    public static String getString(HttpServletRequest request, String paraName,
                                   String defaultValue) {
        String value = request.getParameter(paraName);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取请求参数,以指定格式进行编码<br>
     *
     * @param request
     * @param paraName
     * @param encode   编码格式
     * @return 编码后的参数值
     */
    public static String getParameter(HttpServletRequest request,
                                      String paraName, String encode) {
        String[] values = getParameterValues(request, paraName, encode);
        if (values == null || values.length == 0)
            return null;
        return values[0];
    }

    /**
     * 获取请求参数,以指定格式进行编码<br>
     *
     * @param request
     * @param paraName
     * @param encode
     * @return 编码后的参数值
     */
    public static String[] getParameterValues(HttpServletRequest request,
                                              String paraName, String encode) {
        String encoding = StringUtils.defaultIfNull(
                request.getCharacterEncoding(), "iso-8859-1");
        String[] values = request.getParameterValues(paraName);
        if (!encoding.equals(encode) && values != null) {
            for (int i = 0; i < values.length; i++) {
                if (StringUtils.isNotEmpty(values[i])) {
                    values[i] = StringUtils.transCode(values[i], encoding,
                            encode);
                }
            }
        }
        return values;
    }

    /**
     * 是否存在指定的请求参数
     *
     * @param request
     * @param paraName
     * @return
     */
    public static boolean hasParameter(HttpServletRequest request,
                                       String paraName) {
        return request.getParameterMap().containsKey(paraName);
    }

    /**
     * 将HttpServletRequest对象中的请求参数保存到HttpServletRequest的属性中<br>
     * 如果参数有多个值,则保存为数组,否则,则保存为字符串
     *
     * @param request
     */
    public static void saveParametersInRequest(HttpServletRequest request) {
        Enumeration<?> en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            String[] pValues = request.getParameterValues(name);
            request.setAttribute(name, pValues.length > 1 ? pValues
                    : pValues[0]);
        }
    }

    /**
     * 将HttpServletRequest对象中的请求参数保存到HttpSession的属性中<br>
     * 如果参数有多个值,则保存为数组,否则,则保存为字符串
     *
     * @param request
     */
    public static void saveParametersInSession(HttpServletRequest request) {
        Enumeration<?> en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            String[] pValues = request.getParameterValues(name);
            request.getSession().setAttribute(name,
                    pValues.length > 1 ? pValues : pValues[0]);
        }
    }


    /**
     * 向客户端输出内容
     *
     * @param response
     * @param content
     */
    public static void responseWrite(HttpServletResponse response,
                                     String content) {
        try {
            response.setHeader("Cache-Control", "no-cache");
            response.getWriter().write(content != null ? content : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭客户端页面
     *
     * @param response
     */
    public static void closeWindow(HttpServletResponse response) {
        responseWrite(response, JavaScript.function("window.close"));
    }


    /**
     * 按照Ognl语法对目标对象进行赋值,key为Ognl表达式,value为属性值的字符串形式<br/>
     *
     * @param bean
     * @param map
     * @see #setValue(Object, String, String[])
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setValue(Object bean, Map<String, String[]> map) {
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            try {
                setValue(bean, entry.getKey(), entry.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 客户端JS函数调用
     *
     * @param response
     * @param callback
     * @param args
     */
    public static void jsonp(HttpServletResponse response, String callback,
                             String... args) {
        responseWrite(response, JavaScript.function(callback, args));
    }

    /**
     * 按照Ognl语法对目标对象属性赋值<br/>
     * 这里,属性值以字符串数组形式存在,因此需要进行类型转换:
     * 1) 原始类型,取首元素转换成字符串对应的值<br>
     * 2) 枚举类型,取首元素根据字符串对应的名称获取实例<br>
     * 3) 数组类型,根据元素类型进行转换<br>
     * 4) 集合类型,如果可实例化,则使用默认构造方法的实例,如果为接口或抽象类型,则采用ArrayList类型作为其实例<br>
     * 5) 如果集合类型指定泛型,则将元素转换为对应泛型类型,否则默认使用字符串类型作为其元素类型<br>
     * 6) 其他类型,取首元素使用构造参数为String类型构造方法进行实例化 <br>
     * 7) 如果以上实例化方法失败,则不进行赋值
     *
     * @param target
     * @param key
     * @param values
     * @throws Exception
     */
    private static void setValue(Object target, String key, String[] values) throws Exception {
        Method setter = Reflections.setter(target.getClass(), key);
        if (setter == null)
            return;
        // 属性类型
        Class<?> proType = setter.getParameterTypes()[0];
        // 属性值
        Object proValue = null;
        if (proType.isArray()) {// 如果属性是数组
            // 数组元素类型
            Class<?> componentType = proType.getComponentType();
            // 创建元素类型数组实例
            proValue = Array.newInstance(componentType,
                    values.length);
            // 为数组赋值
            for (int i = 0; i < values.length; i++) {
                Array.set(proValue, i, Reflections.simpleInstance(
                        componentType, values[i]));
            }
        } else if (Collection.class.isAssignableFrom(proType)) {// 属性为集合类型

            // 如果是抽象类型,默认实例化为ArrayList,否则用实际类型实例化
            Collection coll = Reflections.isAbstract(proType) ? new ArrayList()
                    : (Collection) proType.newInstance();
            // Setter方法的参数泛型
            Type type = setter.getGenericParameterTypes()[0];
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                // 泛型类型
                Type actualType = pType.getActualTypeArguments()[0];
                Class<?> elementType;
                // 如果泛型参数还是泛型, 则取RawType
                if (actualType instanceof ParameterizedType) {
                    elementType = (Class<?>) ((ParameterizedType) actualType)
                            .getRawType();
                } else {
                    elementType = (Class<?>) actualType;
                }
                for (int i = 0; i < values.length; i++) {
                    coll.add(Reflections.simpleInstance(elementType,
                            values[i]));
                }
            } else {
                // 没有指定泛型,默认String类型
                for (int i = 0; i < values.length; i++) {
                    coll.add(values[i]);
                }
            }
            proValue = coll;

        } else {// 其他类型
            String paramValue = values[0];
            if (paramValue != null) {
                // 获取属性类型实例
                proValue = Reflections.simpleInstance(proType, paramValue);
            }
        }
        if (proValue != null) {
            // 调用Setter方法设置属性
            setter.invoke(target, proValue);
        }

    }


}
