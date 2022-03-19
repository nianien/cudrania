package com.cudrania.core.pipeline;


import com.cudrania.core.functions.Fn;

/**
 * 支持单参处理逻辑
 *
 * @param <Pipe>
 * @param <IN>   执行参数类型
 */
public interface StepIn<Pipe, IN> extends StepOut<Pipe> {


    /**
     * 添加单参处理逻辑
     *
     * @return
     */
    <R> StepIn<Pipe, R> and(Fn.Function<? super IN, ? extends R> ability);


    /**
     * 保存结果
     *
     * @param name
     * @return
     */
    <T> StepIn<Pipe, IN> as(Named<T, IN> name);


    /**
     * 异常处理
     *
     * @param handler
     * @return
     */
    StepIn<Pipe, IN> failOver(Fn.Function<? super Exception, IN> handler);
}
