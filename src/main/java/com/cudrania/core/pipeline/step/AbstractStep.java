package com.cudrania.core.pipeline.step;

import com.cudrania.core.functions.Fn.Function;
import com.cudrania.core.functions.Fn.Lambda;
import com.cudrania.core.pipeline.Named;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.invoke.SerializedLambda;
import java.util.Arrays;

/**
 * 流水线步骤实现
 */
@Getter
public abstract class AbstractStep<Step extends AbstractStep> {

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
    private AbstractStep nextStep;
    /**
     * 初始步骤,入参用于存储下个步骤的入参,出参作为初始参数
     */
    private AbstractStep initStep;
    /**
     * 节点深度
     */
    private int depth = 0;
    /**
     * 异常处理
     */
    private Function errorHandler;

    /**
     * 初始化
     */
    public AbstractStep() {
        this.initStep = this;
    }


    /**
     * 构造当前步骤,指定上一步骤
     *
     * @param preStep
     */
    protected AbstractStep(AbstractStep preStep) {
        this.initStep = preStep.initStep;
        this.depth = preStep.depth + 1;
        preStep.nextStep = this;
    }


    protected Step outputName(Named<?, ?>... names) {
        this.outputNames = toStrings(names);
        return (Step) this;
    }

    protected Step errorHandler(Function handler) {
        this.errorHandler = handler;
        return (Step) this;
    }


    protected Step inputNames(Named<?, ?>... names) {
        this.initStep.inputNames = toStrings(names);
        return (Step) this;
    }

    protected Step inputNames(String... names) {
        this.initStep.inputNames = names;
        return (Step) this;
    }

    protected Step addAbility(Lambda ability) {
        AbstractStep nextStep = nextStep();
        nextStep.ability = ability;
        //初始步骤用于保存下一步骤的入参, 构建完成之后需要清除
        nextStep.inputNames = this.initStep.inputNames;
        this.initStep.inputNames = new String[0];
        return (Step) nextStep;
    }


    /**
     * 创建下一个实例步骤
     *
     * @return
     */
    public abstract Step nextStep();

    /**
     * 字符串转换
     *
     * @param inputs
     * @return
     */
    private String[] toStrings(Named<?, ?>... inputs) {
        return Arrays.stream(inputs).map(this::resolveName).toArray(n -> new String[n]);
    }

    @SneakyThrows
    private String resolveName(Lambda lambda) {
        SerializedLambda s = lambda.lambda();
        return Class.forName(s.getImplClass().replace('/', '.')).getSimpleName() + "#" + s.getImplMethodName();
    }

}
