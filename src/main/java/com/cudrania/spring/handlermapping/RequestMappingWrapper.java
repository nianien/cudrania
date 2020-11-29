package com.cudrania.spring.handlermapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;

/**
 * {@link org.springframework.web.bind.annotation.RequestMapping}包装类,用于更新属性设置
 *
 * @author skyfalling
 */
public class RequestMappingWrapper implements RequestMapping {

    private String[] path = new String[0];
    private RequestMethod[] method = new RequestMethod[0];
    private String[] params = new String[0];
    private String[] headers = new String[0];
    private String[] consumes = new String[0];
    private String[] produces = new String[0];
    private RequestMapping annotation;


    public RequestMappingWrapper(RequestMapping annotation) {
        if (annotation != null) {
            this.annotation = annotation;
            this.path = annotation.path();
            this.method = annotation.method();
            this.params = annotation.params();
            this.headers = annotation.headers();
            this.consumes = annotation.consumes();
            this.produces = annotation.produces();
        }
    }


    @Override
    public String name() {
        return annotation != null ? annotation.name() : "";
    }

    @Override
    public String[] value() {
        return path;
    }

    @Override
    public String[] path() {
        return path;
    }

    public void path(String[] path) {
        this.path = path;
    }

    @Override
    public RequestMethod[] method() {
        return method;
    }

    public void method(RequestMethod[] method) {
        this.method = method;
    }

    @Override
    public String[] params() {
        return params;
    }

    public void params(String[] params) {
        this.params = params;
    }

    @Override
    public String[] headers() {
        return headers;
    }

    public void headers(String[] headers) {
        this.headers = headers;
    }

    @Override
    public String[] consumes() {
        return consumes;
    }

    public void consumes(String[] consumes) {
        this.consumes = consumes;
    }

    @Override
    public String[] produces() {
        return produces;
    }

    public void produces(String[] produces) {
        this.produces = produces;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotation != null ? annotation.annotationType() : null;
    }
}
