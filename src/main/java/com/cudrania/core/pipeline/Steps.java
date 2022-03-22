package com.cudrania.core.pipeline;

import com.cudrania.core.functions.Fn;
import com.cudrania.core.functions.Fn.Function;
import com.cudrania.core.functions.Fn.Lambda;
import lombok.Getter;

import java.util.Arrays;

/**
 * 流水线步骤实现
 *
 * @param <Pipe>
 * @param <OUT>
 * @param <IN1>
 * @param <IN2>
 * @param <IN3>
 */
@Getter
public class Steps<Pipe, OUT, IN1, IN2, IN3> implements StepInit<OUT>, StepIn<Pipe, OUT, IN1>, StepIn2<Pipe, OUT, IN1, IN2>, StepIn3<Pipe, OUT, IN1, IN2, IN3> {

    /**
     * 入参名称
     */
    private String[] inputNames = new String[0];
    /**
     * 出参名称
     */
    private String[] outputNames = new String[0];
    /**
     * 能力模型
     */
    private Lambda ability;
    /**
     * 下一步骤
     */
    private Steps<Pipe, OUT, IN1, IN2, IN3> nextStep;
    /**
     * 初始步骤,入参用于存储下个步骤的入参,出参作为初始参数
     */
    private Steps<Pipe, OUT, IN1, IN2, IN3> initStep;
    /**
     * 节点深度
     */
    private int depth = 0;
    /**
     * 异常处理
     */
    private Function<? super Exception, IN1> errorHandler;

    /**
     * 初始化
     */
    Steps() {
        this.initStep = this;
    }


    /**
     * 构建下一步
     *
     * @param preStep
     */
    private Steps(Steps<Pipe, OUT, IN1, IN2, IN3> preStep) {
        this.initStep = preStep.initStep;
        this.depth = preStep.depth + 1;
        preStep.nextStep = this;
    }

    @Override
    public <T, IN> StepIn<Pipe, OUT, IN> with(Named<T, IN> name) {
        this.initStep.inputNames = toStrings(name);
        return (StepIn<Pipe, OUT, IN>) this;
    }

    @Override
    public <IN> StepIn<Pipe, OUT, IN> with(String name) {
        this.initStep.inputNames = toStrings(name);
        return (StepIn<Pipe, OUT, IN>) this;
    }

    @Override
    public <T, IN1, IN2> StepIn2<Pipe, OUT, IN1, IN2> with(Named<T, IN1> name1, Named<T, IN2> name2) {
        this.initStep.inputNames = toStrings(name1, name2);
        return (StepIn2<Pipe, OUT, IN1, IN2>) this;
    }

    @Override
    public <IN1, IN2> StepIn2<Pipe, OUT, IN1, IN2> with(String name1, String name2) {
        this.initStep.inputNames = toStrings(name1, name2);
        return (StepIn2<Pipe, OUT, IN1, IN2>) this;
    }

    @Override
    public <T, IN1, IN2, IN3> StepIn3<Pipe, OUT, IN1, IN2, IN3> with(Named<T, IN1> name1, Named<T, IN2> name2, Named<T, IN3> name3) {
        this.initStep.inputNames = toStrings(name1, name2, name3);
        return (StepIn3<Pipe, OUT, IN1, IN2, IN3>) this;
    }

    @Override
    public <IN1, IN2, IN3> StepIn3<Pipe, OUT, IN1, IN2, IN3> with(String name1, String name2, String name3) {
        this.initStep.inputNames = toStrings(name1, name2, name3);
        return (StepIn3<Pipe, OUT, IN1, IN2, IN3>) this;
    }

    @Override
    public <R> StepIn<Pipe, OUT, R> and(Fn.Function<? super IN1, ? extends R> ability) {
        return and0(ability);
    }

    @Override
    public Pipe end(Fn.Function<? super IN1, ? extends OUT> ability) {
        this.and0(ability);
        return (Pipe) new Pipelines(this.initStep);
    }

    @Override
    public <T> StepIn<Pipe, OUT, IN1> as(Named<T, IN1> name) {
        this.outputNames = new String[]{name.name()};
        return this;
    }

    @Override
    public StepIn<Pipe, OUT, IN1> failOver(Function<? super Exception, IN1> handler) {
        this.errorHandler = handler;
        return this;
    }

    /**
     * 添加pipeline处理逻辑
     *
     * @param ability
     * @param <R>
     * @return
     */
    private <R> StepIn<Pipe, OUT, R> and0(Lambda ability) {
        Steps nextStep = new Steps(this);
        nextStep.ability = ability;
        nextStep.inputNames = this.initStep.inputNames;
        this.initStep.inputNames = new String[0];
        return nextStep;
    }

    /**
     * 字符串转换
     *
     * @param inputs
     * @return
     */
    private String[] toStrings(String... inputs) {
        return inputs;
    }

    /**
     * 字符串转换
     *
     * @param inputs
     * @return
     */
    private String[] toStrings(Named<?, ?>... inputs) {
        return Arrays.stream(inputs).map(Named::name).toArray(n -> new String[n]);
    }

    @Override
    public <R> StepIn<Pipe, OUT, R> and(Fn.BiFunction<? super IN1, ? super IN2, ? extends R> ability) {
        return and0(ability);
    }

    @Override
    public <R> StepIn<Pipe, OUT, R> and(Fn.TriFunction<? super IN1, ? super IN2, ? super IN3, ? extends R> ability) {
        return and0(ability);
    }

    @Override
    public <T, IN1> StepIn<Pipeline<IN1, OUT>, OUT, IN1> begin(Named<T, IN1> input) {
        this.outputNames = toStrings(input);
        return (StepIn<Pipeline<IN1, OUT>, OUT, IN1>) this;
    }

    @Override
    public <IN1> StepIn<Pipeline<IN1, OUT>, OUT, IN1> begin(Class<IN1> input) {
        return (StepIn<Pipeline<IN1, OUT>, OUT, IN1>) this;
    }

    @Override
    public <T, IN1, IN2> StepIn2<Pipeline2<IN1, IN2, OUT>, OUT, IN1, IN2> begin(Named<T, IN1> input1, Named<T, IN2> input2) {
        this.outputNames = toStrings(input1, input2);
        return (StepIn2<Pipeline2<IN1, IN2, OUT>, OUT, IN1, IN2>) this;
    }

    @Override
    public <IN1, IN2> StepIn2<Pipeline2<IN1, IN2, OUT>, OUT, IN1, IN2> begin(Class<IN1> input1, Class<IN2> input2) {
        return (StepIn2<Pipeline2<IN1, IN2, OUT>, OUT, IN1, IN2>) this;
    }

    @Override
    public <T, IN1, IN2, IN3> StepIn3<Pipeline3<IN1, IN2, IN3, OUT>, OUT, IN1, IN2, IN3> begin(Named<T, IN1> input1, Named<T, IN2> input2, Named<T, IN3> input3) {
        this.outputNames = toStrings(input1, input2, input3);
        return (StepIn3<Pipeline3<IN1, IN2, IN3, OUT>, OUT, IN1, IN2, IN3>) this;
    }

    @Override
    public <IN1, IN2, IN3> StepIn3<Pipeline3<IN1, IN2, IN3, OUT>, OUT, IN1, IN2, IN3> begin(Class<IN1> input1, Class<IN2> input2, Class<IN3> input3) {
        return (StepIn3<Pipeline3<IN1, IN2, IN3, OUT>, OUT, IN1, IN2, IN3>) this;
    }

}
