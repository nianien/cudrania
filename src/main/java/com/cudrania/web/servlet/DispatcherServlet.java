package com.cudrania.web.servlet;

import com.nianien.core.exception.ExceptionHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 可以根据请求信息调用不同处理方法的Servlet实现类<br>
 * 默认根据parameter字段对应请求参数的值调用同名的方法,同时也可以根据请求对象HttpServletRequest自定义获取处理方法的实现规则<br>
 * 需要注意的是,处理方法的参数类型必须为(HttpServletRequest request, HttpServletResponse response)
 *
 * @author skyfalling
 */
public class DispatcherServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 请求处理方法的参数名
     */
    protected String parameter = "method";

    /**
     * 根据请求获取处理方法的名称
     *
     * @param request
     * @return
     */
    protected String getMethod(HttpServletRequest request) {
        return request.getParameter(this.parameter);
    }


    @Override
    protected void service(HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException {
        String method = this.getMethod(request);
        ExceptionHandler
                .throwIf(
                        "service".equals(method),
                        "Prohibit calling service(HttpServletRequest request,HttpServletResponse response) method!");
        try {
            this.getClass().getMethod(method, HttpServletRequest.class,
                    HttpServletResponse.class).invoke(this, request, response);
        } catch (Exception e) {
            ExceptionHandler.throwException(e);
        }
    }

}
