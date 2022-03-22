package com.cudrania.core.pipeline.step;

import com.cudrania.core.functions.Fn;
import com.cudrania.core.functions.Fn.Function;
import com.cudrania.core.pipeline.Named;
import com.cudrania.core.pipeline.Pipeline;
import com.cudrania.core.pipeline.Pipeline.StepIn;
import com.cudrania.core.pipeline.Pipeline.StepIn2;
import com.cudrania.core.pipeline.Pipeline.StepIn3;
import com.cudrania.core.pipeline.Pipelines;
import lombok.Getter;

/**
 * 流水线步骤实现
 *
 * @param <S>   流水线入参
 * @param <IN1> 当前步骤参数1
 * @param <IN2> 当前步骤参数2
 * @param <IN3> 当前步骤参数3
 */
@Getter
public class PipelineStep<S, IN1, IN2, IN3> extends AbstractStep<PipelineStep> implements StepIn<S, IN1>, StepIn2<S, IN1, IN2>, StepIn3<S, IN1, IN2, IN3> {

    private PipelineStep() {
        super();
    }

    private PipelineStep(AbstractStep preStep) {
        super(preStep);
    }


    @Override
    public PipelineStep nextStep() {
        return new PipelineStep(this);
    }

    @Override
    public <T, IN> StepIn<S, IN> with(Named<T, IN> name) {
        return inputNames(name);
    }

    @Override
    public <IN> StepIn<S, IN> with(String name) {
        return inputNames(name);
    }

    @Override
    public <T1, T2, IN1, IN2> StepIn2<S, IN1, IN2> with(Named<T1, IN1> name1, Named<T2, IN2> name2) {
        return inputNames(name1, name2);
    }

    @Override
    public <IN1, IN2> StepIn2<S, IN1, IN2> with(String name1, String name2) {
        return inputNames(name1, name2);
    }

    @Override
    public <T1, T2, T3, IN1, IN2, IN3> StepIn3<S, IN1, IN2, IN3> with(Named<T1, IN1> name1, Named<T2, IN2> name2, Named<T3, IN3> name3) {
        return inputNames(name1, name2, name3);
    }

    @Override
    public <IN1, IN2, IN3> StepIn3<S, IN1, IN2, IN3> with(String name1, String name2, String name3) {
        return inputNames(name1, name2, name3);
    }

    @Override
    public <R> StepIn<S, R> and(Function<? super IN1, ? extends R> ability) {
        return addAbility(ability);
    }

    @Override
    public Pipeline<S, IN1> end() {
        return (Pipeline<S, IN1>) new Pipelines(this);
    }

    @Override
    public <T> StepIn<S, IN1> as(Named<T, IN1> name) {
        return outputName(name);
    }

    @Override
    public StepIn<S, IN1> failOver(Function<? super Exception, IN1> handler) {
        return errorHandler(handler);
    }


    @Override
    public <R> StepIn<S, R> and(Fn.BiFunction<? super IN1, ? super IN2, ? extends R> ability) {
        return addAbility(ability);
    }

    @Override
    public <R> StepIn<S, R> and(Fn.TriFunction<? super IN1, ? super IN2, ? super IN3, ? extends R> ability) {
        return addAbility(ability);
    }


    /**
     * pipeline的初始参数名称和类型
     *
     * @param input 入参名称
     * @param <T>
     * @param <IN1>
     * @return
     */
    public static <T, IN1> Pipeline.StepIn<IN1, IN1> begin(Named<T, IN1> input) {
        return new PipelineStep<>().outputName(input);
    }

    /**
     * pipeline的初始参数名称和类型
     *
     * @param <IN1> 入参类型
     * @return
     */
    public static <IN1> Pipeline.StepIn<IN1, IN1> begin() {
        return new PipelineStep();
    }


}
