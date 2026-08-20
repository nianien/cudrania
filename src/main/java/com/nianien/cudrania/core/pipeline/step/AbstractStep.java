package com.nianien.cudrania.core.pipeline.step;

import com.nianien.cudrania.core.functions.Fn.Function;
import com.nianien.cudrania.core.functions.Fn.Lambda;
import com.nianien.cudrania.core.pipeline.Named;
import lombok.Getter;

import java.util.Arrays;

/**
 * Pipeline 步骤链的运行时节点基类
 * <p>
 * 初始节点作为哨兵节点保存初始参数名称，并临时保存下一步骤通过 {@code with} 选择的输入名称
 * 实际执行节点保存函数、输入名称、输出名称、异常处理函数和下一个节点
 * <pre>{@code
 * initStep -> step1 -> step2 -> step3
 * }</pre>
 * DSL 的泛型状态由 {@link PipelineStep} 负责，本类只维护执行所需的非泛型节点数据
 */
@Getter
public abstract class AbstractStep {

    /**
     * 当前执行节点显式选择的输入名称
     * <p>
     * 空数组表示使用上一执行步骤的输出
     */
    private String[] inputNames = new String[0];

    /**
     * 当前节点输出需要写入执行上下文的名称
     * <p>
     * 初始节点上的名称表示命名初始参数
     */
    private String[] outputNames = new String[0];

    /**
     * 当前执行节点调用的单参、双参或三参函数
     */
    private Lambda ability;

    /**
     * 步骤链中的下一个执行节点
     */
    private AbstractStep nextStep;

    /**
     * 当前步骤链共享的初始哨兵节点
     */
    private AbstractStep initStep;

    /**
     * 当前节点在步骤链中的深度
     * <p>
     * 初始节点深度为 {@code 0}，第一个执行节点深度为 {@code 1}
     */
    private int depth = 0;

    /**
     * 当前执行节点的异常降级函数
     */
    private Function errorHandler;

    /**
     * 创建初始哨兵节点
     */
    public AbstractStep() {
        this.initStep = this;
    }


    /**
     * 创建执行节点并连接到给定前置节点
     * <p>
     * 新节点继承相同初始节点，深度在前置节点基础上加一
     *
     * @param preStep 前置节点
     */
    protected AbstractStep(AbstractStep preStep) {
        this.initStep = preStep.initStep;
        this.depth = preStep.depth + 1;
        preStep.nextStep = this;
    }

    /**
     * 设置当前节点的输出名称
     *
     * @param names 输出名称及类型声明
     * @return 当前节点
     */
    protected AbstractStep outputName(Named<?, ?>... names) {
        this.outputNames = toStrings(names);
        return this;
    }

    /**
     * 设置当前执行节点的异常降级函数
     *
     * @param handler 异常降级函数
     * @return 当前节点
     */
    protected AbstractStep errorHandler(Function handler) {
        this.errorHandler = handler;
        return this;
    }

    /**
     * 暂存下一执行节点通过命名方法引用选择的输入名称
     * <p>
     * 名称暂存在初始节点中，并在 {@link #addAbility(Lambda)} 创建下一节点时被消费
     *
     * @param names 输入名称及类型声明
     * @return 当前节点
     */
    protected AbstractStep inputNames(Named<?, ?>... names) {
        this.initStep.inputNames = toStrings(names);
        return this;
    }

    /**
     * 暂存下一执行节点通过字符串选择的输入名称
     *
     * @param names 上下文键
     * @return 当前节点
     */
    protected AbstractStep inputNames(String... names) {
        this.initStep.inputNames = names;
        return this;
    }

    /**
     * 创建下一执行节点并绑定执行函数
     * <p>
     * 暂存的输入名称会转移到新节点，随后立即从初始节点清除
     *
     * @param ability 新节点调用的函数
     * @return 新创建的执行节点
     */
    protected AbstractStep addAbility(Lambda ability) {
        AbstractStep nextStep = nextStep();
        nextStep.ability = ability;
        //初始步骤用于保存下一步骤的入参, 构建完成之后需要清除
        nextStep.inputNames = this.initStep.inputNames;
        this.initStep.inputNames = new String[0];
        return nextStep;
    }


    /**
     * 创建并连接下一个具体执行节点
     *
     * @return 新创建的执行节点
     */
    public abstract AbstractStep nextStep();

    /**
     * 把命名方法引用转换为执行上下文键
     *
     * @param inputs 命名方法引用
     * @return 按传入顺序排列的上下文键数组
     */
    private String[] toStrings(Named<?, ?>... inputs) {
        return Arrays.stream(inputs).map(Named::name).toArray(n -> new String[n]);
    }

}
