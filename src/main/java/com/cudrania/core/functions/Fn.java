package com.cudrania.core.functions;

import lombok.SneakyThrows;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;

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


    /**
     * 指定lambda实例，示例: <code>Fn.of(Map&lt;String, String>::put);</code>
     */
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
        T get();
    }

    @FunctionalInterface
    public interface Consumer<P> extends Lambda, Binder<P, Runnable>, java.util.function.Consumer<P> {
        void accept(P p1);

        default Runnable bind(P p1) {
            return () -> this.accept(p1);
        }
    }

    @FunctionalInterface
    public interface BiConsumer<P1, P2> extends Lambda, Binder<P1, Consumer<P2>>, java.util.function.BiConsumer<P1, P2> {
        void accept(P1 p1, P2 p2);

        default Consumer<P2> bind(P1 p1) {
            return (p2) -> this.accept(p1, p2);
        }

    }

    @FunctionalInterface
    public interface TriConsumer<P1, P2, P3> extends Lambda, Binder<P1, BiConsumer<P2, P3>> {
        void accept(P1 p1, P2 p2, P3 p3);

        default BiConsumer<P2, P3> bind(P1 p1) {
            return (p2, p3) -> this.accept(p1, p2, p3);
        }
    }

    @FunctionalInterface
    public interface QuaConsumer<P1, P2, P3, P4> extends Lambda, Binder<P1, TriConsumer<P2, P3, P4>> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4);

        default TriConsumer<P2, P3, P4> bind(P1 p1) {
            return (p2, p3, p4) -> this.accept(p1, p2, p3, p4);
        }

    }

    @FunctionalInterface
    public interface QuiConsumer<P1, P2, P3, P4, P5> extends Lambda, Binder<P1, QuaConsumer<P2, P3, P4, P5>> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);

        default QuaConsumer<P2, P3, P4, P5> bind(P1 p1) {
            return (p2, p3, p4, p5) -> this.accept(p1, p2, p3, p4, p5);
        }
    }

    @FunctionalInterface
    public interface HexConsumer<P1, P2, P3, P4, P5, P6> extends Lambda, Binder<P1, QuiConsumer<P2, P3, P4, P5, P6>> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);

        default QuiConsumer<P2, P3, P4, P5, P6> bind(P1 p1) {
            return (p2, p3, p4, p5, p6) -> this.accept(p1, p2, p3, p4, p5, p6);
        }
    }

    @FunctionalInterface
    public interface HepConsumer<P1, P2, P3, P4, P5, P6, P7> extends Lambda, Binder<P1, HexConsumer<P2, P3, P4, P5, P6, P7>> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);

        default HexConsumer<P2, P3, P4, P5, P6, P7> bind(P1 p1) {
            return (p2, p3, p4, p5, p6, p7) -> this.accept(p1, p2, p3, p4, p5, p6, p7);
        }
    }

    @FunctionalInterface
    public interface OctConsumer<P1, P2, P3, P4, P5, P6, P7, P8> extends Lambda, Binder<P1, HepConsumer<P2, P3, P4, P5, P6, P7, P8>> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);

        default HepConsumer<P2, P3, P4, P5, P6, P7, P8> bind(P1 p1) {
            return (p2, p3, p4, p5, p6, p7, p8) -> this.accept(p1, p2, p3, p4, p5, p6, p7, p8);
        }
    }

    @FunctionalInterface
    public interface NonConsumer<P1, P2, P3, P4, P5, P6, P7, P8, P9> extends Lambda, Binder<P1, OctConsumer<P2, P3, P4, P5, P6, P7, P8, P9>> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);

        default OctConsumer<P2, P3, P4, P5, P6, P7, P8, P9> bind(P1 p1) {
            return (p2, p3, p4, p5, p6, p7, p8, p9) -> this.accept(p1, p2, p3, p4, p5, p6, p7, p8, p9);
        }
    }

    @FunctionalInterface
    public interface DecConsumer<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> extends Lambda, Binder<P1, NonConsumer<P2, P3, P4, P5, P6, P7, P8, P9, P10>> {
        void accept(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10);

        default NonConsumer<P2, P3, P4, P5, P6, P7, P8, P9, P10> bind(P1 p1) {
            return (p2, p3, p4, p5, p6, p7, p8, p9, p10) -> this.accept(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
        }
    }

    @FunctionalInterface
    public interface Function<P, R> extends Lambda, Binder<P, Callable<R>>, java.util.function.Function<P, R> {
        R apply(P p1);

        default Callable<R> bind(P p) {
            return () -> this.apply(p);
        }
    }

    @FunctionalInterface
    public interface BiFunction<P1, P2, R> extends Lambda, Binder<P1, Function<P2, R>>, java.util.function.BiFunction<P1, P2, R> {
        R apply(P1 p1, P2 p2);

        default Function<P2, R> bind(P1 p1) {
            return (p2) -> this.apply(p1, p2);
        }

    }

    @FunctionalInterface
    public interface TriFunction<P1, P2, P3, R> extends Lambda, Binder<P1, BiFunction<P2, P3, R>> {
        R apply(P1 p1, P2 p2, P3 p3);

        default BiFunction<P2, P3, R> bind(P1 p1) {
            return (p2, p3) -> this.apply(p1, p2, p3);
        }
    }

    @FunctionalInterface
    public interface QuaFunction<P1, P2, P3, P4, R> extends Lambda, Binder<P1, TriFunction<P2, P3, P4, R>> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4);

        default TriFunction<P2, P3, P4, R> bind(P1 p1) {
            return (p2, p3, p4) -> this.apply(p1, p2, p3, p4);
        }
    }

    @FunctionalInterface
    public interface QuiFunction<P1, P2, P3, P4, P5, R> extends Lambda, Binder<P1, QuaFunction<P2, P3, P4, P5, R>> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);

        default QuaFunction<P2, P3, P4, P5, R> bind(P1 p1) {
            return (p2, p3, p4, p5) -> this.apply(p1, p2, p3, p4, p5);
        }
    }

    @FunctionalInterface
    public interface HexFunction<P1, P2, P3, P4, P5, P6, R> extends Lambda, Binder<P1, QuiFunction<P2, P3, P4, P5, P6, R>> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);

        default QuiFunction<P2, P3, P4, P5, P6, R> bind(P1 p1) {
            return (p2, p3, p4, p5, p6) -> this.apply(p1, p2, p3, p4, p5, p6);
        }
    }

    @FunctionalInterface
    public interface HepFunction<P1, P2, P3, P4, P5, P6, P7, R> extends Lambda, Binder<P1, HexFunction<P2, P3, P4, P5, P6, P7, R>> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);

        default HexFunction<P2, P3, P4, P5, P6, P7, R> bind(P1 p1) {
            return (p2, p3, p4, p5, p6, p7) -> this.apply(p1, p2, p3, p4, p5, p6, p7);
        }
    }

    @FunctionalInterface
    public interface OctFunction<P1, P2, P3, P4, P5, P6, P7, P8, R> extends Lambda, Binder<P1, HepFunction<P2, P3, P4, P5, P6, P7, P8, R>> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);

        default HepFunction<P2, P3, P4, P5, P6, P7, P8, R> bind(P1 p1) {
            return (p2, p3, p4, p5, p6, p7, p8) -> this.apply(p1, p2, p3, p4, p5, p6, p7, p8);
        }
    }

    @FunctionalInterface
    public interface NonFunction<P1, P2, P3, P4, P5, P6, P7, P8, P9, R> extends Lambda, Binder<P1, OctFunction<P2, P3, P4, P5, P6, P7, P8, P9, R>> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);

        default OctFunction<P2, P3, P4, P5, P6, P7, P8, P9, R> bind(P1 p1) {
            return (p2, p3, p4, p5, p6, p7, p8, p9) -> this.apply(p1, p2, p3, p4, p5, p6, p7, p8, p9);
        }
    }


    @FunctionalInterface
    public interface DecFunction<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> extends Lambda, Binder<P1, NonFunction<P2, P3, P4, P5, P6, P7, P8, P9, P10, R>> {
        R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9, P10 p10);

        default NonFunction<P2, P3, P4, P5, P6, P7, P8, P9, P10, R> bind(P1 p1) {
            return (p2, p3, p4, p5, p6, p7, p8, p9, p10) -> this.apply(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
        }
    }


    /**
     * 用于提取Lambda对象
     */
    public interface Binder<P, FUN> {
        /**
         * 绑定lambda第一个参数
         *
         * @param p
         * @return
         */
        FUN bind(P p);
    }

    /**
     * 用于提取Lambda对象
     */
    public interface Lambda extends Serializable {

        /**
         * 获取lambda引用方法名称
         *
         * @return
         */
        default String name() {
            return lambda().getImplMethodName();
        }

        /**
         * 获取SerializedLambda对象
         *
         * @return
         */
        default SerializedLambda lambda() {
            return Fn.lambda(this);
        }

    }

    /**
     * 缓存
     */
    private static Map<Serializable, SerializedLambda> cache = new WeakHashMap();

    /**
     * 获取SerializedLambda对象, 入参必须是方法引用
     *
     * @param lambda
     * @return
     */
    @SneakyThrows
    private static SerializedLambda lambda(Serializable lambda) {
        return cache.computeIfAbsent(lambda, k -> lambda0(k));
    }

    /**
     * 获取SerializedLambda对象, 入参必须是方法引用
     *
     * @param lambda
     * @return
     */
    @SneakyThrows
    private static SerializedLambda lambda0(Serializable lambda) {
        Method method = lambda.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(Boolean.TRUE);
        //调用writeReplace()方法，返回一个SerializedLambda对象
        return (SerializedLambda) method.invoke(lambda);
    }
}
