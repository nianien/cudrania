package com.cudrania.spring.handlermapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * {@link org.springframework.web.bind.annotation.RequestMapping}的包装类,用于存储注解对象的属性设置
 *
 * @author skyfalling
 */
public class RequestMappingAnnotationWrapper {

    private String[] value = new String[0];

    private RequestMethod[] method = new RequestMethod[0];

    private String[] params = new String[0];

    private String[] headers = new String[0];

    private String[] consumes = new String[0];

    private String[] produces = new String[0];


    public RequestMappingAnnotationWrapper() {
    }

    public RequestMappingAnnotationWrapper(RequestMapping annotation) {
        if (annotation != null) {
            this.value = value();
            this.method = annotation.method();
            this.params = annotation.params();
            this.headers = annotation.headers();
            this.consumes = annotation.consumes();
            this.produces = annotation.produces();
        }
    }




    public String[] value() {
        return value;
    }

    public void value(String[] value) {
        this.value = value;
    }

    public RequestMethod[] method() {
        return method;
    }

    public void method(RequestMethod[] method) {
        this.method = method;
    }

    public String[] params() {
        return params;
    }

    public void params(String[] params) {
        this.params = params;
    }

    public String[] headers() {
        return headers;
    }

    public void headers(String[] headers) {
        this.headers = headers;
    }

    public String[] consumes() {
        return consumes;
    }

    public void consumes(String[] consumes) {
        this.consumes = consumes;
    }

    public String[] produces() {
        return produces;
    }

    public void produces(String[] produces) {
        this.produces = produces;
    }

}
