package com.cudrania.core.pipeline;

/**
 * 构建结束或者继续下一个步骤
 *
 * @param <Pipe>
 */
public interface StepOut<Pipe> {


    /**
     * 设置下一步骤入参名称和类型,自动重从上下文中获取
     *
     * @param name 入参名称和类型
     * @param <T>  入参类型
     * @param <IN>
     * @return
     */
    <T, IN> StepIn<Pipe, IN> with(Named<T, IN> name);

    /**
     * 设置下一步骤的入参名称,自动重从上下文中获取
     *
     * @param name 入参名称
     * @param <IN> 入参类型
     * @return
     */
    <IN> StepIn<Pipe, IN> with(String name);

    /**
     * 设置下一步骤的入参名称,自动重从上下文中获取
     *
     * @param name1 第一个入参名称和类型
     * @param name2 第二个入参名称和类型
     * @param <IN1> 第一个入参类型
     * @param <IN2> 第二个入参类型
     * @return
     */
    <T, IN1, IN2> StepIn2<Pipe, IN1, IN2> with(Named<T, IN1> name1, Named<T, IN2> name2);

    /**
     * 设置下一步骤的入参名称,自动重从上下文中获取
     *
     * @param name1 第一个入参名称
     * @param name2 第二个入参名称
     * @param <IN1> 第一个入参类型
     * @param <IN2> 第二个入参类型
     * @return
     */
    <IN1, IN2> StepIn2<Pipe, IN1, IN2> with(String name1, String name2);


    /**
     * 设置下一步骤的入参名称,自动重从上下文中获取
     *
     * @param name1 第一个入参名称和类型
     * @param name2 第二个入参名称和类型
     * @param name3 第三个入参名称和类型
     * @param <IN1> 第一个入参类型
     * @param <IN2> 第二个入参类型
     * @param <IN3> 第三个入参类型
     * @return
     */
    <T, IN1, IN2, IN3> StepIn3<Pipe, IN1, IN2, IN3> with(Named<T, IN1> name1, Named<T, IN2> name2, Named<T, IN3> name3);

    /**
     * 设置下一步骤的入参名称,自动重从上下文中获取
     *
     * @param name1 第一个入参名称
     * @param name2 第二个入参名称
     * @param name3 第三个入参名称
     * @param <IN1> 第一个入参类型
     * @param <IN2> 第二个入参类型
     * @param <IN3> 第三个入参类型
     * @return
     */
    <IN1, IN2, IN3> StepIn3<Pipe, IN1, IN2, IN3> with(String name1, String name2, String name3);


    /**
     * 构建结束,生成相应pipeline
     *
     * @return
     */
    Pipe end();


}
