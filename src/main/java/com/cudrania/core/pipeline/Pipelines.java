package com.cudrania.core.pipeline;

import com.cudrania.core.functions.Fn;
import com.cudrania.core.functions.Fn.Lambda;
import lombok.extern.slf4j.Slf4j;

/**
 * 流水线实现,支持单参/双参/三参流水线
 *
 * @param <IN1>
 * @param <IN2>
 * @param <IN3>
 * @param <OUT>
 */
@Slf4j
public class Pipelines<IN1, IN2, IN3, OUT> implements Pipeline<IN1, OUT>, Pipeline2<IN1, IN2, OUT>, Pipeline3<IN1, IN2, IN3, OUT> {
    private Steps<Pipelines<IN1, IN2, IN3, OUT>, OUT, IN1, IN2, IN3> step;
    private PipelineContext context;


    Pipelines(Steps<Pipelines<IN1, IN2, IN3, OUT>, OUT, IN1, IN2, IN3> step) {
        this.step = step;
        this.context = new PipelineContext();
    }

    /**
     * 创建流水线
     *
     * @param <OUT> 输出结果类型
     * @return
     */
    public static <OUT> StepInit<OUT> of() {
        return new Steps<>();
    }


    /**
     * 创建流水线
     *
     * @param outType 输出结果类型
     * @param <OUT>   输出结果类型
     * @return
     */
    public static <OUT> StepInit<OUT> of(Class<OUT> outType) {
        return new Steps<>();
    }

    /**
     * 赋值参数并执行pipeline
     *
     * @param input pipeline入参
     * @return
     */
    @Override
    public OUT eval(IN1 input) {
        return eval0(input);
    }

    /**
     * 赋值参数并执行pipeline
     *
     * @param input1 第一个pipeline入参
     * @param input2 第二个pipeline入参
     * @return
     */
    @Override
    public OUT eval(IN1 input1, IN2 input2) {
        return eval0(input1, input2);
    }

    /**
     * 赋值参数并执行pipeline
     *
     * @param input1 第一个pipeline入参
     * @param input2 第二个pipeline入参
     * @param input3 第三个pipeline入参
     * @return
     */
    @Override
    public OUT eval(IN1 input1, IN2 input2, IN3 input3) {
        return eval0(input1, input2, input3);
    }

    /**
     * 赋值参数并执行pipeline
     *
     * @param inputs
     * @param <R>
     * @return
     */
    private <R> R eval0(Object... inputs) {
        context.setFirst(inputs);
        Steps initStep = this.step.getInitStep();
        String[] inputNames = initStep.getOutputNames();
        //初始步骤的出参作为第一个步骤的入参
        for (int i = 0; i < inputs.length && i < inputNames.length; i++) {
            context.put(inputNames[i], inputs[i]);
        }
        Steps step = initStep.getNextStep();
        while (step != null) {
            doEval(step);
            step = step.getNextStep();
        }
        return (R) context.getLast()[0];
    }


    /**
     * 执行当前步骤
     *
     * @param step
     * @param <R>
     * @return
     */
    private <R> R doEval(Steps step) {
        log.info("==>begin step [{}] with input: {}", step.getDepth(), step.getInputNames());
        String[] inputNames = step.getInputNames();
        Object[] inputValues = new Object[inputNames.length];
        if (inputNames.length == 0) {
            inputValues = context.getLast();
        } else {
            for (int i = 0; i < inputNames.length; i++) {
                String inputName = inputNames[i];
                if (!context.has(inputName)) {
                    throw new IllegalStateException("param not provided: " + inputName);
                }
                inputValues[i] = context.get(inputName);
            }
        }
        Object output;
        try {
            Lambda ability = step.getAbility();
            if (ability instanceof Fn.Function) {
                output = Fn.Function.class.cast(ability).apply(inputValues[0]);
            } else if (ability instanceof Fn.BiFunction) {
                output = Fn.BiFunction.class.cast(ability).apply(inputValues[0], inputValues[1]);
            } else if (ability instanceof Fn.TriFunction) {
                output = Fn.TriFunction.class.cast(ability).apply(inputValues[0], inputValues[1], inputValues[2]);
            } else if (ability instanceof Fn.QuaFunction) {
                output = Fn.QuaFunction.class.cast(ability).apply(inputValues[0], inputValues[1], inputValues[2], inputValues[3]);
            } else if (ability instanceof Fn.QuiFunction) {
                output = Fn.QuiFunction.class.cast(ability).apply(inputValues[0], inputValues[1], inputValues[2], inputValues[3], inputValues[4]);
            } else if (ability instanceof Fn.HexFunction) {
                output = Fn.HexFunction.class.cast(ability).apply(inputValues[0], inputValues[1], inputValues[2], inputValues[3], inputValues[4], inputValues[5]);
            } else if (ability instanceof Fn.HepFunction) {
                output = Fn.HepFunction.class.cast(ability).apply(inputValues[0], inputValues[1], inputValues[2], inputValues[3], inputValues[4], inputValues[5], inputValues[6]);
            } else if (ability instanceof Fn.OctFunction) {
                output = Fn.OctFunction.class.cast(ability).apply(inputValues[0], inputValues[1], inputValues[2], inputValues[3], inputValues[4], inputValues[5], inputValues[6], inputValues[7]);
            } else if (ability instanceof Fn.NonFunction) {
                output = Fn.NonFunction.class.cast(ability).apply(inputValues[0], inputValues[1], inputValues[2], inputValues[3], inputValues[4], inputValues[5], inputValues[6], inputValues[7], inputValues[8]);
            } else if (ability instanceof Fn.DecFunction) {
                output = Fn.DecFunction.class.cast(ability).apply(inputValues[0], inputValues[1], inputValues[2], inputValues[3], inputValues[4], inputValues[5], inputValues[6], inputValues[7], inputValues[8], inputValues[9]);
            } else {
                throw new UnsupportedOperationException("unsupported lambda function!");
            }
        } catch (Exception e) {
            if (step.getErrorHandler() != null) {
                log.warn("==>failover for step [{}], error caused by: ", step.getDepth(), e);
                output = step.getErrorHandler().apply(e);
            } else {
                throw e;
            }
        }
        for (String outputName : step.getOutputNames()) {
            context.put(outputName, output);
        }
        context.setLast(new Object[]{output});
        log.info("==>finish step [{}] with output[{}]: {} ", step.getDepth(), step.getOutputNames(), output);
        return (R) output;
    }


}
