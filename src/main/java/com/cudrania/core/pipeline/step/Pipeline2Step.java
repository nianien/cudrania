package com.cudrania.core.pipeline.step;

import com.cudrania.core.functions.Fn;
import com.cudrania.core.functions.Fn.Function;
import com.cudrania.core.pipeline.Named;
import com.cudrania.core.pipeline.Pipeline2;
import com.cudrania.core.pipeline.Pipeline2.StepIn;
import com.cudrania.core.pipeline.Pipeline2.StepIn2;
import com.cudrania.core.pipeline.Pipeline2.StepIn3;
import com.cudrania.core.pipeline.Pipelines;
import lombok.Getter;

/**
 * 流水线步骤实现
 *
 * @param <S1>  流水线第一个参数
 * @param <S2>  流水线第二个参数
 * @param <IN1> 当前步骤第一个参数
 * @param <IN2> 当前步骤第二个参数
 * @param <IN3> 当前步骤第三个参数
 */
@Getter
public class Pipeline2Step<S1, S2, IN1, IN2, IN3> extends AbstractStep<Pipeline2Step> implements StepIn<S1, S2, IN1>, StepIn2<S1, S2, IN1, IN2>, StepIn3<S1, S2, IN1, IN2, IN3> {

    private Pipeline2Step() {
        super();
    }

    private Pipeline2Step(AbstractStep preStep) {
        super(preStep);
    }

    @Override
    public Pipeline2Step nextStep() {
        return new Pipeline2Step(this);
    }

    @Override
    public <T, IN> StepIn<S1, S2, IN> with(Named<T, IN> name) {
        return inputNames(name);
    }

    @Override
    public <IN> StepIn<S1, S2, IN> with(String name) {
        return inputNames(name);
    }

    @Override
    public <T, IN1, IN2> StepIn2<S1, S2, IN1, IN2> with(Named<T, IN1> name1, Named<T, IN2> name2) {
        return inputNames(name1, name2);
    }

    @Override
    public <IN1, IN2> StepIn2<S1, S2, IN1, IN2> with(String name1, String name2) {
        return inputNames(name1, name2);
    }

    @Override
    public <T, IN1, IN2, IN3> StepIn3<S1, S2, IN1, IN2, IN3> with(Named<T, IN1> name1, Named<T, IN2> name2, Named<T, IN3> name3) {
        return inputNames(name1, name2, name3);
    }

    @Override
    public <IN1, IN2, IN3> StepIn3<S1, S2, IN1, IN2, IN3> with(String name1, String name2, String name3) {
        return inputNames(name1, name2, name3);
    }

    @Override
    public <R> StepIn<S1, S2, R> and(Function<? super IN1, ? extends R> ability) {
        return addAbility(ability);
    }

    @Override
    public Pipeline2<S1, S2, IN1> end() {
        return (Pipeline2<S1, S2, IN1>) new Pipelines(this);
    }

    @Override
    public <T> StepIn<S1, S2, IN1> as(Named<T, IN1> name) {
        return outputName(name);
    }

    @Override
    public StepIn<S1, S2, IN1> failOver(Function<? super Exception, IN1> handler) {
        return errorHandler(handler);
    }


    @Override
    public <R> StepIn<S1, S2, R> and(Fn.BiFunction<? super IN1, ? super IN2, ? extends R> ability) {
        return addAbility(ability);
    }

    @Override
    public <R> StepIn<S1, S2, R> and(Fn.TriFunction<? super IN1, ? super IN2, ? super IN3, ? extends R> ability) {
        return addAbility(ability);
    }


    /**
     * 设置初始参数
     *
     * @param input1 参数1名称
     * @param input2 参数2名称
     * @param <T>
     * @param <IN1>
     * @return
     */
    public static <T, IN1, IN2> Pipeline2.StepIn2<IN1, IN2, IN1, IN2> begin(Named<T, IN1> input1, Named<T, IN2> input2) {
        return new Pipeline2Step<>().outputName(input1, input2);
    }

    /**
     * 设置初始参数类型
     *
     * @param <IN1> 参数1类型
     * @param <IN2> 参数2类型
     * @return
     */
    public static <IN1, IN2> Pipeline2.StepIn2<IN1, IN2, IN1, IN2> begin() {
        return new Pipeline2Step();
    }

}
