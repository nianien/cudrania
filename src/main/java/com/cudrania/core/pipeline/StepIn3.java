package com.cudrania.core.pipeline;


import com.cudrania.core.functions.Fn;

/**
 * 支持三参处理逻辑
 *
 * @param <Pipe>
 * @param <IN1>  第一个执行参数类型
 * @param <IN2>  第二个执行参数类型
 * @param <IN3>  第三个执行参数类型
 */
public interface StepIn3<Pipe, IN1, IN2, IN3> extends StepOut<Pipe> {


    /**
     * 添加三参处理逻辑
     *
     * @param ability
     * @param <R>
     * @return
     */
    <R> StepIn<Pipe, R> and(Fn.TriFunction<? super IN1, ? super IN2, ? super IN3, ? extends R> ability);


}
