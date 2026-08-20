package com.nianien.cudrania.core.pipeline.step;

import com.nianien.cudrania.core.functions.Fn;
import com.nianien.cudrania.core.functions.Fn.Function;
import com.nianien.cudrania.core.pipeline.Named;
import com.nianien.cudrania.core.pipeline.Pipeline;
import com.nianien.cudrania.core.pipeline.Pipeline2;
import com.nianien.cudrania.core.pipeline.Pipeline3;
import com.nianien.cudrania.core.pipeline.Pipelines;

/**
 * 单参、双参和三参 Pipeline 共用的 DSL 步骤实现
 * <p>
 * 一个实例同时实现九个 DSL 状态接口
 * 各接口中的同名方法通过返回具体 {@code PipelineStep} 类型满足协变返回约束
 * 泛型参数只描述构建期状态，运行时步骤数据由 {@link AbstractStep} 保存
 * <p>
 * 调用方通常不直接使用本类，而是通过 {@link Pipelines#of(Class)} 等入口开始构建
 * 未使用的初始参数位置和当前参数位置以 {@link Void} 作为占位类型
 *
 * @param <S1>  流水线初始参数1类型
 * @param <S2>  流水线初始参数2类型
 * @param <S3>  流水线初始参数3类型
 * @param <IN1> 当前待处理参数1类型
 * @param <IN2> 当前待处理参数2类型
 * @param <IN3> 当前待处理参数3类型
 * @see Pipelines
 */
public final class PipelineStep<S1, S2, S3, IN1, IN2, IN3> extends AbstractStep implements
        Pipeline.StepIn<S1, IN1>,
        Pipeline.StepIn2<S1, IN1, IN2>,
        Pipeline.StepIn3<S1, IN1, IN2, IN3>,
        Pipeline2.StepIn<S1, S2, IN1>,
        Pipeline2.StepIn2<S1, S2, IN1, IN2>,
        Pipeline2.StepIn3<S1, S2, IN1, IN2, IN3>,
        Pipeline3.StepIn<S1, S2, S3, IN1>,
        Pipeline3.StepIn2<S1, S2, S3, IN1, IN2>,
        Pipeline3.StepIn3<S1, S2, S3, IN1, IN2, IN3> {

    /**
     * 创建初始哨兵节点
     */
    private PipelineStep() {
        super();
    }

    /**
     * 创建并连接到前置节点的执行节点
     *
     * @param preStep 前置节点
     */
    private PipelineStep(AbstractStep preStep) {
        super(preStep);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PipelineStep<S1, S2, S3, IN1, IN2, IN3> nextStep() {
        return new PipelineStep<>(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, IN> PipelineStep<S1, S2, S3, IN, Void, Void> with(Named<T, IN> name) {
        return typed(inputNames(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <IN> PipelineStep<S1, S2, S3, IN, Void, Void> with(String name) {
        return typed(inputNames(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1, T2, A, B> PipelineStep<S1, S2, S3, A, B, Void> with(Named<T1, A> name1, Named<T2, B> name2) {
        return typed(inputNames(name1, name2));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A, B> PipelineStep<S1, S2, S3, A, B, Void> with(String name1, String name2) {
        return typed(inputNames(name1, name2));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1, T2, T3, A, B, C> PipelineStep<S1, S2, S3, A, B, C> with(Named<T1, A> name1, Named<T2, B> name2, Named<T3, C> name3) {
        return typed(inputNames(name1, name2, name3));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A, B, C> PipelineStep<S1, S2, S3, A, B, C> with(String name1, String name2, String name3) {
        return typed(inputNames(name1, name2, name3));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> PipelineStep<S1, S2, S3, R, Void, Void> and(Function<? super IN1, ? extends R> ability) {
        return typed(addAbility(ability));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> PipelineStep<S1, S2, S3, R, Void, Void> and(Fn.BiFunction<? super IN1, ? super IN2, ? extends R> ability) {
        return typed(addAbility(ability));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> PipelineStep<S1, S2, S3, R, Void, Void> and(Fn.TriFunction<? super IN1, ? super IN2, ? super IN3, ? extends R> ability) {
        return typed(addAbility(ability));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> PipelineStep<S1, S2, S3, IN1, IN2, IN3> as(Named<T, IN1> name) {
        return typed(outputName(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PipelineStep<S1, S2, S3, IN1, IN2, IN3> failOver(Function<? super Exception, IN1> handler) {
        return typed(errorHandler(handler));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pipelines<S1, S2, S3, IN1> end() {
        return new Pipelines<>(this);
    }

    /**
     * 把运行时节点解释为目标 DSL 泛型状态
     * <p>
     * 该转换只接收当前节点或由 {@link #nextStep()} 创建的同类节点
     * 所有泛型参数均不参与运行时数据表示，因此转换集中在此处是安全的
     *
     * @param step 待转换的运行时节点
     * @param <A>  目标参数1类型
     * @param <B>  目标参数2类型
     * @param <C>  目标参数3类型
     * @return 具有目标 DSL 泛型状态的步骤
     */
    @SuppressWarnings("unchecked")
    private <A, B, C> PipelineStep<S1, S2, S3, A, B, C> typed(AbstractStep step) {
        return (PipelineStep<S1, S2, S3, A, B, C>) step;
    }

    /**
     * 使用一个命名初始参数创建步骤链
     *
     * @param input 初始参数名称及类型声明
     * @param <T>   声明名称的宿主类型
     * @param <IN>  初始参数类型
     * @return 等待处理初始参数的单参 DSL 状态
     */
    public static <T, IN> Pipeline.StepIn<IN, IN> begin(Named<T, IN> input) {
        PipelineStep<IN, Void, Void, IN, Void, Void> step = new PipelineStep<>();
        step.outputName(input);
        return step;
    }

    /**
     * 使用两个命名初始参数创建步骤链
     *
     * @param input1 初始参数1名称及类型声明
     * @param input2 初始参数2名称及类型声明
     * @param <T1>   声明参数1名称的宿主类型
     * @param <T2>   声明参数2名称的宿主类型
     * @param <IN1>  初始参数1类型
     * @param <IN2>  初始参数2类型
     * @return 等待处理两个初始参数的双参 DSL 状态
     */
    public static <T1, T2, IN1, IN2> Pipeline2.StepIn2<IN1, IN2, IN1, IN2> begin(Named<T1, IN1> input1, Named<T2, IN2> input2) {
        PipelineStep<IN1, IN2, Void, IN1, IN2, Void> step = new PipelineStep<>();
        step.outputName(input1, input2);
        return step;
    }

    /**
     * 使用三个命名初始参数创建步骤链
     *
     * @param input1 初始参数1名称及类型声明
     * @param input2 初始参数2名称及类型声明
     * @param input3 初始参数3名称及类型声明
     * @param <T1>   声明参数1名称的宿主类型
     * @param <T2>   声明参数2名称的宿主类型
     * @param <T3>   声明参数3名称的宿主类型
     * @param <IN1>  初始参数1类型
     * @param <IN2>  初始参数2类型
     * @param <IN3>  初始参数3类型
     * @return 等待处理三个初始参数的三参 DSL 状态
     */
    public static <T1, T2, T3, IN1, IN2, IN3> Pipeline3.StepIn3<IN1, IN2, IN3, IN1, IN2, IN3> begin(Named<T1, IN1> input1, Named<T2, IN2> input2, Named<T3, IN3> input3) {
        PipelineStep<IN1, IN2, IN3, IN1, IN2, IN3> step = new PipelineStep<>();
        step.outputName(input1, input2, input3);
        return step;
    }

    /**
     * 使用一个初始参数类型创建步骤链
     *
     * @param input 初始参数类型
     * @param <IN>  初始参数类型
     * @return 等待处理初始参数的单参 DSL 状态
     */
    public static <IN> Pipeline.StepIn<IN, IN> begin(Class<IN> input) {
        return new PipelineStep<>();
    }

    /**
     * 使用两个初始参数类型创建步骤链
     *
     * @param input1 初始参数1类型
     * @param input2 初始参数2类型
     * @param <IN1>  初始参数1类型
     * @param <IN2>  初始参数2类型
     * @return 等待处理两个初始参数的双参 DSL 状态
     */
    public static <IN1, IN2> Pipeline2.StepIn2<IN1, IN2, IN1, IN2> begin(Class<IN1> input1, Class<IN2> input2) {
        return new PipelineStep<>();
    }

    /**
     * 使用三个初始参数类型创建步骤链
     *
     * @param input1 初始参数1类型
     * @param input2 初始参数2类型
     * @param input3 初始参数3类型
     * @param <IN1>  初始参数1类型
     * @param <IN2>  初始参数2类型
     * @param <IN3>  初始参数3类型
     * @return 等待处理三个初始参数的三参 DSL 状态
     */
    public static <IN1, IN2, IN3> Pipeline3.StepIn3<IN1, IN2, IN3, IN1, IN2, IN3> begin(Class<IN1> input1, Class<IN2> input2, Class<IN3> input3) {
        return new PipelineStep<>();
    }
}
