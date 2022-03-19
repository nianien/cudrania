package com.cudrania.core.pipeline;

/**
 * 初始步骤,设置pipeline的期望结果
 *
 * @param <OUT>
 */
public interface StepInit<OUT> {


    /**
     * 设置初始参数类型和名称, 自动探测
     *
     * @param input 入参名称和类型
     * @param <T>
     * @param <IN1>
     * @return
     */
    <T, IN1> StepIn<Pipeline<IN1, OUT>, IN1> begin(Named<T, IN1> input);

    /**
     * 设置初始参数类型
     *
     * @param input 入参类型
     * @param <IN1>
     * @return
     */
    <IN1> StepIn<Pipeline<IN1, OUT>, IN1> begin(Class<IN1> input);

    /**
     * 设置初始参数类型和名称, 自动探测
     *
     * @param input1 第一个入参名称和类型
     * @param input2 第二个入参名称和类型
     * @param <T>
     * @param <IN1>
     * @param <IN2>
     * @return
     */
    <T, IN1, IN2> StepIn2<Pipeline2<IN1, IN2, OUT>, IN1, IN2> begin(Named<T, IN1> input1, Named<T, IN2> input2);

    /**
     * 设置初始参数类型
     *
     * @param input1 第一个入参类型
     * @param input2 第二个入参类型
     * @param <IN1>
     * @param <IN2>
     * @return
     */
    <IN1, IN2> StepIn2<Pipeline2<IN1, IN2, OUT>, IN1, IN2> begin(Class<IN1> input1, Class<IN2> input2);


    /**
     * 设置初始参数类型和名称, 自动探测
     *
     * @param input1 第一个入参名称和类型
     * @param input2 第二个入参名称和类型
     * @param input3 第三个入参名称和类型
     * @param <T>
     * @param <IN1>
     * @param <IN2>
     * @param <IN3>
     * @return
     */
    <T, IN1, IN2, IN3> StepIn3<Pipeline3<IN1, IN2, IN3, OUT>, IN1, IN2, IN3> begin(Named<T, IN1> input1, Named<T, IN2> input2, Named<T, IN3> input3);

    /**
     * 设置初始参数类型
     *
     * @param input1 第一个入参类型
     * @param input2 第二个入参类型
     * @param input3 第三个入参类型
     * @param <IN1>
     * @param <IN2>
     * @param <IN3>
     * @return
     */
    <IN1, IN2, IN3> StepIn3<Pipeline3<IN1, IN2, IN3, OUT>, IN1, IN2, IN3> begin(Class<IN1> input1, Class<IN2> input2, Class<IN3> input3);


}
