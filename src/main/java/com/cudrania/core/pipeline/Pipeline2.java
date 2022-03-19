package com.cudrania.core.pipeline;

public interface Pipeline2<IN1, IN2, OUT> {

    /**
     * 计算pipeline
     *
     * @return
     */
    OUT eval(IN1 input1, IN2 input2);

}
