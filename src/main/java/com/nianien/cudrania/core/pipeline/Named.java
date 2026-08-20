package com.nianien.cudrania.core.pipeline;

import com.nianien.cudrania.core.functions.Fn.Lambda;
import lombok.SneakyThrows;

import java.lang.invoke.SerializedLambda;

/**
 * 可序列化的命名取值函数
 * <p>
 * Pipeline 使用该类型把方法引用同时作为类型声明和上下文键
 * {@link #name()} 会从序列化 Lambda 中提取实现类与方法名并生成稳定键
 * 键的格式为 {@code 全限定类名#方法名}
 * <p>
 * 推荐使用无参属性方法的方法引用声明名称
 * <pre>{@code
 * interface Inputs {
 *     Long userId();
 * }
 *
 * Named<Inputs, Long> userId = Inputs::userId;
 * String key = userId.name();
 * }</pre>
 * 相同方法引用生成相同名称，可分别用于 {@link Pipelines#of(Named)}、
 * {@code as} 和 {@code with}，从而在多个步骤之间保存并再次选择结果
 *
 * @param <T> 声明名称的宿主类型
 * @param <R> 方法引用表示的值类型
 */
@FunctionalInterface
public interface Named<T, R> extends Lambda {

    /**
     * 调用命名函数
     * <p>
     * Pipeline 只使用方法引用携带的类型和名称，不会在执行过程中调用该方法
     *
     * @param t 函数参数
     * @return 函数结果
     */
    R apply(T t);


    /**
     * 获取当前方法引用对应的上下文键
     *
     * @return 格式为 {@code 全限定类名#方法名} 的名称
     */
    @SneakyThrows
    default String name() {
        SerializedLambda s = this.lambda();
        return Class.forName(s.getImplClass().replace('/', '.')).getName() + "#" + s.getImplMethodName();
    }


    /**
     * 保留传入的命名函数并辅助 Java 完成方法引用类型推断
     *
     * @param lambda 命名函数
     * @param <P>    声明名称的宿主类型
     * @param <R>    方法引用表示的值类型
     * @return 原命名函数
     */
    static <P, R> Named<P, R> of(Named<P, R> lambda) {
        return lambda;
    }
}
