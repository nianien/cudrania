package com.nianien.cudrania.core.pipeline;

import com.nianien.cudrania.core.functions.Fn;

/**
 * 接收一个初始参数并返回一个结果的流水线
 * <p>
 * 通过 {@link Pipelines#of(Class)} 或 {@link Pipelines#of(Named)} 创建 DSL
 * 每次 {@code and} 都会追加一个执行步骤，步骤输出默认成为下一个步骤的输入
 * <pre>{@code
 * Pipeline<Integer, String> pipeline = Pipelines.of(Integer.class)
 *         .and(value -> value * 2)
 *         .and(value -> "result:" + value)
 *         .end();
 *
 * String result = pipeline.eval(21);
 * }</pre>
 * 如需在后续步骤复用较早的结果，可通过 {@code as} 保存结果并通过 {@code with} 选择
 *
 * @param <IN>  流水线初始参数类型
 * @param <OUT> 流水线最终结果类型
 */
public interface Pipeline<IN, OUT> {

    /**
     * 使用给定初始参数执行完整流水线
     * <p>
     * 每次调用都创建独立执行上下文
     * 未被 {@link StepOut#failOver(Fn.Function)} 处理的异常会直接向调用方传播
     *
     * @param input 流水线初始参数
     * @return 最后一个步骤的输出
     */
    OUT eval(IN input);


    /**
     * 等待添加单参处理函数的 DSL 状态
     * <p>
     * {@code IN} 表示下一个 {@code and} 接收的参数类型
     * 该参数默认为上一执行步骤的输出，也可以由 {@code with} 显式选择
     *
     * @param <S>  流水线初始参数类型
     * @param <IN> 当前待处理参数类型
     */
    interface StepIn<S, IN> extends StepOut<S, IN> {

        /**
         * 追加单参执行步骤
         *
         * @param ability 接收当前参数并产生新结果的函数
         * @param <R>     新步骤的结果类型
         * @return 以新结果作为默认输入的 DSL 状态
         */
        <R> StepIn<S, R> and(Fn.Function<? super IN, ? extends R> ability);
    }


    /**
     * 等待添加双参处理函数的 DSL 状态
     * <p>
     * 两个参数通常由 {@link StepInit#with(Named, Named)} 或
     * {@link StepInit#with(String, String)} 选择
     *
     * @param <S>   流水线初始参数类型
     * @param <IN1> 当前待处理参数1类型
     * @param <IN2> 当前待处理参数2类型
     */
    interface StepIn2<S, IN1, IN2> extends StepInit<S> {

        /**
         * 追加双参执行步骤
         *
         * @param ability 接收两个已选参数并产生新结果的函数
         * @param <R>     新步骤的结果类型
         * @return 以新结果作为默认输入的 DSL 状态
         */
        <R> StepIn<S, R> and(Fn.BiFunction<? super IN1, ? super IN2, ? extends R> ability);
    }

    /**
     * 等待添加三参处理函数的 DSL 状态
     * <p>
     * 三个参数通常由 {@link StepInit#with(Named, Named, Named)} 或
     * {@link StepInit#with(String, String, String)} 选择
     *
     * @param <S>   流水线初始参数类型
     * @param <IN1> 当前待处理参数1类型
     * @param <IN2> 当前待处理参数2类型
     * @param <IN3> 当前待处理参数3类型
     */
    interface StepIn3<S, IN1, IN2, IN3> extends StepInit<S> {

        /**
         * 追加三参执行步骤
         *
         * @param ability 接收三个已选参数并产生新结果的函数
         * @param <R>     新步骤的结果类型
         * @return 以新结果作为默认输入的 DSL 状态
         */
        <R> StepIn<S, R> and(Fn.TriFunction<? super IN1, ? super IN2, ? super IN3, ? extends R> ability);
    }


    /**
     * 已产生当前步骤结果的 DSL 状态
     * <p>
     * 可结束构建、命名当前结果、设置当前步骤的异常降级函数或准备下一步骤
     *
     * @param <S>  流水线初始参数类型
     * @param <IN> 当前步骤结果类型
     */
    interface StepOut<S, IN> extends StepInit<S> {

        /**
         * 结束 DSL 构建并生成可执行流水线
         *
         * @return 以当前步骤结果作为最终输出的流水线
         */
        Pipeline<S, IN> end();

        /**
         * 将当前步骤结果保存到执行上下文
         * <p>
         * 后续步骤可把同一方法引用传给 {@code with} 重新选择该结果
         *
         * @param name 结果名称及类型声明
         * @param <T>  声明名称的宿主类型
         * @return 保持当前结果类型的 DSL 状态
         */
        <T> StepIn<S, IN> as(Named<T, IN> name);

        /**
         * 为当前步骤设置异常降级函数
         * <p>
         * 当当前步骤抛出 {@link Exception} 时调用处理函数
         * 处理函数返回值会替代失败步骤的结果并继续执行后续步骤
         *
         * @param handler 接收步骤异常并返回替代结果的函数
         * @return 保持当前结果类型的 DSL 状态
         */
        StepIn<S, IN> failOver(Fn.Function<? super Exception, IN> handler);
    }

    /**
     * 可为下一执行步骤选择输入参数的 DSL 状态
     * <p>
     * {@code with} 只影响紧随其后通过 {@code and} 创建的步骤
     * 使用 {@link Named} 可获得编译期类型推断
     * 使用字符串名称时通常需要显式指定方法类型参数
     *
     * @param <S> 流水线初始参数类型
     */
    interface StepInit<S> {

        /**
         * 通过命名方法引用选择下一步骤的一个参数
         *
         * @param name 参数名称及类型声明
         * @param <T>  声明名称的宿主类型
         * @param <IN> 参数类型
         * @return 等待添加单参函数的 DSL 状态
         */
        <T, IN> StepIn<S, IN> with(Named<T, IN> name);

        /**
         * 通过上下文键选择下一步骤的一个参数
         *
         * @param name 上下文键
         * @param <IN> 由调用方声明的参数类型
         * @return 等待添加单参函数的 DSL 状态
         */
        <IN> StepIn<S, IN> with(String name);

        /**
         * 通过命名方法引用选择下一步骤的两个参数
         *
         * @param name1 参数1名称及类型声明
         * @param name2 参数2名称及类型声明
         * @param <T1>  声明参数1名称的宿主类型
         * @param <T2>  声明参数2名称的宿主类型
         * @param <IN1> 参数1类型
         * @param <IN2> 参数2类型
         * @return 等待添加双参函数的 DSL 状态
         */
        <T1, T2, IN1, IN2> StepIn2<S, IN1, IN2> with(Named<T1, IN1> name1, Named<T2, IN2> name2);

        /**
         * 通过上下文键选择下一步骤的两个参数
         *
         * @param name1 参数1上下文键
         * @param name2 参数2上下文键
         * @param <IN1> 由调用方声明的参数1类型
         * @param <IN2> 由调用方声明的参数2类型
         * @return 等待添加双参函数的 DSL 状态
         */
        <IN1, IN2> StepIn2<S, IN1, IN2> with(String name1, String name2);

        /**
         * 通过命名方法引用选择下一步骤的三个参数
         *
         * @param name1 参数1名称及类型声明
         * @param name2 参数2名称及类型声明
         * @param name3 参数3名称及类型声明
         * @param <T1>  声明参数1名称的宿主类型
         * @param <T2>  声明参数2名称的宿主类型
         * @param <T3>  声明参数3名称的宿主类型
         * @param <IN1> 参数1类型
         * @param <IN2> 参数2类型
         * @param <IN3> 参数3类型
         * @return 等待添加三参函数的 DSL 状态
         */
        <T1, T2, T3, IN1, IN2, IN3> StepIn3<S, IN1, IN2, IN3> with(Named<T1, IN1> name1, Named<T2, IN2> name2, Named<T3, IN3> name3);

        /**
         * 通过上下文键选择下一步骤的三个参数
         *
         * @param name1 参数1上下文键
         * @param name2 参数2上下文键
         * @param name3 参数3上下文键
         * @param <IN1> 由调用方声明的参数1类型
         * @param <IN2> 由调用方声明的参数2类型
         * @param <IN3> 由调用方声明的参数3类型
         * @return 等待添加三参函数的 DSL 状态
         */
        <IN1, IN2, IN3> StepIn3<S, IN1, IN2, IN3> with(String name1, String name2, String name3);
    }
}
