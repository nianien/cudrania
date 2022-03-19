package com.cudrania.core.pipeline;

public interface Pipeline<IN, OUT> {

    /**
     * 计算pipeline
     *
     * @param input
     * @return
     */
    OUT eval(IN input);


}
