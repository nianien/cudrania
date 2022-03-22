package com.cudrania.core.pipeline;


import com.cudrania.core.functions.Fn;

/**
 * 支持三参处理逻辑
 *
 * @param <Pipe> Pipeline类型
 * @param <OUT>  Pipeline输出类型
 * @param <IN1>  第一个执行参数类型
 * @param <IN2>  第二个执行参数类型
 * @param <IN3>  第三个执行参数类型
 */
public interface StepIn3<Pipe, OUT, IN1, IN2, IN3> extends StepOut<Pipe, OUT> {


    /**
     * 添加三参处理逻辑
     *
     * @param ability
     * @param <R>
     * @return
     */
    <R> StepIn<Pipe, OUT, R> and(Fn.TriFunction<? super IN1, ? super IN2, ? super IN3, ? extends R> ability);


}
