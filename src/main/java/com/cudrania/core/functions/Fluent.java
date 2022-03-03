package com.cudrania.core.functions;


import com.cudrania.core.functions.Fn.*;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 构建Fluent API的Function工具<br/>
 * <p>
 * <ul>
 * <li>
 * 执行函数，并持有函数执行结果<br/>
 * {@link Fluent#$$(Function)}
 * </li>
 * <li>
 * 执行函数，并忽略函数执行结果（持有对象不变）<br/>
 * {@link Fluent#$(Consumer)}
 * </li>
 * </ul>
 * </p>
 *
 * @param <T> 输出参数类型
 * @author scorpio
 * @version 1.0.0
 */
public class Fluent<T> {

    /**
     * 绑定对象
     */
    protected T target;


    /**
     * 私有方法
     *
     * @param target
     */
    protected Fluent(T target) {
        this.target = target;
    }

    /**
     * 构建对象
     *
     * @param target
     * @param <T>
     * @return
     */
    public static <T> Fluent<T> of(T target) {
        return new Fluent<>(target);
    }


    /**
     * 获取持有对象
     *
     * @return
     */
    public T get() {
        return target;
    }


    /**
     * 如参数{@link Param#get()}返回结果有值,则调用函数并绑定返回结果
     *
     * @param param    条件参数
     * @param function 函数表达式
     * @param <P>      参数类型&函数第二个参数类型
     * @return
     */
    public <P> Fluent<T> $$(Param<P> param, BiFunction<T, P, T> function) {
        param.get().ifPresent(p -> this.target = function.apply(target, p));
        return this;
    }


    /**
     * 如参数{@link Param#get()}返回结果有值,则执行函数
     *
     * @param param    条件参数
     * @param consumer 函数表达式
     * @param <P>      参数类型&函数第二个参数类型
     * @return
     */
    public <P> Fluent<T> $(Param<P> param, BiConsumer<T, P> consumer) {
        param.get().ifPresent(p -> consumer.accept(target, p));
        return this;
    }

    /**
     * 执行函数，持有返回结果
     *
     * @param function
     * @param <R>      函数返回类型
     * @return
     */
    public <R> Fluent<R> $$(Function<T, R> function) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     *
     * @param function
     * @param p        函数参数
     * @param <R>      函数返回类型
     * @param <P>      参数类型
     * @return
     */
    public <R, P> Fluent<R> $$(BiFunction<T, P, R> function, P p) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     *
     * @param function
     * @param p1       第一个参数
     * @param p2       第二个参数
     * @param <R>      函数返回类型
     * @param <P1>     第一个参数类型
     * @param <P2>     第二个参数类型
     * @return
     */
    public <P1, P2, R> Fluent<R> $$(Function3<T, P1, P2, R> function, P1 p1, P2 p2) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     *
     * @param function
     * @param p1
     * @param p2
     * @param p3
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <R>
     * @return
     */
    public <P1, P2, P3, R> Fluent<R> $$(Function4<T, P1, P2, P3, R> function, P1 p1, P2 p2, P3 p3) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     *
     * @param function
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <P4>
     * @param <R>
     * @return
     */
    public <P1, P2, P3, P4, R> Fluent<R> $$(Function5<T, P1, P2, P3, P4, R> function, P1 p1, P2 p2, P3 p3, P4 p4) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3, p4);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     *
     * @param function
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param p5
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <P4>
     * @param <P5>
     * @param <R>
     * @return
     */
    public <P1, P2, P3, P4, P5, R> Fluent<R> $$(Function6<T, P1, P2, P3, P4, P5, R> function, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3, p4, p5);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     *
     * @param function
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param p5
     * @param p6
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <P4>
     * @param <P5>
     * @param <P6>
     * @param <R>
     * @return
     */
    public <P1, P2, P3, P4, P5, P6, R> Fluent<R> $$(Function7<T, P1, P2, P3, P4, P5, P6, R> function, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3, p4, p5, p6);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     *
     * @param function
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param p5
     * @param p6
     * @param p7
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <P4>
     * @param <P5>
     * @param <P6>
     * @param <P7>
     * @param <R>
     * @return
     */
    public <P1, P2, P3, P4, P5, P6, P7, R> Fluent<R> $$(Function8<T, P1, P2, P3, P4, P5, P6, P7, R> function, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3, p4, p5, p6, p7);
        }
        return f;
    }


    /**
     * 执行函数，持有返回结果
     *
     * @param function
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param p5
     * @param p6
     * @param p7
     * @param p8
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <P4>
     * @param <P5>
     * @param <P6>
     * @param <P7>
     * @param <P8>
     * @param <R>
     * @return
     */
    public <P1, P2, P3, P4, P5, P6, P7, P8, R> Fluent<R> $$(Function9<T, P1, P2, P3, P4, P5, P6, P7, P8, R> function, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3, p4, p5, p6, p7, p8);
        }
        return f;
    }


    /**
     * 执行函数，忽略返回结果
     *
     * @param consumer
     * @return
     */
    public Fluent<T> $(Consumer<T> consumer) {
        if (target != null) {
            consumer.accept(target);
        }
        return this;
    }

    /**
     * 执行函数，忽略返回结果
     *
     * @param consumer
     * @param p
     * @param <P>
     * @return
     */
    public <P> Fluent<T> $(BiConsumer<T, P> consumer, P p) {
        if (target != null) {
            consumer.accept(target, p);
        }
        return this;
    }


    /**
     * 执行函数，参数为p，忽略返回结果
     *
     * @param consumer
     * @param <P1>     参数1类型
     * @param <P2>     参数2类型
     * @return
     */
    public <P1, P2> Fluent<T> $(Consumer3<T, P1, P2> consumer, P1 p1, P2 p2) {
        if (target != null) {
            consumer.accept(target, p1, p2);
        }
        return this;
    }


    /**
     * 执行函数，忽略返回结果
     *
     * @param consumer
     * @param p1
     * @param p2
     * @param p3
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @return
     */
    public <P1, P2, P3> Fluent<T> $(Consumer4<T, P1, P2, P3> consumer, P1 p1, P2 p2, P3 p3) {
        if (target != null) {
            consumer.accept(target, p1, p2, p3);
        }
        return this;
    }

    /**
     * 执行函数，忽略返回结果
     *
     * @param consumer
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <P4>
     * @return
     */
    public <P1, P2, P3, P4> Fluent<T> $(Consumer5<T, P1, P2, P3, P4> consumer, P1 p1, P2 p2, P3 p3, P4 p4) {
        if (target != null) {
            consumer.accept(target, p1, p2, p3, p4);
        }
        return this;
    }

    /**
     * 执行函数，忽略返回结果
     *
     * @param consumer
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param p5
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <P4>
     * @param <P5>
     * @return
     */
    public <P1, P2, P3, P4, P5> Fluent<T> $(Consumer6<T, P1, P2, P3, P4, P5> consumer, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
        if (target != null) {
            consumer.accept(target, p1, p2, p3, p4, p5);
        }
        return this;
    }

    /**
     * 执行函数，忽略返回结果
     *
     * @param consumer
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param p5
     * @param p6
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <P4>
     * @param <P5>
     * @param <P6>
     * @return
     */
    public <P1, P2, P3, P4, P5, P6> Fluent<T> $(Consumer7<T, P1, P2, P3, P4, P5, P6> consumer, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
        if (target != null) {
            consumer.accept(target, p1, p2, p3, p4, p5, p6);
        }
        return this;
    }


    /**
     * 执行函数，忽略返回结果
     *
     * @param consumer
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param p5
     * @param p6
     * @param p7
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <P4>
     * @param <P5>
     * @param <P6>
     * @param <P7>
     * @return
     */
    public <P1, P2, P3, P4, P5, P6, P7> Fluent<T> $(Consumer8<T, P1, P2, P3, P4, P5, P6, P7> consumer, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
        if (target != null) {
            consumer.accept(target, p1, p2, p3, p4, p5, p6, p7);
        }
        return this;
    }

    /**
     * 执行函数，忽略返回结果
     *
     * @param consumer
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param p5
     * @param p6
     * @param p7
     * @param p8
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <P4>
     * @param <P5>
     * @param <P6>
     * @param <P7>
     * @param <P8>
     * @return
     */
    public <P1, P2, P3, P4, P5, P6, P7, P8> Fluent<T> $(Consumer9<T, P1, P2, P3, P4, P5, P6, P7, P8> consumer, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
        if (target != null) {
            consumer.accept(target, p1, p2, p3, p4, p5, p6, p7, p8);
        }
        return this;
    }


    public <P1> Fluent2<T, P1> method(BiConsumer<T, P1> function) {
        return new Fluent2<>(target, function);
    }

    public <P1, P2> Fluent3<T, P1, P2> method(Consumer3<T, P1, P2> function) {
        return new Fluent3<>(target, function);
    }

    public <P1, P2, P3> Fluent4<T, P1, P2, P3> method(Consumer4<T, P1, P2, P3> function) {
        return new Fluent4<>(target, function);
    }


    public <P1, P2, P3, P4> Fluent5<T, P1, P2, P3, P4> method(Consumer5<T, P1, P2, P3, P4> function) {
        return new Fluent5<>(target, function);
    }

    public <P1, P2, P3, P4, P5> Fluent6<T, P1, P2, P3, P4, P5> method(Consumer6<T, P1, P2, P3, P4,
            P5> function) {
        return new Fluent6<>(target, function);
    }

    public <P1, P2, P3, P4, P5, P6> Fluent7<T, P1, P2, P3, P4, P5, P6> method(Consumer7<T, P1, P2, P3, P4,
            P5, P6> function) {
        return new Fluent7<>(target, function);
    }

    public <P1, P2, P3, P4, P5, P6, P7> Fluent8<T, P1, P2, P3, P4, P5, P6, P7> method(Consumer8<T, P1, P2, P3, P4,
            P5, P6, P7> function) {
        return new Fluent8<>(target, function);
    }

    public <P1, P2, P3, P4, P5, P6, P7, P8> Fluent9<T, P1, P2, P3, P4, P5, P6, P7, P8> method(Consumer9<T, P1, P2, P3, P4, P5, P6, P7, P8> function) {
        return new Fluent9<>(target, function);
    }


    public static class Fluent2<T, P1> extends Fluent<T> {

        private BiConsumer<T, P1> f;

        public Fluent2(T target, BiConsumer<T, P1> f) {
            super(target);
            this.f = f;
        }

        public Fluent2<T, P1> invoke(P1 p1) {
            f.accept(target, p1);
            return this;
        }


        public Fluent2<T, P1> invoke(Param<P1> u) {
            u.get().ifPresent(p -> f.accept(target, p));
            return this;
        }

    }


    public static class Fluent3<T, P1, P2> extends Fluent<T> {
        private Consumer3<T, P1, P2> f;

        public Fluent3(T target, Consumer3<T, P1, P2> f) {
            super(target);
            this.f = f;
        }

        public Fluent3<T, P1, P2> invoke(P1 p1, P2 p2) {
            f.accept(target, p1, p2);
            return this;
        }
    }

    public static class Fluent4<T, P1, P2, P3> extends Fluent<T> {
        private Consumer4<T, P1, P2, P3> f;

        public Fluent4(T target, Consumer4<T, P1, P2, P3> f) {
            super(target);
            this.f = f;
        }

        public Fluent4<T, P1, P2, P3> invoke(P1 p1, P2 p2, P3 p3) {
            f.accept(target, p1, p2, p3);
            return this;
        }
    }

    public static class Fluent5<T, P1, P2, P3, P4> extends Fluent<T> {
        private Consumer5<T, P1, P2, P3, P4> f;

        public Fluent5(T target, Consumer5<T, P1, P2, P3, P4> f) {
            super(target);
            this.f = f;
        }

        public Fluent5<T, P1, P2, P3, P4> invoke(P1 p1, P2 p2, P3 p3, P4 p4) {
            f.accept(target, p1, p2, p3, p4);
            return this;
        }
    }

    public static class Fluent6<T, P1, P2, P3, P4, P5> extends Fluent<T> {
        private Consumer6<T, P1, P2, P3, P4, P5> f;

        public Fluent6(T target, Consumer6<T, P1, P2, P3, P4, P5> f) {
            super(target);
            this.f = f;
        }

        public Fluent6<T, P1, P2, P3, P4, P5> invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
            f.accept(target, p1, p2, p3, p4, p5);
            return this;
        }
    }

    public static class Fluent7<T, P1, P2, P3, P4, P5, P6> extends Fluent<T> {
        private Consumer7<T, P1, P2, P3, P4, P5, P6> f;

        public Fluent7(T target, Consumer7<T, P1, P2, P3, P4, P5, P6> f) {
            super(target);
            this.f = f;
        }

        public Fluent7<T, P1, P2, P3, P4, P5, P6> invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
            f.accept(target, p1, p2, p3, p4, p5, p6);
            return this;
        }
    }

    public static class Fluent8<T, P1, P2, P3, P4, P5, P6, P7> extends Fluent<T> {
        private Consumer8<T, P1, P2, P3, P4, P5, P6, P7> f;

        public Fluent8(T target, Consumer8<T, P1, P2, P3, P4, P5, P6, P7> f) {
            super(target);
            this.f = f;
        }

        public Fluent8<T, P1, P2, P3, P4, P5, P6, P7> invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
            f.accept(target, p1, p2, p3, p4, p5, p6, p7);
            return this;
        }
    }

    public static class Fluent9<T, P1, P2, P3, P4, P5, P6, P7, P8> extends Fluent<T> {
        private Consumer9<T, P1, P2, P3, P4, P5, P6, P7, P8> f;

        public Fluent9(T target, Consumer9<T, P1, P2, P3, P4, P5, P6, P7, P8> f) {
            super(target);
            this.f = f;
        }

        public Fluent9<T, P1, P2, P3, P4, P5, P6, P7, P8> invoke(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
            f.accept(target, p1, p2, p3, p4, p5, p6, p7, p8);
            return this;
        }
    }


}
