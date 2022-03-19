package com.cudrania.core.pipeline;


import com.cudrania.core.functions.Fn;

/**
 * 支持双参处理逻辑
 *
 * @param <Pipe>
 * @param <IN1> 第一个执行参数类型
 * @param <IN2> 第二个执行参数类型
 */
public interface StepIn2<Pipe, IN1, IN2> extends StepOut<Pipe> {


    /**
     * 添加双参处理逻辑
     *
     * @param ability
     * @return
     */
    <R> StepIn<Pipe, R> and(Fn.BiFunction<? super IN1, ? super IN2, ? extends R> ability);

}
