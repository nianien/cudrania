package com.cudrania.core.pipeline;


import com.cudrania.core.functions.Fn;

/**
 * 支持双参处理逻辑
 *
 * @param <Pipe> Pipeline类型
 * @param <OUT>  Pipeline输出类型
 * @param <IN1>  第一个执行参数类型
 * @param <IN2>  第二个执行参数类型
 */
public interface StepIn2<Pipe, OUT, IN1, IN2> extends StepOut<Pipe, OUT>{


    /**
     * 添加双参处理逻辑
     *
     * @param ability
     * @return
     */
    <R> StepIn<Pipe, OUT, R> and(Fn.BiFunction<? super IN1, ? super IN2, ? extends R> ability);

}
