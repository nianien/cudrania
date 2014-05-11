package com.kavas.misc.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Map;

/**
 * 清除请求参数首尾的空白字符的过滤器
 *
 * @author skyfalling
 */
public class RequestParameterTrimFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request = new HttpServletRequestWrapper((HttpServletRequest) request) {
            @Override
            public String getParameter(String name) {
                return resolve(super.getParameter(name));
            }

            @Override
            public Map getParameterMap() {
                Map map = super.getParameterMap();
                for (Object value : map.values()) {
                    if (value instanceof String[]) {
                        resolve((String[]) value);
                    }
                }
                return map;
            }

            @Override
            public String[] getParameterValues(String name) {
                return resolve(super.getParameterValues(name));
            }

        };
        System.out.println(request.getParameterMap());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }


    private String[] resolve(String[] values) {
        if (values != null)
            for (int i = 0; i < values.length; i++) {
                values[i] = resolve(values[i]);
            }
        return values;
    }

    private String resolve(String value) {
        return value == null ? value : value.trim();
    }
}
