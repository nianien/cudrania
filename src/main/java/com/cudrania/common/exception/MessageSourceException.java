package com.cudrania.common.exception;

import org.springframework.context.MessageSourceResolvable;

/**
 * 实现MessageSourceResolvable接口的异常,用于信息的资源国际化
 *
 * @author skyfalling
 */
@DefinedException
public class MessageSourceException extends RuntimeException implements MessageSourceResolvable {

    private final String[] codes;

    private final Object[] arguments;

    private final String defaultMessage;

    /**
     * @param code he codes to be used to resolve this message
     */
    public MessageSourceException(String code) {
        this(code, null, null);
    }


    /**
     * @param code  he codes to be used to resolve this message
     * @param cause
     */
    public MessageSourceException(String code, Throwable cause) {
        this(code, null, cause);
    }

    /**
     * @param code      he codes to be used to resolve this message
     * @param arguments the array of arguments to be used to resolve this message
     */
    public MessageSourceException(String code, Object[] arguments) {
        this(code, arguments, null);
    }

    /**
     * @param code      he codes to be used to resolve this message
     * @param arguments the array of arguments to be used to resolve this message
     * @param cause
     */
    public MessageSourceException(String code, Object[] arguments, Throwable cause) {
        this(code, arguments, code, cause);
    }

    protected MessageSourceException(String code, Object[] arguments, String defaultMessage, Throwable cause) {
        super(code, cause);
        this.codes = new String[]{code};
        this.arguments = arguments;
        this.defaultMessage = defaultMessage;
    }


    @Override
    public String[] getCodes() {
        return codes;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
