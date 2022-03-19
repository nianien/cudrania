package com.cudrania.core.pipeline;

public interface Pipeline3<IN1, IN2, IN3, OUT> {

    /**
     * 计算pipeline
     *
     * @return
     */
    OUT eval(IN1 input1, IN2 input2, IN3 input3);

}
