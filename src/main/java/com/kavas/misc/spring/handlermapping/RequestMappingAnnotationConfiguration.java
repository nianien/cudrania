package com.kavas.misc.spring.handlermapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

/**
 * 由于注解类型无法实例化,因此这里定义用于存储{@link org.springframework.web.bind.annotation.RequestMapping}配置的对象,
 *
 * @author skyfalling
 */
public class RequestMappingAnnotationConfiguration {

    private String[] value = new String[0];

    private RequestMethod[] method = new RequestMethod[0];

    private String[] params = new String[0];

    private String[] headers = new String[0];

    private String[] consumes = new String[0];

    private String[] produces = new String[0];

    private RequestCondition<?> requestCondition;

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

    public RequestCondition<?> requestCondition() {
        return requestCondition;
    }

    public void requestCondition(RequestCondition<?> requestCondition) {
        this.requestCondition = requestCondition;
    }
}
