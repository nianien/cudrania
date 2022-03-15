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
     * 执行无参方法，持有返回结果
     */
    public <R> Fluent<R> apply(Function<T, R> function) {
        return new FFunction<>(target, function).apply();
    }

    /**
     * 执行单参方法，持有返回结果
     */
    public <R, P> Fluent<R> apply(BiFunction<T, P, R> function, P p) {
        return new FBiFunction<>(target, function).apply(p);
    }

    /**
     * 执行双参方法，持有返回结果
     */
    public <P1, P2, R> Fluent<R> apply(TriFunction<T, P1, P2, R> function, P1 p1, P2 p2) {
        return new FTriFunction<>(target, function).apply(p1, p2);
    }

    /**
     * 执行三参方法，持有返回结果
     */
    public <P1, P2, P3, R> Fluent<R> apply(QuaFunction<T, P1, P2, P3, R> function, P1 p1, P2 p2, P3 p3) {
        return new FQuaFunction<>(target, function).apply(p1, p2, p3);
    }

    /**
     * 执行四参方法，持有返回结果
     */
    public <P1, P2, P3, P4, R> Fluent<R> apply(QuiFunction<T, P1, P2, P3, P4, R> function, P1 p1, P2 p2, P3 p3, P4 p4) {
        return new FQuiFunction<>(target, function).apply(p1, p2, p3, p4);
    }


    /**
     * 执行无参方法，忽略返回结果
     */
    public Fluent<T> accept(Consumer<T> consumer) {
        return new FConsumer<>(target, consumer).accept();
    }

    /**
     * 执行单参方法，忽略返回结果
     */
    public <P> Fluent<T> accept(BiConsumer<T, P> consumer, P p) {
        return new FBiConsumer<>(target, consumer).accept(p);
    }

    /**
     * 如参数{@link Param#get()}返回结果有值,则执行函数
     *
     * @param <P>      参数类型&函数第二个参数类型
     * @param consumer 函数表达式
     * @param param    条件参数
     * @return
     */
    public <P> Fluent<T> acceptIf(BiConsumer<T, P> consumer, Param<P> param) {
        if (target != null) {
            param.get().ifPresent(p -> consumer.accept(target, p));
        }
        return this;
    }

    /**
     * 执行双参方法，忽略返回结果
     *
     * @return
     */
    public <P1, P2> Fluent<T> accept(TriConsumer<T, P1, P2> consumer, P1 p1, P2 p2) {
        return new FTriConsumer<>(target, consumer).accept(p1, p2);
    }


    /**
     * 执行三参方法，忽略返回结果
     */
    public <P1, P2, P3> Fluent<T> accept(QuaConsumer<T, P1, P2, P3> consumer, P1 p1, P2 p2, P3 p3) {
        return new FQuaConsumer<>(target, consumer).accept(p1, p2, p3);
    }

    /**
     * 执行四参方法，忽略返回结果
     */
    public <P1, P2, P3, P4> Fluent<T> accept(QuiConsumer<T, P1, P2, P3, P4> consumer, P1 p1, P2 p2, P3 p3, P4 p4) {
        return new FQuiConsumer<>(target, consumer).accept(p1, p2, p3, p4);
    }


    /**
     * 绑定无参方法,忽略返回结果
     *
     * @param consumer
     * @return
     */
    public FConsumer<T> consumer(Consumer<T> consumer) {
        return new FConsumer<>(target, consumer);
    }

    /**
     * 绑定单参方法,忽略返回结果
     */
    public <P1> FBiConsumer<T, P1> consumer(BiConsumer<T, P1> consumer) {
        return new FBiConsumer<>(target, consumer);
    }

    /**
     * 绑定双参方法,忽略返回结果
     */
    public <P1, P2> FTriConsumer<T, P1, P2> consumer(TriConsumer<T, P1, P2> consumer) {
        return new FTriConsumer<>(target, consumer);
    }

    /**
     * 绑定三参方法,忽略返回结果
     */
    public <P1, P2, P3> FQuaConsumer<T, P1, P2, P3> consumer(QuaConsumer<T, P1, P2, P3> consumer) {
        return new FQuaConsumer<>(target, consumer);
    }


    /**
     * 绑定四参方法,忽略返回结果
     */
    public <P1, P2, P3, P4> FQuiConsumer<T, P1, P2, P3, P4> consumer(QuiConsumer<T, P1, P2, P3, P4> consumer) {
        return new FQuiConsumer<>(target, consumer);
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
     * {@link Consumer}柯里化(Currying)为无参函数
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
            if (target != null) {
                f.accept(target);
            }
            return this;
        }
    }


    /**
     * {@link BiConsumer}柯里化(Currying)为单参函数
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
            if (target != null) {
                f.accept(target, p1);
            }
            return this;
        }

        /**
         * 如参数{@link Param#get()}返回结果有值,则执行函数
         *
         * @param param 条件参数
         * @return
         */
        public FBiConsumer<T, P1> acceptIf(Param<P1> param) {
            if (target != null) {
                param.get().ifPresent(p -> f.accept(target, p));
            }
            return this;
        }
    }


    /**
     * {@link TriConsumer}柯里化(Currying)为双参函数
     */
    public static class FTriConsumer<T, P1, P2> extends Fluent<T> {
        private TriConsumer<T, P1, P2> f;

        public FTriConsumer(T target, TriConsumer<T, P1, P2> f) {
            super(target);
            this.f = f;
        }

        public FTriConsumer<T, P1, P2> accept(P1 p1, P2 p2) {
            if (target != null) {
                f.accept(target, p1, p2);
            }
            return this;
        }
    }

    /**
     * {@link QuaConsumer}柯里化(Currying)为三参函数
     */
    public static class FQuaConsumer<T, P1, P2, P3> extends Fluent<T> {
        private QuaConsumer<T, P1, P2, P3> f;

        public FQuaConsumer(T target, QuaConsumer<T, P1, P2, P3> f) {
            super(target);
            this.f = f;
        }

        public FQuaConsumer<T, P1, P2, P3> accept(P1 p1, P2 p2, P3 p3) {
            if (target != null) {
                f.accept(target, p1, p2, p3);
            }
            return this;
        }
    }

    /**
     * {@link QuiConsumer}柯里化(Currying)为四参函数
     */
    public static class FQuiConsumer<T, P1, P2, P3, P4> extends Fluent<T> {
        private QuiConsumer<T, P1, P2, P3, P4> f;

        public FQuiConsumer(T target, QuiConsumer<T, P1, P2, P3, P4> f) {
            super(target);
            this.f = f;
        }

        public FQuiConsumer<T, P1, P2, P3, P4> accept(P1 p1, P2 p2, P3 p3, P4 p4) {
            if (target != null) {
                f.accept(target, p1, p2, p3, p4);
            }
            return this;
        }
    }


    /**
     * {@link Function}柯里化(Currying)为无参函数
     */
    public static class FFunction<T, R> extends Fluent<T> {
        private Function<T, R> f;

        public FFunction(T target, Function<T, R> f) {
            super(target);
            this.f = f;
        }

        public Fluent<R> apply() {
            Fluent<R> fluent = (Fluent<R>) this;
            if (target != null) {
                fluent.target = f.apply(target);
            }
            return fluent;
        }

    }


    /**
     * {@link BiFunction}柯里化(Currying)为单参函数
     */
    public static class FBiFunction<T, P1, R> extends Fluent<T> {
        private BiFunction<T, P1, R> f;

        public FBiFunction(T target, BiFunction<T, P1, R> f) {
            super(target);
            this.f = f;
        }

        public Fluent<R> apply(P1 p1) {
            Fluent<R> fluent = (Fluent<R>) this;
            if (target != null) {
                fluent.target = f.apply(target, p1);
            }
            return fluent;
        }

    }

    /**
     * {@link TriFunction}柯里化(Currying)为双参函数
     */
    public static class FTriFunction<T, P1, P2, R> extends Fluent<T> {
        private TriFunction<T, P1, P2, R> f;

        public FTriFunction(T target, TriFunction<T, P1, P2, R> f) {
            super(target);
            this.f = f;
        }

        public Fluent<R> apply(P1 p1, P2 p2) {
            Fluent<R> fluent = (Fluent<R>) this;
            if (target != null) {
                fluent.target = f.apply(target, p1, p2);
            }
            return fluent;
        }

    }

    /**
     * {@link QuaFunction}柯里化(Currying)为三参函数
     */
    public static class FQuaFunction<T, P1, P2, P3, R> extends Fluent<T> {
        private QuaFunction<T, P1, P2, P3, R> f;

        public FQuaFunction(T target, QuaFunction<T, P1, P2, P3, R> f) {
            super(target);
            this.f = f;
        }

        public Fluent<R> apply(P1 p1, P2 p2, P3 p3) {
            Fluent<R> fluent = (Fluent<R>) this;
            if (target != null) {
                fluent.target = f.apply(target, p1, p2, p3);
            }
            return fluent;
        }
    }

    /**
     * {@link QuiFunction}柯里化(Currying)为四参函数
     */
    public static class FQuiFunction<T, P1, P2, P3, P4, R> extends Fluent<T> {
        private QuiFunction<T, P1, P2, P3, P4, R> f;

        public FQuiFunction(T target, QuiFunction<T, P1, P2, P3, P4, R> f) {
            super(target);
            this.f = f;
        }

        public Fluent<R> apply(P1 p1, P2 p2, P3 p3, P4 p4) {
            Fluent<R> fluent = (Fluent<R>) this;
            if (target != null) {
                fluent.target = f.apply(target, p1, p2, p3, p4);
            }
            return fluent;
        }
    }

}
