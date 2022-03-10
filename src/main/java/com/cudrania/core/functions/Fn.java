package com.cudrania.core.functions;

import lombok.SneakyThrows;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 多个参数的函数定义
 * <pre>
 * bi = 2
 * tri = 3
 * quad = 4
 * quin = 5
 * hexa = 6
 * hept = 7
 * octa = 8
 * nona = 9
 * deca = 10
 * </pre>
 */
public class Fn {
    public static <T> Supplier<T> of(Supplier<T> lambda) {
        return lambda;
    }


    public static <P> Consumer<P> of(Consumer<P> lambda) {
        return lambda;
    }


    public static <P1, P2> BiConsumer<P1, P2> of(BiConsumer<P1, P2> lambda) {
        return lambda;
    }


    public static <P1, P2, P3> TriConsumer<P1, P2, P3> of(TriConsumer<P1, P2, P3> lambda) {
        return lambda;
    }


    public static <P1, P2, P3, P4> QuaConsumer<P1, P2, P3, P4> of(QuaConsumer<P1, P2, P3, P4> lambda) {
        return lambda;
    }


    public static <P1, P2, P3, P4, P5> QuiConsumer<P1, P2, P3, P4, P5> of(QuiConsumer<P1, P2, P3, P4, P5> lambda) {
        return lambda;
    }


    public static <P1, P2, P3, P4, P5, P6> HexConsumer<P1, P2, P3, P4, P5, P6> of(HexConsumer<P1, P2, P3, P4, P5, P6> lambda) {
        return lambda;
    }


    public static <P1, P2, P3, P4, P5, P6, P7> HepConsumer<P1, P2, P3, P4, P5, P6, P7> of(HepConsumer<P1, P2, P3, P4, P5, P6, P7> lambda) {
        return lambda;
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8> OctConsumer<P1, P2, P3, P4, P5, P6, P7, P8> of(OctConsumer<P1, P2, P3, P4, P5, P6, P7, P8> lambda) {
        return lambda;
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9> NonConsumer<P1, P2, P3, P4, P5, P6, P7, P8, P9> of(NonConsumer<P1, P2, P3, P4, P5, P6, P7, P8, P9> lambda) {
        return lambda;
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> DecConsumer<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> of(DecConsumer<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> lambda) {
        return lambda;
    }


    public static <P, R> Function<P, R> of(Function<P, R> lambda) {
        return lambda;
    }


    public static <P1, P2, R> BiFunction<P1, P2, R> of(BiFunction<P1, P2, R> lambda) {
        return lambda;
    }


    public static <P1, P2, P3, R> TriFunction<P1, P2, P3, R> of(TriFunction<P1, P2, P3, R> lambda) {
        return lambda;
    }

    public static <P1, P2, P3, P4, R> QuaFunction<P1, P2, P3, P4, R> of(QuaFunction<P1, P2, P3, P4, R> lambda) {
        return lambda;
    }

    public static <P1, P2, P3, P4, P5, R> QuiFunction<P1, P2, P3, P4, P5, R> of(QuiFunction<P1, P2, P3, P4, P5, R> lambda) {
        return lambda;
    }

    public static <P1, P2, P3, P4, P5, P6, R> HexFunction<P1, P2, P3, P4, P5, P6, R> of(HexFunction<P1, P2, P3, P4, P5, P6, R> lambda) {
        return lambda;
    }

    public static <P1, P2, P3, P4, P5, P6, P7, R> HepFunction<P1, P2, P3, P4, P5, P6, P7, R> of(HepFunction<P1, P2, P3, P4, P5, P6, P7, R> lambda) {
        return lambda;
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, R> OctFunction<P1, P2, P3, P4, P5, P6, P7, P8, R> of(OctFunction<P1, P2, P3, P4, P5, P6, P7, P8, R> lambda) {
        return lambda;
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> NonFunction<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> of(NonFunction<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> lambda) {
        return lambda;
    }

    public static <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> DecFunction<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> of(DecFunction<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> lambda) {
        return lambda;
    }


    @FunctionalInterface
    public interface Supplier<T> extends Lambda {
        T invoke();


    }

    @FunctionalInterface
    public interface Consumer<P> extends Lambda {
        void invoke(P p1);


    }

    @FunctionalInterface
    public interface BiConsumer<P1, P2> extends Lambda {
        void invoke(P1 p1, P2 p2);


    }

    @FunctionalInterface
    public interface TriConsumer<P1, P2, P3> extends Lambda {
        void invoke(P1 p1, P2 p2, P3 p3);


    }

    @FunctionalInterface
    public interface QuaConsumer<P1, P2, P3, P4> extends Lambda {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4);


    }

    @FunctionalInterface
    public interface QuiConsumer<P1, P2, P3, P4, P5> extends Lambda {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);


    }

    @FunctionalInterface
    public interface HexConsumer<P1, P2, P3, P4, P5, P6> extends Lambda {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);


    }

    @FunctionalInterface
    public interface HepConsumer<P1, P2, P3, P4, P5, P6, P7> extends Lambda {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);


    }

    @FunctionalInterface
    public interface OctConsumer<P1, P2, P3, P4, P5, P6, P7, P8> extends Lambda {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);


    }

    @FunctionalInterface
    public interface NonConsumer<P1, P2, P3, P4, P5, P6, P7, P8, P9> extends Lambda {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);


    }

    @FunctionalInterface
    public interface DecConsumer<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> extends Lambda {
        void invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10);


    }

    @FunctionalInterface
    public interface Function<P, R> extends Lambda {
        R invoke(P p1);


    }

    @FunctionalInterface
    public interface BiFunction<P1, P2, R> extends Lambda {
        R invoke(P1 p1, P2 p2);


    }

    @FunctionalInterface
    public interface TriFunction<P1, P2, P3, R> extends Lambda {
        R invoke(P1 p1, P2 p2, P3 p3);


    }

    @FunctionalInterface
    public interface QuaFunction<P1, P2, P3, P4, R> extends Lambda {
        R invoke(P1 p1, P2 p2, P3 p3, P4 p4);


    }

    @FunctionalInterface
    public interface QuiFunction<P1, P2, P3, P4, P5, R> extends Lambda {
        R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);


    }

    @FunctionalInterface
    public interface HexFunction<P1, P2, P3, P4, P5, P6, R> extends Lambda {
        R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);


    }

    @FunctionalInterface
    public interface HepFunction<P1, P2, P3, P4, P5, P6, P7, R> extends Lambda {
        R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);


    }

    @FunctionalInterface
    public interface OctFunction<P1, P2, P3, P4, P5, P6, P7, P8, R> extends Lambda {
        R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);


    }

    @FunctionalInterface
    public interface NonFunction<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> extends Lambda {
        R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);


    }


    @FunctionalInterface
    public interface DecFunction<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> extends Lambda {
        R invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10);

    }

    /**
     * 用于提取Lambda对象
     */
    public interface Lambda extends Serializable {

        default String name() {
            return Fn.lambda(this).getImplMethodName();
        }
    }

    /**
     * 缓存
     */
    private static Map<Serializable, SerializedLambda> cache = new WeakHashMap();

    @SneakyThrows
    public static SerializedLambda lambda(Serializable lambda) {
        return cache.computeIfAbsent(lambda, k -> lambda0(k));
    }

    @SneakyThrows
    private static SerializedLambda lambda0(Serializable lambda) {
        Method method = lambda.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(Boolean.TRUE);
        //调用writeReplace()方法，返回一个SerializedLambda对象
        return (SerializedLambda) method.invoke(lambda);
    }
}
