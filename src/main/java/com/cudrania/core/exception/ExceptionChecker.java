package com.cudrania.core.exception;

/**
 * 异常处理类,以运行时异常的形式将指定的异常抛出
 *
 * @author skyfalling
 */
public class ExceptionChecker {

    /**
     * 抛出异常信息<br/>
     * 注意: 这里的返回值只是为了绕过编译器的异常检查,不会真正返回
     *
     * @param e
     * @return
     */
    public static RuntimeException throwException(Throwable e) {
        if (e == null) {
            throw skip(new NullPointerException());
        }
        throw ExceptionChecker.<RuntimeException>doThrowException(e);
    }

    /**
     * 抛出异常信息<br/>
     * 注意: 这里的返回值只是为了绕过编译器的异常检查,不会真正返回
     *
     * @param cause
     * @return
     */
    public static RuntimeException throwException(String cause) {
        throw skip(new RuntimeException(cause));
    }


    /**
     * 检查obj对象是否为空,如果为空,则抛出异常
     *
     * @param obj  被检查的对象
     * @param info 抛出异常的信息
     */
    public static void throwIfNull(Object obj, String info) {
        throwIfNull(obj, skip(new NullPointerException(info)));
    }

    /**
     * 检查obj对象是否为空,如果为空,则抛出异常
     *
     * @param obj   被检查的对象
     * @param cause 抛出的异常
     */
    public static void throwIfNull(Object obj, Exception cause) {
        if (obj == null) {
            throwException(cause);
        }
    }


    /**
     * 根据表达式判断是否抛出异常,如果表达式为真,则抛出异常
     *
     * @param expression 布尔表达式
     * @param info       抛出异常的信息
     */
    public static void throwIf(boolean expression, String info) {
        throwIf(expression, skip(new RuntimeException(info)));
    }

    /**
     * 根据表达式判断是否抛出异常,如果表达式为真,则抛出异常
     *
     * @param expression 布尔表达式
     * @param cause      抛出的异常
     */
    public static void throwIf(boolean expression, Exception cause) {
        if (expression) {
            throwException(cause);
        }
    }


    /**
     * 这里的返回值只是为了绕过编译器的异常检查,不会真正返回
     *
     * @param e
     * @param <T>
     * @return
     * @throws T
     */
    private static <T extends Throwable> T doThrowException(Throwable e) throws T {
        throw (T) e;
    }

    private static <T extends Exception> T skip(T ex) {
        int size = ex.getStackTrace().length - 1;
        StackTraceElement[] st = new StackTraceElement[size];
        System.arraycopy(ex.getStackTrace(), 1, st, 0, size);
        ex.setStackTrace(st);
        return ex;
    }

}
