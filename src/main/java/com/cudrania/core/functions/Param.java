package com.cudrania.core.functions;

import java.util.Optional;

/**
 * 条件参数，方法{@link #get()} 返回结果{@link Optional#isPresent()}为true时，参数对象可用
 *
 * @author scorpio
 * @version 1.0.0
 */
public interface Param<T> {

    /**
     * 获取参数值
     *
     * @return
     */
    Optional<T> get();

}
