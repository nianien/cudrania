package com.cudrania.core.pipeline.step;

import com.cudrania.core.functions.Fn;
import com.cudrania.core.functions.Fn.Function;
import com.cudrania.core.pipeline.Named;
import com.cudrania.core.pipeline.Pipeline3;
import com.cudrania.core.pipeline.Pipeline3.StepIn;
import com.cudrania.core.pipeline.Pipeline3.StepIn2;
import com.cudrania.core.pipeline.Pipeline3.StepIn3;
import com.cudrania.core.pipeline.Pipelines;
import lombok.Getter;

/**
 * 流水线步骤实现
 *
 * @param <S1>  流水线参数1
 * @param <S2>  流水线参数2
 * @param <S3>  流水线参数3
 * @param <IN1> 当前步骤参数1
 * @param <IN2> 当前步骤参数2
 * @param <IN3> 当前步骤参数3
 */
@Getter
public class Pipeline3Step<S1, S2, S3, IN1, IN2, IN3> extends AbstractStep<Pipeline3Step> implements StepIn<S1, S2, S3, IN1>, StepIn2<S1, S2, S3, IN1, IN2>, StepIn3<S1, S2, S3, IN1, IN2, IN3> {

    private Pipeline3Step() {
        super();
    }

    private Pipeline3Step(AbstractStep preStep) {
        super(preStep);
    }


    @Override
    public Pipeline3Step nextStep() {
        return new Pipeline3Step(this);
    }


    @Override
    public <T, IN> StepIn<S1, S2, S3, IN> with(Named<T, IN> name) {
        return inputNames(name);
    }

    @Override
    public <IN> StepIn<S1, S2, S3, IN> with(String name) {
        return inputNames(name);
    }

    @Override
    public <T1, T2, IN1, IN2> StepIn2<S1, S2, S3, IN1, IN2> with(Named<T1, IN1> name1, Named<T2, IN2> name2) {
        return inputNames(name1, name2);
    }

    @Override
    public <IN1, IN2> StepIn2<S1, S2, S3, IN1, IN2> with(String name1, String name2) {
        return inputNames(name1, name2);
    }

    @Override
    public <T1, T2, T3, IN1, IN2, IN3> StepIn3<S1, S2, S3, IN1, IN2, IN3> with(Named<T1, IN1> name1, Named<T2, IN2> name2, Named<T3, IN3> name3) {
        return inputNames(name1, name2, name3);
    }

    @Override
    public <IN1, IN2, IN3> StepIn3<S1, S2, S3, IN1, IN2, IN3> with(String name1, String name2, String name3) {
        return inputNames(name1, name2, name3);
    }

    @Override
    public <R> StepIn<S1, S2, S3, R> and(Function<? super IN1, ? extends R> ability) {
        return addAbility(ability);
    }

    @Override
    public Pipeline3<S1, S2, S3, IN1> end() {
        return (Pipeline3<S1, S2, S3, IN1>) new Pipelines(this);
    }

    @Override
    public <T> StepIn<S1, S2, S3, IN1> as(Named<T, IN1> name) {
        return outputName(name);
    }

    @Override
    public StepIn<S1, S2, S3, IN1> failOver(Function<? super Exception, IN1> handler) {
        return errorHandler(handler);
    }

    @Override
    public <R> StepIn<S1, S2, S3, R> and(Fn.BiFunction<? super IN1, ? super IN2, ? extends R> ability) {
        return addAbility(ability);
    }

    @Override
    public <R> StepIn<S1, S2, S3, R> and(Fn.TriFunction<? super IN1, ? super IN2, ? super IN3, ? extends R> ability) {
        return addAbility(ability);
    }

    /**
     * pipeline的初始参数名称和类型
     *
     * @param input1 参数1名称
     * @param input2 参数2名称
     * @param input3 参数3名称
     * @param <T1> 声明参数1的对象
     * @param <T2> 声明参数2的对象
     * @param <T3> 声明参数3的对象
     * @param <IN1>
     * @return
     */
    public static <T1, T2, T3, IN1, IN2, IN3> Pipeline3.StepIn3<IN1, IN2, IN3, IN1, IN2, IN3> begin(Named<T1, IN1> input1, Named<T2, IN2> input2, Named<T3, IN3> input3) {
        return new Pipeline3Step<>().outputName(input1, input2, input3);
    }

    /**
     * pipeline的初始参数名称和类型
     *
     * @param <IN1> 参数1类型
     * @param <IN2> 参数2类型
     * @param <IN3> 参数3类型
     * @return
     */
    public static <IN1, IN2, IN3> Pipeline3.StepIn3<IN1, IN2, IN3, IN1, IN2, IN3> begin() {
        return new Pipeline3Step();
    }

}
