package com.cudrania.core.functions;


import com.cudrania.core.functions.Fn.*;


/**
 * 构建Fluent API的Function工具<br/>
 * <p>
 * <ul>
 * <li>
 * 执行函数，并持有函数执行结果<br/>
 * {@link Fluent#apply(Function)}
 * </li>
 * <li>
 * 执行函数，并忽略函数执行结果（持有对象不变）<br/>
 * {@link Fluent#accept(Consumer)}
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
    public <P> Fluent<T> apply(Param<P> param, BiFunction<T, P, T> function) {
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
    public <P> Fluent<T> accept(Param<P> param, BiConsumer<T, P> consumer) {
        param.get().ifPresent(p -> consumer.accept(target, p));
        return this;
    }

    /**
     * 执行函数，持有返回结果
     */
    public <R> Fluent<R> apply(Function<T, R> function) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     */
    public <R, P> Fluent<R> apply(BiFunction<T, P, R> function, P p) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     */
    public <P1, P2, R> Fluent<R> apply(TriFunction<T, P1, P2, R> function, P1 p1, P2 p2) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     */
    public <P1, P2, P3, R> Fluent<R> apply(QuaFunction<T, P1, P2, P3, R> function, P1 p1, P2 p2, P3 p3) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     */
    public <P1, P2, P3, P4, R> Fluent<R> apply(QuiFunction<T, P1, P2, P3, P4, R> function, P1 p1, P2 p2, P3 p3, P4 p4) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3, p4);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     */
    public <P1, P2, P3, P4, P5, R> Fluent<R> apply(HexFunction<T, P1, P2, P3, P4, P5, R> function, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3, p4, p5);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     */
    public <P1, P2, P3, P4, P5, P6, R> Fluent<R> apply(HepFunction<T, P1, P2, P3, P4, P5, P6, R> function, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3, p4, p5, p6);
        }
        return f;
    }

    /**
     * 执行函数，持有返回结果
     */
    public <P1, P2, P3, P4, P5, P6, P7, R> Fluent<R> apply(OctFunction<T, P1, P2, P3, P4, P5, P6, P7, R> function, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3, p4, p5, p6, p7);
        }
        return f;
    }


    /**
     * 执行函数，持有返回结果
     *
     * @return
     */
    public <P1, P2, P3, P4, P5, P6, P7, P8, R> Fluent<R> apply(NonFunction<T, P1, P2, P3, P4, P5, P6, P7, P8, R> function, P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8) {
        Fluent<R> f = (Fluent<R>) this;
        if (target != null) {
            f.target = function.apply(target, p1, p2, p3, p4, p5, p6, p7, p8);
        }
        return f;
    }


    /**
     * 执行函数，忽略返回结果
     */
    public Fluent<T> accept(Consumer<T> consumer) {
        if (target != null) {
            consumer.accept(target);
        }
        return this;
    }

    /**
     * 执行函数，忽略返回结果
     */
    public <P> Fluent<T> accept(BiConsumer<T, P> consumer, P p) {
        if (target != null) {
            consumer.accept(target, p);
        }
        return this;
    }


    /**
     * 执行函数，忽略返回结果
     *
     * @return
     */
    public <P1, P2> Fluent<T> accept(TriConsumer<T, P1, P2> consumer, P1 p1, P2 p2) {
        if (target != null) {
            consumer.accept(target, p1, p2);
        }
        return this;
    }


    /**
     * 执行函数，忽略返回结果
     */
    public <P1, P2, P3> Fluent<T> accept(QuaConsumer<T, P1, P2, P3> consumer, P1 p1, P2 p2, P3 p3) {
        if (target != null) {
            consumer.accept(target, p1, p2, p3);
        }
        return this;
    }

    /**
     * 绑定无参方法,忽略返回结果
     *
     * @param function
     * @return
     */
    public FConsumer<T> consumer(Consumer<T> function) {
        return new FConsumer<>(target, function);
    }

    /**
     * 绑定单参方法,忽略返回结果
     */
    public <P1> FBiConsumer<T, P1> consumer(BiConsumer<T, P1> function) {
        return new FBiConsumer<>(target, function);
    }

    /**
     * 绑定双参方法,忽略返回结果
     */
    public <P1, P2> FTriConsumer<T, P1, P2> consumer(TriConsumer<T, P1, P2> function) {
        return new FTriConsumer<>(target, function);
    }

    /**
     * 绑定三参方法,忽略返回结果
     */
    public <P1, P2, P3> FQuaConsumer<T, P1, P2, P3> consumer(QuaConsumer<T, P1, P2, P3> function) {
        return new FQuaConsumer<>(target, function);
    }


    /**
     * 绑定四参方法,忽略返回结果
     */
    public <P1, P2, P3, P4> FQuiConsumer<T, P1, P2, P3, P4> consumer(QuiConsumer<T, P1, P2, P3, P4> function) {
        return new FQuiConsumer<>(target, function);
    }


    /**
     * 绑定无参方法,持有返回结果
     */
    public <R> FFunction<T, R> function(Function<T, R> function) {
        return new FFunction<>(target, function);
    }


    /**
     * 绑定单参方法,持有返回结果
     */
    public <P1, R> FBiFunction<T, P1, R> function(BiFunction<T, P1, R> function) {
        return new FBiFunction<>(target, function);
    }


    /**
     * 绑定双参方法,持有返回结果
     */
    public <P1, P2, R> FTriFunction<T, P1, P2, R> function(TriFunction<T, P1, P2, R> function) {
        return new FTriFunction<>(target, function);
    }


    /**
     * 绑定三参方法,持有返回结果
     */
    public <P1, P2, P3, R> FQuaFunction<T, P1, P2, P3, R> function(QuaFunction<T, P1, P2, P3, R> function) {
        return new FQuaFunction<>(target, function);
    }


    /**
     * 绑定四参方法,持有返回结果
     *
     * @param function
     * @return
     */
    public <P1, P2, P3, P4, R> FQuiFunction<T, P1, P2, P3, P4, R> function(QuiFunction<T, P1, P2, P3, P4, R> function) {
        return new FQuiFunction<>(target, function);
    }

    /**
     * 柯里化(Currying){@link Consumer}
     *
     * @param <T>
     */
    public static class FConsumer<T> extends Fluent<T> {

        private Consumer<T> f;

        public FConsumer(T target, Consumer<T> f) {
            super(target);
            this.f = f;
        }

        public FConsumer<T> accept() {
            f.accept(target);
            return this;
        }
    }


    /**
     * 柯里化(Currying){@link BiConsumer}
     *
     * @param <T>
     * @param <P1>
     */
    public static class FBiConsumer<T, P1> extends Fluent<T> {

        private BiConsumer<T, P1> f;

        public FBiConsumer(T target, BiConsumer<T, P1> f) {
            super(target);
            this.f = f;
        }

        public FBiConsumer<T, P1> accept(P1 p1) {
            f.accept(target, p1);
            return this;
        }

        /**
         * 如参数{@link Param#get()}返回结果有值,则执行函数
         *
         * @param param 条件参数
         * @return
         */
        public FBiConsumer<T, P1> accept(Param<P1> param) {
            param.get().ifPresent(p -> f.accept(target, p));
            return this;
        }
    }


    /**
     * 柯里化(Currying){@link TriConsumer}
     */
    public static class FTriConsumer<T, P1, P2> extends Fluent<T> {
        private TriConsumer<T, P1, P2> f;

        public FTriConsumer(T target, TriConsumer<T, P1, P2> f) {
            super(target);
            this.f = f;
        }

        public FTriConsumer<T, P1, P2> accept(P1 p1, P2 p2) {
            f.accept(target, p1, p2);
            return this;
        }
    }

    /**
     * 柯里化(Currying){@link QuaConsumer}
     */
    public static class FQuaConsumer<T, P1, P2, P3> extends Fluent<T> {
        private QuaConsumer<T, P1, P2, P3> f;

        public FQuaConsumer(T target, QuaConsumer<T, P1, P2, P3> f) {
            super(target);
            this.f = f;
        }

        public FQuaConsumer<T, P1, P2, P3> accept(P1 p1, P2 p2, P3 p3) {
            f.accept(target, p1, p2, p3);
            return this;
        }
    }

    /**
     * 柯里化(Currying){@link QuaConsumer}
     */
    public static class FQuiConsumer<T, P1, P2, P3, P4> extends Fluent<T> {
        private QuiConsumer<T, P1, P2, P3, P4> f;

        public FQuiConsumer(T target, QuiConsumer<T, P1, P2, P3, P4> f) {
            super(target);
            this.f = f;
        }

        public FQuiConsumer<T, P1, P2, P3, P4> accept(P1 p1, P2 p2, P3 p3, P4 p4) {
            f.accept(target, p1, p2, p3, p4);
            return this;
        }
    }


    /**
     * 柯里化(Currying){@link Function}
     */
    public static class FFunction<T, R> extends Fluent<T> {
        private Function<T, R> f;

        public FFunction(T target, Function<T, R> f) {
            super(target);
            this.f = f;
        }

        public Fluent<R> apply() {
            Fluent<R> fluent = (Fluent<R>) this;
            fluent.target = f.apply(target);
            return fluent;
        }

    }


    /**
     * 柯里化(Currying){@link BiFunction}
     */
    public static class FBiFunction<T, P1, R> extends Fluent<T> {
        private BiFunction<T, P1, R> f;

        public FBiFunction(T target, BiFunction<T, P1, R> f) {
            super(target);
            this.f = f;
        }

        public Fluent<R> apply(P1 p1) {
            Fluent<R> fluent = (Fluent<R>) this;
            fluent.target = f.apply(target, p1);
            return fluent;
        }

    }

    /**
     * 柯里化(Currying){@link TriFunction}
     */
    public static class FTriFunction<T, P1, P2, R> extends Fluent<T> {
        private TriFunction<T, P1, P2, R> f;

        public FTriFunction(T target, TriFunction<T, P1, P2, R> f) {
            super(target);
            this.f = f;
        }

        public Fluent<R> apply(P1 p1, P2 p2) {
            Fluent<R> fluent = (Fluent<R>) this;
            fluent.target = f.apply(target, p1, p2);
            return fluent;
        }

    }

    /**
     * 柯里化(Currying){@link QuaFunction}
     */
    public static class FQuaFunction<T, P1, P2, P3, R> extends Fluent<T> {
        private QuaFunction<T, P1, P2, P3, R> f;

        public FQuaFunction(T target, QuaFunction<T, P1, P2, P3, R> f) {
            super(target);
            this.f = f;
        }

        public Fluent<R> apply(P1 p1, P2 p2, P3 p3) {
            Fluent<R> fluent = (Fluent<R>) this;
            fluent.target = f.apply(target, p1, p2, p3);
            return fluent;
        }
    }

    /**
     * 柯里化(Currying){@link QuiFunction}
     */
    public static class FQuiFunction<T, P1, P2, P3, P4, R> extends Fluent<T> {
        private QuiFunction<T, P1, P2, P3, P4, R> f;

        public FQuiFunction(T target, QuiFunction<T, P1, P2, P3, P4, R> f) {
            super(target);
            this.f = f;
        }

        public Fluent<R> apply(P1 p1, P2 p2, P3 p3, P4 p4) {
            Fluent<R> fluent = (Fluent<R>) this;
            fluent.target = f.apply(target, p1, p2, p3, p4);
            return fluent;
        }
    }

}
