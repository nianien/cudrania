package com.cudrania.core.pipeline;

import com.cudrania.core.functions.Fn;

public interface Pipeline<IN, OUT> {

    /**
     * 计算pipeline
     *
     * @param input
     * @return
     */
    OUT eval(IN input);


    /**
     * 单参处理步骤
     */
    interface StepIn<S, IN> extends StepOut<S> {


        /**
         * 添加执行步骤
         *
         * @param ability 单参函数, 参数为上一个执行结果或者通过方法{@link #with(Named)}和{@link #with(String)}指定
         * @return
         */
        <R> StepIn<S, R> and(Fn.Function<? super IN, ? extends R> ability);


        /**
         * 构建结束,生成pipeline
         *
         * @return
         */
        Pipeline<S, IN> end();


        /**
         * 保存计算结果
         *
         * @param name 标记结果名称
         * @return
         */
        <T> StepIn<S, IN> as(Named<T, IN> name);


        /**
         * 异常处理
         *
         * @param handler
         * @return
         */
        StepIn<S, IN> failOver(Fn.Function<? super Exception, IN> handler);


    }


    /**
     * 双参处理步骤
     */
    interface StepIn2<S, IN1, IN2> extends StepOut<S> {


        /**
         * 添加执行步骤
         *
         * @param ability 双参函数, 参数可通过方法{@link #with(Named, Named)}或者{@link #with(String, String)}指定
         * @return
         */
        <R> StepIn<S, R> and(Fn.BiFunction<? super IN1, ? super IN2, ? extends R> ability);

    }

    /**
     * 三参处理步骤
     */
    interface StepIn3<S, IN1, IN2, IN3> extends StepOut<S> {


        /**
         * 添加执行步骤
         *
         * @param ability 双参函数, 参数可通过方法{@link #with(Named, Named, Named)}或者{@link #with(String, String, String)}指定
         * @return
         */
        <R> StepIn<S, R> and(Fn.TriFunction<? super IN1, ? super IN2, ? super IN3, ? extends R> ability);


    }

    /**
     * 构建结束或者继续下一个步骤
     */
    interface StepOut<S> {


        /**
         * 指定下一步的参数名称和类型
         *
         * @param name 参数名称
         * @param <T>  声明参数的对象
         * @param <IN> 参数类型
         * @return
         */
        <T, IN> StepIn<S, IN> with(Named<T, IN> name);

        /**
         * 设置下一步骤的入参名称
         *
         * @param name 入参名称
         * @param <IN> 入参类型
         * @return
         */
        <IN> StepIn<S, IN> with(String name);

        /**
         * 指定下一步的参数名称和类型
         *
         * @param name1 参数1名称
         * @param name2 参数2名称
         * @param <T1> 声明参数1的对象
         * @param <T2> 声明参数2的对象
         * @param <IN1> 参数1类型
         * @param <IN2> 参数2类型
         * @return
         */
        <T1,T2, IN1, IN2> StepIn2<S, IN1, IN2> with(Named<T1, IN1> name1, Named<T2, IN2> name2);

        /**
         * 指定下一步的参数名称和类型
         *
         * @param name1 参数1名称
         * @param name2 参数1名称
         * @param <IN1> 参数1类型
         * @param <IN2> 参数2类型
         * @return
         */
        <IN1, IN2> StepIn2<S, IN1, IN2> with(String name1, String name2);


        /**
         * 指定下一步的参数名称和类型
         *
         * @param name1 参数1名称
         * @param name2 参数2名称
         * @param name3 参数3名称
         * @param <T1> 声明参数1的对象
         * @param <T2> 声明参数2的对象
         * @param <T3> 声明参数3的对象
         * @param <IN1> 参数1类型
         * @param <IN2> 参数2类型
         * @param <IN3> 参数3类型
         * @return
         */
        <T1, T2, T3, IN1, IN2, IN3> StepIn3<S, IN1, IN2, IN3> with(Named<T1, IN1> name1, Named<T2, IN2> name2, Named<T3, IN3> name3);

        /**
         * 指定下一步的参数名称和类型
         *
         * @param name1 参数1名称
         * @param name2 参数2名称
         * @param name3 参数3名称
         * @param <IN1> 参数1类型
         * @param <IN2> 参数2类型
         * @param <IN3> 参数3类型
         * @return
         */
        <IN1, IN2, IN3> StepIn3<S, IN1, IN2, IN3> with(String name1, String name2, String name3);

    }


}
