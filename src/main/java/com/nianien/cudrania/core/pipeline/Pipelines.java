package com.nianien.cudrania.core.pipeline;

import com.nianien.cudrania.core.functions.Fn;
import com.nianien.cudrania.core.functions.Fn.Lambda;
import com.nianien.cudrania.core.pipeline.step.AbstractStep;
import com.nianien.cudrania.core.pipeline.step.PipelineStep;
import lombok.extern.slf4j.Slf4j;

/**
 * 单参、双参和三参 Pipeline 的统一构建入口与执行器
 * <p>
 * {@code of} 根据初始参数数量返回对应的 DSL 状态
 * {@code and} 追加执行步骤，{@code as} 保存步骤结果，{@code with} 为下一步骤选择参数
 * {@code failOver} 为当前步骤设置异常降级函数，{@code end} 完成构建
 * <p>
 * 使用命名参数构建带分支复用的流水线
 * <pre>{@code
 * interface Inputs {
 *     Integer left();
 *     Integer right();
 * }
 *
 * interface Results {
 *     Integer sum();
 * }
 *
 * Pipeline2<Integer, Integer, String> pipeline = Pipelines
 *         .of(Inputs::left, Inputs::right)
 *         .and(Integer::sum)
 *         .as(Results::sum)
 *         .with(Results::sum)
 *         .and(value -> "sum:" + value)
 *         .failOver(error -> "unavailable")
 *         .end();
 *
 * String result = pipeline.eval(20, 22);
 * }</pre>
 * 使用 {@link Named} 创建时，初始参数会按名称写入上下文供后续 {@code with} 使用
 * 使用 {@link Class} 创建时只携带编译期类型，初始参数不会获得上下文名称
 * <p>
 * 每次 {@code eval} 都创建独立上下文
 * 流水线构建完成后可重复或并发执行，但调用方提供的函数也必须满足相应线程安全要求
 *
 * @param <IN1> 初始参数1类型
 * @param <IN2> 初始参数2类型，单参流水线中不使用
 * @param <IN3> 初始参数3类型，单参和双参流水线中不使用
 * @param <OUT> 最终结果类型
 */
@Slf4j
public class Pipelines<IN1, IN2, IN3, OUT> implements Pipeline<IN1, OUT>, Pipeline2<IN1, IN2, OUT>, Pipeline3<IN1, IN2, IN3, OUT> {

    /**
     * DSL 构建出的任意步骤，用于定位共享的初始节点
     */
    private AbstractStep step;

    /**
     * 使用已构建的步骤链创建执行器
     * <p>
     * 通常由 DSL 的 {@code end} 方法调用
     *
     * @param step 步骤链中的任意节点
     */
    public Pipelines(AbstractStep step) {
        this.step = step;
    }

    /**
     * 使用一个命名初始参数创建单参 Pipeline DSL
     * <p>
     * 初始值会按 {@link Named#name()} 生成的名称写入执行上下文
     *
     * @param input 初始参数名称及类型声明
     * @param <T>   声明名称的宿主类型
     * @param <IN>  初始参数类型
     * @return 等待处理初始参数的单参 DSL 状态
     */
    public static <T, IN> Pipeline.StepIn<IN, IN> of(Named<T, IN> input) {
        return PipelineStep.begin(input);
    }

    /**
     * 使用一个初始参数类型创建单参 Pipeline DSL
     * <p>
     * 类型对象仅用于泛型推断，不会在运行时校验参数类型
     *
     * @param input 初始参数类型
     * @param <IN>  初始参数类型
     * @return 等待处理初始参数的单参 DSL 状态
     */
    public static <IN> Pipeline.StepIn<IN, IN> of(Class<IN> input) {
        return PipelineStep.begin(input);
    }


    /**
     * 使用两个命名初始参数创建双参 Pipeline DSL
     * <p>
     * 两个初始值都会按各自名称写入执行上下文
     * 第一个 {@code and} 必须接收两个参数
     *
     * @param input1 初始参数1名称及类型声明
     * @param input2 初始参数2名称及类型声明
     * @param <T1>   声明参数1名称的宿主类型
     * @param <T2>   声明参数2名称的宿主类型
     * @param <IN1>  初始参数1类型
     * @param <IN2>  初始参数2类型
     * @return 等待处理两个初始参数的双参 DSL 状态
     */
    public static <T1, T2, IN1, IN2> Pipeline2.StepIn2<IN1, IN2, IN1, IN2> of(Named<T1, IN1> input1, Named<T2, IN2> input2) {
        return PipelineStep.begin(input1, input2);
    }

    /**
     * 使用两个初始参数类型创建双参 Pipeline DSL
     * <p>
     * 类型对象仅用于泛型推断，不会在运行时校验参数类型
     *
     * @param input1 初始参数1类型
     * @param input2 初始参数2类型
     * @param <IN1>  初始参数1类型
     * @param <IN2>  初始参数2类型
     * @return 等待处理两个初始参数的双参 DSL 状态
     */
    public static <IN1, IN2> Pipeline2.StepIn2<IN1, IN2, IN1, IN2> of(Class<IN1> input1, Class<IN2> input2) {
        return PipelineStep.begin(input1, input2);
    }

    /**
     * 使用三个命名初始参数创建三参 Pipeline DSL
     * <p>
     * 三个初始值都会按各自名称写入执行上下文
     * 第一个 {@code and} 必须接收三个参数
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
    public static <T1, T2, T3, IN1, IN2, IN3> Pipeline3.StepIn3<IN1, IN2, IN3, IN1, IN2, IN3> of(Named<T1, IN1> input1, Named<T2, IN2> input2, Named<T3, IN3> input3) {
        return PipelineStep.begin(input1, input2, input3);
    }

    /**
     * 使用三个初始参数类型创建三参 Pipeline DSL
     * <p>
     * 类型对象仅用于泛型推断，不会在运行时校验参数类型
     *
     * @param input1 初始参数1类型
     * @param input2 初始参数2类型
     * @param input3 初始参数3类型
     * @param <IN1>  初始参数1类型
     * @param <IN2>  初始参数2类型
     * @param <IN3>  初始参数3类型
     * @return 等待处理三个初始参数的三参 DSL 状态
     */
    public static <IN1, IN2, IN3> Pipeline3.StepIn3<IN1, IN2, IN3, IN1, IN2, IN3> of(Class<IN1> input1, Class<IN2> input2, Class<IN3> input3) {
        return PipelineStep.begin(input1, input2, input3);
    }

    /**
     * 执行单参流水线
     *
     * @param input 初始参数
     * @return 最后一个步骤的输出
     */
    @Override
    public OUT eval(IN1 input) {
        return eval0(input);
    }

    /**
     * 执行双参流水线
     *
     * @param input1 初始参数1
     * @param input2 初始参数2
     * @return 最后一个步骤的输出
     */
    @Override
    public OUT eval(IN1 input1, IN2 input2) {
        return eval0(input1, input2);
    }

    /**
     * 执行三参流水线
     *
     * @param input1 初始参数1
     * @param input2 初始参数2
     * @param input3 初始参数3
     * @return 最后一个步骤的输出
     */
    @Override
    public OUT eval(IN1 input1, IN2 input2, IN3 input3) {
        return eval0(input1, input2, input3);
    }

    /**
     * 创建执行上下文并按顺序执行步骤链
     * <p>
     * 命名初始参数先写入上下文
     * 每个步骤执行完成后把输出设为最近结果，最后返回最近结果中的第一个值
     *
     * @param inputs 本次执行的全部初始参数
     * @param <R>    调用入口声明的结果类型
     * @return 最后一个步骤的输出，单参无步骤流水线返回原始输入
     */
    private <R> R eval0(Object... inputs) {
        //上下文为每次调用独立创建, 保证Pipelines实例可复用/可重入/线程安全
        PipelineContext context = new PipelineContext();
        context.setFirst(inputs);
        AbstractStep initStep = this.step.getInitStep();
        String[] inputNames = initStep.getOutputNames();
        log.debug("==>init step [{}] with input: {}", 0, inputNames);
        //初始步骤的出参作为第一个步骤的参数
        for (int i = 0; i < inputs.length && i < inputNames.length; i++) {
            context.put(inputNames[i], inputs[i]);
        }
        AbstractStep step = initStep.getNextStep();
        while (step != null) {
            doEval(step, context);
            step = step.getNextStep();
        }
        return (R) context.getLast()[0];
    }


    /**
     * 解析当前步骤的输入并调用对应元数的函数
     * <p>
     * 未显式指定输入名称时使用最近步骤输出
     * 显式指定名称时按声明顺序从上下文读取
     * 函数异常由当前步骤的降级函数处理，没有降级函数时继续向外传播
     * 步骤输出会写入全部输出名称并成为新的最近结果
     *
     * @param step    当前执行步骤
     * @param context 本次调用的执行上下文
     * @param <R>     当前步骤结果类型
     * @return 当前步骤输出或降级函数返回的替代结果
     * @throws IllegalStateException      指定的输入名称不存在时抛出
     * @throws UnsupportedOperationException 步骤函数元数不受支持时抛出
     */
    private <R> R doEval(AbstractStep step, PipelineContext context) {
        log.debug("==>begin step [{}] with input: {}", step.getDepth(), step.getInputNames());
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
            }/* else if (ability instanceof Fn.QuaFunction) {
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
            } */ else {
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
        log.debug("==>finish step [{}] with output{}: {} ", step.getDepth(), step.getOutputNames(), output);
        return (R) output;
    }


}
