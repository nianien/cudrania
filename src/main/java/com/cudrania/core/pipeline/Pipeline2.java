package com.cudrania.core.pipeline;

import com.cudrania.core.functions.Fn;

/**
 * 接收两个初始参数并返回一个结果的流水线
 * <p>
 * 创建后首先处于双参步骤状态，因此第一个 {@code and} 接收两个初始参数
 * 第一个步骤执行完成后进入单参状态，后续 {@code and} 默认接收上一结果
 * <pre>{@code
 * Pipeline2<Integer, Integer, String> pipeline = Pipelines
 *         .of(Integer.class, Integer.class)
 *         .and(Integer::sum)
 *         .and(sum -> "sum:" + sum)
 *         .end();
 *
 * String result = pipeline.eval(20, 22);
 * }</pre>
 *
 * @param <IN1> 初始参数1类型
 * @param <IN2> 初始参数2类型
 * @param <OUT> 最终结果类型
 * @see Pipelines#of(Class, Class)
 * @see Pipelines#of(Named, Named)
 */
public interface Pipeline2<IN1, IN2, OUT> {

    /**
     * 使用两个给定初始参数执行完整流水线
     *
     * @param input1 初始参数1
     * @param input2 初始参数2
     * @return 最后一个步骤的输出
     */
    OUT eval(IN1 input1, IN2 input2);


    /**
     * 等待添加单参处理函数的 DSL 状态
     *
     * @param <S1> 流水线初始参数1类型
     * @param <S2> 流水线初始参数2类型
     * @param <IN> 当前待处理参数类型
     */
    interface StepIn<S1, S2, IN> extends StepOut<S1, S2, IN> {

        /**
         * 追加单参执行步骤
         *
         * @param ability 接收当前参数并产生新结果的函数
         * @param <R>     新步骤的结果类型
         * @return 以新结果作为默认输入的 DSL 状态
         */
        <R> StepIn<S1, S2, R> and(Fn.Function<? super IN, ? extends R> ability);
    }

    /**
     * 等待添加双参处理函数的 DSL 状态
     * <p>
     * 初次构建时两个参数就是流水线的两个初始参数
     * 构建过程中也可由 {@code with} 重新选择两个已命名值
     *
     * @param <S1>  流水线初始参数1类型
     * @param <S2>  流水线初始参数2类型
     * @param <IN1> 当前待处理参数1类型
     * @param <IN2> 当前待处理参数2类型
     */
    interface StepIn2<S1, S2, IN1, IN2> extends StepInit<S1, S2> {

        /**
         * 追加双参执行步骤
         *
         * @param ability 接收两个当前参数并产生新结果的函数
         * @param <R>     新步骤的结果类型
         * @return 以新结果作为默认输入的单参 DSL 状态
         */
        <R> StepIn<S1, S2, R> and(Fn.BiFunction<? super IN1, ? super IN2, ? extends R> ability);
    }

    /**
     * 等待添加三参处理函数的 DSL 状态
     * <p>
     * 三个参数必须通过 {@code with} 从已命名的初始参数或步骤结果中选择
     *
     * @param <S1>  流水线初始参数1类型
     * @param <S2>  流水线初始参数2类型
     * @param <IN1> 当前待处理参数1类型
     * @param <IN2> 当前待处理参数2类型
     * @param <IN3> 当前待处理参数3类型
     */
    interface StepIn3<S1, S2, IN1, IN2, IN3> extends StepInit<S1, S2> {

        /**
         * 追加三参执行步骤
         *
         * @param ability 接收三个已选参数并产生新结果的函数
         * @param <R>     新步骤的结果类型
         * @return 以新结果作为默认输入的单参 DSL 状态
         */
        <R> StepIn<S1, S2, R> and(Fn.TriFunction<? super IN1, ? super IN2, ? super IN3, ? extends R> ability);
    }

    /**
     * 已产生当前步骤结果的 DSL 状态
     *
     * @param <S1> 流水线初始参数1类型
     * @param <S2> 流水线初始参数2类型
     * @param <IN> 当前步骤结果类型
     */
    interface StepOut<S1, S2, IN> extends StepInit<S1, S2> {

        /**
         * 结束 DSL 构建并生成双入参流水线
         *
         * @return 以当前步骤结果作为最终输出的流水线
         */
        Pipeline2<S1, S2, IN> end();

        /**
         * 将当前步骤结果保存到执行上下文
         *
         * @param name 结果名称及类型声明
         * @param <T>  声明名称的宿主类型
         * @return 保持当前结果类型的 DSL 状态
         */
        <T> StepIn<S1, S2, IN> as(Named<T, IN> name);

        /**
         * 为当前步骤设置异常降级函数
         * <p>
         * 降级函数结果替代失败步骤的结果，之后流水线继续执行
         *
         * @param handler 接收步骤异常并返回替代结果的函数
         * @return 保持当前结果类型的 DSL 状态
         */
        StepIn<S1, S2, IN> failOver(Fn.Function<? super Exception, IN> handler);
    }


    /**
     * 可为下一执行步骤选择输入参数的 DSL 状态
     * <p>
     * 名称可以来自创建流水线时声明的初始参数，也可以来自之前的 {@code as}
     * 未找到指定名称时会在执行到该步骤时抛出 {@link IllegalStateException}
     *
     * @param <S1> 流水线初始参数1类型
     * @param <S2> 流水线初始参数2类型
     */
    interface StepInit<S1, S2> {

        /**
         * 通过命名方法引用选择下一步骤的一个参数
         *
         * @param name 参数名称及类型声明
         * @param <T>  声明名称的宿主类型
         * @param <IN> 参数类型
         * @return 等待添加单参函数的 DSL 状态
         */
        <T, IN> StepIn<S1, S2, IN> with(Named<T, IN> name);

        /**
         * 通过上下文键选择下一步骤的一个参数
         *
         * @param name 上下文键
         * @param <IN> 由调用方声明的参数类型
         * @return 等待添加单参函数的 DSL 状态
         */
        <IN> StepIn<S1, S2, IN> with(String name);

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
        <T1, T2, IN1, IN2> StepIn2<S1, S2, IN1, IN2> with(Named<T1, IN1> name1, Named<T2, IN2> name2);

        /**
         * 通过上下文键选择下一步骤的两个参数
         *
         * @param name1 参数1上下文键
         * @param name2 参数2上下文键
         * @param <IN1> 由调用方声明的参数1类型
         * @param <IN2> 由调用方声明的参数2类型
         * @return 等待添加双参函数的 DSL 状态
         */
        <IN1, IN2> StepIn2<S1, S2, IN1, IN2> with(String name1, String name2);

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
        <T1, T2, T3, IN1, IN2, IN3> StepIn3<S1, S2, IN1, IN2, IN3> with(Named<T1, IN1> name1, Named<T2, IN2> name2, Named<T3, IN3> name3);

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
        <IN1, IN2, IN3> StepIn3<S1, S2, IN1, IN2, IN3> with(String name1, String name2, String name3);
    }
}
