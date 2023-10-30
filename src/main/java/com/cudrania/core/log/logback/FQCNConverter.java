package com.cudrania.core.log.logback;

import ch.qos.logback.classic.pattern.NamedConverter;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 打印业务日志的方法调用栈信息,包括所属类/方法/行信息,用于解决三方库封装自定义Logger时无法正确打印FQCN的问题<p>
 * <pre>
 *     &lt;configuration&gt;
 *        &lt;conversionRule conversionWord="CML" converterClass="com.cudrania.side.logback.FQCNConverter"&gt;
 *        ...
 *        &lt;appender name="console" class="ch.qos.logback.core.ConsoleAppender"&gt;
 *            &lt;encoder&gt;
 *               &lt;pattern&gt;%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %CML{54} - %msg%n&lt;/pattern&gt;
 *           &lt;/encoder&gt;
 *        &lt;/appender&gt;
 *          ...
 *     &lt;/configuration&gt;
 * </pre>
 *
 * @author scorpio
 */
public class FQCNConverter extends NamedConverter {

    private String postfix;

    /**
     * 默认Logger类后缀
     *
     * @param postfix
     */
    public FQCNConverter(String postfix) {
        this.postfix = postfix;
    }

    public FQCNConverter() {
        this("Logger");
    }

    @Override
    protected String getFullyQualifiedName(ILoggingEvent event) {
        StackTraceElement[] cda = event.getCallerData();
        if (cda != null && cda.length > 0) {
            for (StackTraceElement stackTraceElement : cda) {
                if (!stackTraceElement.getClassName().endsWith(postfix)) {
                    return stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber();
                }
            }
            return cda[0].getClassName() + "." + cda[0].getMethodName() + ":" + cda[0].getLineNumber();
        } else {
            return CallerData.CALLER_DATA_NA;
        }
    }

}

