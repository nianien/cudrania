package com.cudrania.core.pipeline;


import com.cudrania.core.functions.Fn;

/**
 * 支持单参处理逻辑
 *
 * @param <Pipe> Pipeline类型
 * @param <OUT>  Pipeline输出类型
 * @param <IN>   执行参数类型
 */
public interface StepIn<Pipe, OUT, IN> extends StepOut<Pipe, OUT> {


    /**
     * 添加单参处理逻辑
     *
     * @return
     */
    <R> StepIn<Pipe, OUT, R> and(Fn.Function<? super IN, ? extends R> ability);


    /**
     * 构建结束,生成相应pipeline
     *
     * @return
     */
    Pipe end(Fn.Function<? super IN, ? extends OUT> ability);


    /**
     * 保存结果
     *
     * @param name
     * @return
     */
    <T> StepIn<Pipe, OUT, IN> as(Named<T, IN> name);


    /**
     * 异常处理
     *
     * @param handler
     * @return
     */
    StepIn<Pipe, OUT, IN> failOver(Fn.Function<? super Exception, IN> handler);


}
